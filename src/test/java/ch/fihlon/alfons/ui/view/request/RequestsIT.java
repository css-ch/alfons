package ch.fihlon.alfons.ui.view.request;

import ch.fihlon.alfons.data.entity.RequestListEntity;
import ch.fihlon.alfons.data.entity.Role;
import ch.fihlon.alfons.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.github.mvysny.kaributools.RouterUtilsKt;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
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
        assertEquals("Test Conference 2", GridKt._get(grid, 0).conferenceName());
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
        assertEquals("Test Conference 2", GridKt._get(grid, 0).conferenceName());
        assertEquals("Test Conference 1", GridKt._get(grid, 1).conferenceName());
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
}
