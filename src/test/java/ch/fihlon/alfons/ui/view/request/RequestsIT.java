package ch.fihlon.alfons.ui.view.request;

import ch.fihlon.alfons.data.db.enums.RequestRole;
import ch.fihlon.alfons.data.db.tables.records.ConferenceRecord;
import ch.fihlon.alfons.data.entity.Employee;
import ch.fihlon.alfons.data.entity.RequestListEntity;
import ch.fihlon.alfons.data.entity.Role;
import ch.fihlon.alfons.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.ComboBoxKt;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.github.mvysny.kaributools.RouterUtilsKt;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assertDisabled;
import static com.github.mvysny.kaributesting.v10.LocatorJ._assertEnabled;
import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestsIT extends KaribuTest {

    @Test
    void userOnlySeesOwnRequestsByDefault() {
        login("jane.doe@localhost", "user", List.of(Role.USER));
        RouterUtilsKt.navigateTo("/requests");
        final var requestsView = _get(RequestsView.class);
        final var filterField = _get(requestsView, TextField.class, spec -> spec.withPlaceholder("Filter"));
        assertEquals("Jane Doe", filterField.getValue());

        @SuppressWarnings("unchecked") final Grid<RequestListEntity> grid = _get(requestsView, Grid.class);
        GridKt.expectRows(grid, 1);
        assertEquals("Test Conference 1", GridKt._get(grid, 0).conferenceName());
    }

    @Test
    void userCanSeeAllRequestsOrderedByDate() {
        login("jane.doe@localhost", "user", List.of(Role.USER));
        RouterUtilsKt.navigateTo("/requests");
        final var requestsView = _get(RequestsView.class);
        final var filterField = _get(requestsView, TextField.class, spec -> spec.withPlaceholder("Filter"));
        filterField.clear();

        @SuppressWarnings("unchecked") final Grid<RequestListEntity> grid = _get(requestsView, Grid.class);
        GridKt.expectRows(grid, 2);
        assertEquals("Test Conference 1", GridKt._get(grid, 0).conferenceName());
        assertEquals("Jane", GridKt._get(grid, 0).employeeFirstName());
        assertEquals("Test Conference 1", GridKt._get(grid, 1).conferenceName());
        assertEquals("John", GridKt._get(grid, 1).employeeFirstName());
    }

    @Test
    void adminSeesAllRequestsByDefault() {
        login("john.doe@localhost", "admin", List.of(Role.USER, Role.ADMIN));
        RouterUtilsKt.navigateTo("/requests");
        final var requestsView = _get(RequestsView.class);
        final var filterField = _get(requestsView, TextField.class, spec -> spec.withPlaceholder("Filter"));
        assertEquals("", filterField.getValue());

        @SuppressWarnings("unchecked") final Grid<RequestListEntity> grid = _get(requestsView, Grid.class);
        GridKt.expectRows(grid, 2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void addAndDeleteRequest() {
        login("jane.doe@localhost", "user", List.of(Role.USER));
        RouterUtilsKt.navigateTo("/requests");
        final var requestsView = _get(RequestsView.class);

        @SuppressWarnings("unchecked") final Grid<RequestListEntity> grid = _get(requestsView, Grid.class);
        GridKt.expectRows(grid, 1);

        _get(requestsView, Button.class, spec -> spec.withIcon(VaadinIcon.FILE_ADD)).click();
        final var requestDialog = _get(RequestDialog.class);
        final var saveButton = _get(requestDialog, Button.class, spec -> spec.withCaption("Save"));
        _assertDisabled(saveButton);

        final var employeeField = (ComboBox<Employee>) _get(requestDialog, ComboBox.class, spec -> spec.withCaption("Employee"));
        assertEquals("Jane Doe", employeeField.getValue().getFullName());

        final var conferenceField = (ComboBox<ConferenceRecord>) _get(requestDialog, ComboBox.class, spec -> spec.withCaption("Conference"));
        ComboBoxKt.selectByLabel(conferenceField, "Test Conference 2");
        assertEquals("Test Conference 2", conferenceField.getValue().getName());
        _assertDisabled(saveButton);

        final var requestRoleField = (ComboBox<RequestRole>) _get(requestDialog, ComboBox.class, spec -> spec.withCaption("Role"));
        ComboBoxKt.selectByLabel(requestRoleField, "Attendee");
        assertEquals(RequestRole.attendee, requestRoleField.getValue());
        _assertDisabled(saveButton);


        final var reasonField = _get(requestDialog, TextArea.class, spec -> spec.withCaption("Reason"));
        _setValue(reasonField, "This is too short");
        _assertDisabled(saveButton);
        _setValue(reasonField, "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam.");
        _assertEnabled(saveButton);

        _click(saveButton);

        GridKt.expectRows(grid, 2);
        assertEquals("Test Conference 2", GridKt._get(grid, 0).conferenceName());
        assertEquals("Test Conference 1", GridKt._get(grid, 1).conferenceName());

        final var actionButtons = (HorizontalLayout) GridKt._getCellComponent(grid, 0, "actions");
        final var deleteButton = _get(actionButtons, Button.class, spec -> spec.withIcon(VaadinIcon.TRASH));
        _assertEnabled(deleteButton);
        _click(deleteButton);
        _fireConfirm(_get(ConfirmDialog.class));

        GridKt.expectRows(grid, 1);
    }
}
