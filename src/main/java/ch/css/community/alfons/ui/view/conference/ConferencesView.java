/*
 * Alfons - Make Community Management Great Again
 * Copyright (C) Marcus Fihlon and the individual contributors to Alfons.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.css.community.alfons.ui.view.conference;

import ch.css.community.alfons.data.entity.Conference;
import ch.css.community.alfons.data.entity.Role;
import ch.css.community.alfons.data.service.DatabaseService;
import ch.css.community.alfons.ui.component.EnhancedButton;
import ch.css.community.alfons.ui.component.FilterField;
import ch.css.community.alfons.ui.component.ResizableView;
import ch.css.community.alfons.ui.view.MainLayout;
import com.opencsv.CSVWriter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.Serial;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

import static ch.css.community.alfons.util.FormatterUtil.formatDate;
import static java.nio.charset.StandardCharsets.UTF_8;

@Route(value = "conferences", layout = MainLayout.class)
@PageTitle("Conferences")
@CssImport(value = "./themes/alfons/views/conferences-view.css")
@CssImport(value = "./themes/alfons/views/alfons-dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
@RolesAllowed(Role.Type.USER)
public final class ConferencesView extends ResizableView implements HasUrlParameter<String> {

    @Serial
    private static final long serialVersionUID = -5938974936125304046L;
    private final DatabaseService databaseService;
    private final TextField filterField;
    private final Grid<Conference> grid;

    public ConferencesView(@NotNull final DatabaseService databaseService) {
        this.databaseService = databaseService;

        addClassNames("conferences-view", "flex", "flex-col", "h-full");

        grid = new Grid<>();
        configureGrid();
        filterField = new FilterField();
        filterField.addValueChangeListener(event -> reloadConferences());
        filterField.setTitle("Filter conferences by name");

        final var newConferenceButton = new EnhancedButton(new Icon(VaadinIcon.FILE_ADD), clickEvent -> showConferenceDialog(null));
        newConferenceButton.setTitle("Add a new conference");

        final var refreshConferencesButton = new EnhancedButton(new Icon(VaadinIcon.REFRESH), clickEvent -> reloadConferences());
        refreshConferencesButton.setTitle("Refresh the list of conferences");

        final var downloadConferencesButton = new EnhancedButton(new Icon(VaadinIcon.DOWNLOAD), clickEvent -> downloadConferences());
        downloadConferencesButton.setTitle("Download the list of conferences");

        final var optionBar = new HorizontalLayout(filterField, newConferenceButton, refreshConferencesButton, downloadConferencesButton);
        optionBar.setPadding(true);

        add(optionBar, grid);
        reloadConferences();
        filterField.focus();
    }


    @Override
    public void setParameter(@NotNull final BeforeEvent beforeEvent,
                             @Nullable @OptionalParameter final String parameter) {
        final var location = beforeEvent.getLocation();
        final var queryParameters = location.getQueryParameters();
        final var parameters = queryParameters.getParameters();
        final var filterValue = parameters.getOrDefault("filter", List.of("")).get(0);
        filterField.setValue(filterValue);
    }

    private void configureGrid() {
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        final var today = LocalDate.now();
        grid.addColumn(LitRenderer.<Conference>of(
                "<a style=\"font-weight: ${item.fontWeight};\" href=\"${item.website}\" target=\"_blank\">${item.name}</a>")
                .withProperty("fontWeight", (conference) ->
                        conference.beginDate() != null && conference.beginDate().isBefore(today) ? "normal" : "bold")
                .withProperty("name", Conference::name)
                .withProperty("website", Conference::website))
                .setHeader("Name").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(conference -> formatDate(conference.beginDate()))
                .setHeader("Begin Date")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setKey("beginDate");
        grid.addColumn(conference -> formatDate(conference.endDate()))
                .setHeader("End Date")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setKey("endDate");

        grid.addColumn(new ComponentRenderer<>(conference -> {
            final var editButton = new EnhancedButton(new Icon(VaadinIcon.EDIT), clickEvent -> showConferenceDialog(conference));
            editButton.setTitle("Edit this conference");
            final var deleteButton = new EnhancedButton(new Icon(VaadinIcon.TRASH), clickEvent -> deleteConference(conference));
            deleteButton.setTitle("Delete this conference");
            return new HorizontalLayout(editButton, deleteButton);
        }))
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.setHeightFull();
    }

    @Override
    protected void onResize(final int width) {
        grid.getColumnByKey("beginDate").setVisible(width >= 1000);
        grid.getColumnByKey("endDate").setVisible(width >= 1200);
    }

    private void showConferenceDialog(@Nullable final Conference conference) {
        final var conferenceRecord = conference == null || conference.id() == null ? databaseService.newConference()
                : databaseService.getConferenceRecord(conference.id()).orElse(databaseService.newConference());
        final var dialog = new ConferenceDialog(conferenceRecord.getId() != null ? "Edit Conference" : "New Conference");
        dialog.open(conferenceRecord, this::reloadConferences);
    }

    private void deleteConference(final Conference conference) {
        new ConfirmDialog("Confirm deletion",
                String.format("Are you sure you want to permanently delete the conference \"%s\"?", conference.name()),
                "Delete", dialogEvent -> {
            databaseService.deleteConference(conference.id());
            reloadConferences();
            dialogEvent.getSource().close();
        },
                "Cancel", dialogEvent -> dialogEvent.getSource().close()
        ).open();
    }

    private void reloadConferences() {
        grid.setItems(query -> databaseService.findConferences(query.getOffset(), query.getLimit(), filterField.getValue()));
    }

    private void downloadConferences() {
        final var resource = new StreamResource("conferences.csv", () -> {
            final var stringWriter = new StringWriter();
            final var csvWriter = new CSVWriter(stringWriter);
            csvWriter.writeNext(new String[] {
                    "ID", "Name", "Website", "Begin Date", "End Date"
            });
            grid.getGenericDataView()
                    .getItems().map(conference -> new String[] {
                    conference.id().toString(),
                    conference.name(),
                    conference.website(),
                    formatDate(conference.beginDate()),
                    formatDate(conference.endDate())
            }).forEach(csvWriter::writeNext);
            return new ByteArrayInputStream(stringWriter.toString().getBytes(UTF_8));
        });
        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        UI.getCurrent().getPage().setLocation(registration.getResourceUri());
    }
}
