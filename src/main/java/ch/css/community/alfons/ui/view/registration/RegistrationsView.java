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

package ch.css.community.alfons.ui.view.registration;

import ch.css.community.alfons.data.entity.RegistrationListEntity;
import ch.css.community.alfons.data.entity.Role;
import ch.css.community.alfons.data.service.DatabaseService;
import ch.css.community.alfons.security.AuthenticatedEmployee;
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
import java.util.List;

import static ch.css.community.alfons.util.FormatterUtil.formatDateTime;
import static java.nio.charset.StandardCharsets.UTF_8;

@Route(value = "registrations", layout = MainLayout.class)
@PageTitle("Registrations")
@CssImport(value = "./themes/alfons/views/registrations-view.css")
@CssImport(value = "./themes/alfons/views/alfons-dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
@RolesAllowed(Role.Type.USER)
public final class RegistrationsView extends ResizableView implements HasUrlParameter<String> {

    @Serial
    private static final long serialVersionUID = 5432174661071333245L;
    private final DatabaseService databaseService;
    private final AuthenticatedEmployee authenticatedEmployee;
    private final TextField filterField;
    private final Grid<RegistrationListEntity> grid;

    public RegistrationsView(@NotNull final DatabaseService databaseService,
                             @NotNull final AuthenticatedEmployee authenticatedEmployee) {
        this.databaseService = databaseService;
        this.authenticatedEmployee = authenticatedEmployee;

        addClassNames("registrations-view", "flex", "flex-col", "h-full");

        grid = new Grid<>();
        configureGrid();
        filterField = new FilterField();
        filterField.addValueChangeListener(event -> reloadRegistrations());
        filterField.setTitle("Filter registrations by conference or employee");

        final var newRegistrationButton = new EnhancedButton(new Icon(VaadinIcon.FILE_ADD), clickEvent -> showRegistrationDialog(null));
        newRegistrationButton.setTitle("Add a new registration");

        final var refreshRegistrationsButton = new EnhancedButton(new Icon(VaadinIcon.REFRESH), clickEvent -> reloadRegistrations());
        refreshRegistrationsButton.setTitle("Refresh the list of registrations");

        final var downloadRegistrationsButton = new EnhancedButton(new Icon(VaadinIcon.DOWNLOAD), clickEvent -> downloadRegistrations());
        downloadRegistrationsButton.setTitle("Download the list of registrations");

        final var optionBar = new HorizontalLayout(filterField, newRegistrationButton, refreshRegistrationsButton, downloadRegistrationsButton);
        optionBar.setPadding(true);

        add(optionBar, grid);
        reloadRegistrations();
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

        grid.addColumn(LitRenderer.<RegistrationListEntity>of("${item.firstName} ${item.lastName}")
                .withProperty("firstName", RegistrationListEntity::employeeFirstName)
                .withProperty("lastName", RegistrationListEntity::employeeLastName))
                .setHeader("Employee").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(LitRenderer.<RegistrationListEntity>of("<a href=\"${item.website}\" target=\"_blank\">${item.conference}</a>")
                .withProperty("conference", RegistrationListEntity::conferenceName)
                .withProperty("website", RegistrationListEntity::conferenceWebsite))
                .setHeader("Conference").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(registrationListEntity -> formatDateTime(registrationListEntity.registrationDate()))
                .setHeader("Date")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(RegistrationListEntity::status)
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(registrationListEntity -> {
            final var editButton = new EnhancedButton(new Icon(VaadinIcon.EDIT), clickEvent -> showRegistrationDialog(registrationListEntity));
            editButton.setTitle("Edit this registration");
            final var deleteButton = new EnhancedButton(new Icon(VaadinIcon.TRASH), clickEvent -> deleteRegistration(registrationListEntity));
            deleteButton.setTitle("Delete this registration");
            return new HorizontalLayout(editButton, deleteButton);
        }))
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.setHeightFull();
    }

    private void showRegistrationDialog(@Nullable final RegistrationListEntity registrationListEntity) {
        final var registrationRecord = registrationListEntity == null ? databaseService.newRegistration()
                : databaseService.getRegistrationRecord(registrationListEntity.employeeId(), registrationListEntity.conferenceId())
                .orElse(databaseService.newRegistration());
        final var dialog = new RegistrationDialog(registrationRecord.getEmployeeId() != null
                ? "Edit Registration" : "New Registration", databaseService, authenticatedEmployee);
        dialog.open(registrationRecord, this::reloadRegistrations);
    }

    private void deleteRegistration(@NotNull final RegistrationListEntity registrationListEntity) {
        new ConfirmDialog("Confirm deletion",
                String.format("Are you sure you want to permanently delete the registration of \"%s %s\" for \"%s\"?",
                        registrationListEntity.employeeFirstName(), registrationListEntity.employeeLastName(),
                        registrationListEntity.conferenceName()),
                "Delete", dialogEvent -> {
            databaseService.deleteRegistration(registrationListEntity.employeeId(), registrationListEntity.conferenceId());
            reloadRegistrations();
            dialogEvent.getSource().close();
        },
                "Cancel", dialogEvent -> dialogEvent.getSource().close()
        ).open();
    }

    private void reloadRegistrations() {
        grid.setItems(query -> databaseService.findRegistrations(query.getOffset(), query.getLimit(), filterField.getValue()));
    }

    private void downloadRegistrations() {
        final var resource = new StreamResource("registrations.csv", () -> {
            final var stringWriter = new StringWriter();
            final var csvWriter = new CSVWriter(stringWriter);
            csvWriter.writeNext(new String[] {
                    "Employee ID", "Employee First Name", "Employee Last Name",
                    "Conference ID", "Conference Name", "Conference Website",
                    "Registration Date", "Registration Role", "Registration Reason",
                    "Status", "Status Date", "Status Comment"
            });
            grid.getGenericDataView()
                    .getItems().map(registrationListEntity -> new String[] {
                            registrationListEntity.employeeId().toString(),
                            registrationListEntity.employeeFirstName(),
                            registrationListEntity.employeeLastName(),
                            registrationListEntity.conferenceId().toString(),
                            registrationListEntity.conferenceName(),
                            registrationListEntity.conferenceWebsite(),
                            formatDateTime(registrationListEntity.registrationDate()),
                            registrationListEntity.registrationRole().toString(),
                            registrationListEntity.registrationReason(),
                            registrationListEntity.status().toString(),
                            formatDateTime(registrationListEntity.statusDate()),
                            registrationListEntity.statusComment()
            }).forEach(csvWriter::writeNext);
            return new ByteArrayInputStream(stringWriter.toString().getBytes(UTF_8));
        });
        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        UI.getCurrent().getPage().setLocation(registration.getResourceUri());
    }
}
