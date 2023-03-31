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

package ch.fihlon.alfons.ui.view.request;

import ch.fihlon.alfons.data.db.enums.RequestStatus;
import ch.fihlon.alfons.data.entity.Employee;
import ch.fihlon.alfons.data.entity.RequestListEntity;
import ch.fihlon.alfons.data.entity.Role;
import ch.fihlon.alfons.data.service.DatabaseService;
import ch.fihlon.alfons.security.AuthenticatedEmployee;
import ch.fihlon.alfons.ui.component.EnhancedButton;
import ch.fihlon.alfons.ui.component.FilterField;
import ch.fihlon.alfons.ui.component.ResizableView;
import ch.fihlon.alfons.ui.view.MainLayout;
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
import jakarta.annotation.security.RolesAllowed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.Serial;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.List;

import static ch.fihlon.alfons.util.FormatterUtil.formatDateTime;
import static java.nio.charset.StandardCharsets.UTF_8;

@Route(value = "requests", layout = MainLayout.class)
@PageTitle("Requests")
@CssImport(value = "./themes/alfons/views/requests-view.css")
@CssImport(value = "./themes/alfons/views/alfons-dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
@RolesAllowed(Role.Type.USER)
public final class RequestsView extends ResizableView implements HasUrlParameter<String> {

    @Serial
    private static final long serialVersionUID = 5432174661071333245L;
    private final DatabaseService databaseService;
    private final Employee user;
    private final TextField filterField;
    private final Grid<RequestListEntity> grid;

    public RequestsView(@NotNull final DatabaseService databaseService,
                        @NotNull final AuthenticatedEmployee authenticatedEmployee) {
        this.databaseService = databaseService;
        this.user = authenticatedEmployee.get().orElseThrow();

        addClassNames("requests-view", "flex", "flex-col", "h-full");

        grid = new Grid<>();
        configureGrid();
        filterField = new FilterField();
        filterField.addValueChangeListener(event -> reloadRequests());
        filterField.setTitle("Filter requests by conference or employee");

        final var newRequestButton = new EnhancedButton(new Icon(VaadinIcon.FILE_ADD), clickEvent -> showRequestDialog(null));
        newRequestButton.setTitle("Add a new request");

        final var refreshRequestsButton = new EnhancedButton(new Icon(VaadinIcon.REFRESH), clickEvent -> reloadRequests());
        refreshRequestsButton.setTitle("Refresh the list of requests");

        final var downloadRequestsButton = new EnhancedButton(new Icon(VaadinIcon.DOWNLOAD), clickEvent -> downloadRequests());
        downloadRequestsButton.setTitle("Download the list of requests");

        final var optionBar = new HorizontalLayout(filterField, newRequestButton, refreshRequestsButton, downloadRequestsButton);
        optionBar.setPadding(true);

        add(optionBar, grid);
        reloadRequests();
        filterField.focus();
    }


    @Override
    public void setParameter(@NotNull final BeforeEvent beforeEvent,
                             @Nullable @OptionalParameter final String parameter) {
        final var location = beforeEvent.getLocation();
        final var queryParameters = location.getQueryParameters();
        final var parameters = queryParameters.getParameters();
        final var filterDefault = user.getAdmin() ? "" : user.getFullName();
        final var filterValue = parameters.getOrDefault("filter", List.of(filterDefault)).get(0);
        filterField.setValue(filterValue);
    }

    private void configureGrid() {
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(LitRenderer.<RequestListEntity>of("${item.firstName} ${item.lastName}")
                .withProperty("firstName", RequestListEntity::employeeFirstName)
                .withProperty("lastName", RequestListEntity::employeeLastName))
                .setHeader("Employee").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(LitRenderer.<RequestListEntity>of("<a href=\"/requests?filter=${item.filterValue}\">${item.conference}</a>")
                .withProperty("conference", RequestListEntity::conferenceName)
                .withProperty("filterValue", request -> URLEncoder.encode(request.conferenceName(), UTF_8)))
                .setHeader("Conference").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(requestListEntity -> formatDateTime(requestListEntity.requestDate()))
                .setHeader("Request Date")
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.addColumn(RequestListEntity::status)
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(requestListEntity -> {
            final var editButton = new EnhancedButton(new Icon(VaadinIcon.EDIT), clickEvent -> showRequestDialog(requestListEntity));
            editButton.setTitle("Edit this request");
            final var deleteButton = new EnhancedButton(new Icon(VaadinIcon.TRASH), clickEvent -> deleteRequest(requestListEntity));
            deleteButton.setTitle("Delete this request");
            deleteButton.setEnabled(requestListEntity.status().equals(RequestStatus.submitted)
                    && (user.getId().equals(requestListEntity.employeeId()) || user.getAdmin()));
            return new HorizontalLayout(editButton, deleteButton);
        }))
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.setHeightFull();
    }

    private void showRequestDialog(@Nullable final RequestListEntity requestListEntity) {
        final var requestRecord = requestListEntity == null ? databaseService.newRequestRecord(user)
                : databaseService.getRequestRecord(requestListEntity.employeeId(), requestListEntity.conferenceId())
                .orElse(databaseService.newRequestRecord(user));
        final var dialog = new RequestDialog(requestRecord.getConferenceId() != null
                ? "Edit Request" : "New Request", databaseService);
        dialog.open(requestRecord, this::reloadRequests);
    }

    private void deleteRequest(@NotNull final RequestListEntity requestListEntity) {
        new ConfirmDialog("Confirm deletion",
                String.format("Are you sure you want to permanently delete the request from \"%s %s\" for \"%s\"?",
                        requestListEntity.employeeFirstName(), requestListEntity.employeeLastName(),
                        requestListEntity.conferenceName()),
                "Delete", dialogEvent -> {
            databaseService.deleteRequest(requestListEntity.employeeId(), requestListEntity.conferenceId());
            reloadRequests();
            dialogEvent.getSource().close();
        },
                "Cancel", dialogEvent -> dialogEvent.getSource().close()
        ).open();
    }

    private void reloadRequests() {
        grid.setItems(query -> databaseService.findRequest(query.getOffset(), query.getLimit(), filterField.getValue()));
    }

    private void downloadRequests() {
        final var resource = new StreamResource("requests.csv", () -> {
            final var stringWriter = new StringWriter();
            final var csvWriter = new CSVWriter(stringWriter);
            csvWriter.writeNext(new String[] {
                    "Employee ID", "Employee First Name", "Employee Last Name",
                    "Conference ID", "Conference Name", "Conference Website",
                    "Request Date", "Request Role", "Request Reason",
                    "Status", "Status Date", "Status Comment"
            });
            grid.getGenericDataView()
                    .getItems().map(requestListEntity -> new String[] {
                            requestListEntity.employeeId().toString(),
                            requestListEntity.employeeFirstName(),
                            requestListEntity.employeeLastName(),
                            requestListEntity.conferenceId().toString(),
                            requestListEntity.conferenceName(),
                            requestListEntity.conferenceWebsite(),
                            formatDateTime(requestListEntity.requestDate()),
                            requestListEntity.requestRole().toString(),
                            requestListEntity.requestReason(),
                            requestListEntity.status().toString(),
                            formatDateTime(requestListEntity.statusDate()),
                            requestListEntity.statusComment()
            }).forEach(csvWriter::writeNext);
            return new ByteArrayInputStream(stringWriter.toString().getBytes(UTF_8));
        });
        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        UI.getCurrent().getPage().setLocation(registration.getResourceUri());
    }
}
