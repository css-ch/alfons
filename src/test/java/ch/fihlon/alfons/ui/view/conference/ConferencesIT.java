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
package ch.fihlon.alfons.ui.view.conference;

import ch.fihlon.alfons.data.entity.Conference;
import ch.fihlon.alfons.data.entity.Role;
import ch.fihlon.alfons.ui.KaribuTest;
import ch.fihlon.alfons.ui.component.CustomDatePicker;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.github.mvysny.kaributools.RouterUtilsKt;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assertDisabled;
import static com.github.mvysny.kaributesting.v10.LocatorJ._assertEnabled;
import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConferencesIT extends KaribuTest {

    @Test
    void showTwoConferencesOrderedByDateDesc() {
        login("jane.doe@localhost", "user", List.of(Role.USER));
        RouterUtilsKt.navigateTo("/conferences");
        final var conferencesView = _get(ConferencesView.class);

        @SuppressWarnings("unchecked") final Grid<Conference> grid = _get(conferencesView, Grid.class);
        GridKt.expectRows(grid, 2);
        assertEquals("Test Conference 2", GridKt._get(grid, 0).name());
        assertEquals("Test Conference 1", GridKt._get(grid, 1).name());
    }

    @Test
    void filterConferencesByName() {
        login("jane.doe@localhost", "user", List.of(Role.USER));
        RouterUtilsKt.navigateTo("/conferences");
        final var conferencesView = _get(ConferencesView.class);

        @SuppressWarnings("unchecked") final Grid<Conference> grid = _get(conferencesView, Grid.class);
        GridKt.expectRows(grid, 2);

        _get(conferencesView, TextField.class, spec -> spec.withPlaceholder("Filter")).setValue("Test Conference 1");
        GridKt.expectRows(grid, 1);
        assertEquals("Test Conference 1", GridKt._get(grid, 0).name());

        _get(conferencesView, TextField.class, spec -> spec.withPlaceholder("Filter")).setValue("Test Conference 2");
        GridKt.expectRows(grid, 1);
        assertEquals("Test Conference 2", GridKt._get(grid, 0).name());

        _get(conferencesView, TextField.class, spec -> spec.withPlaceholder("Filter")).setValue("");
        GridKt.expectRows(grid, 2);
    }

    @Test
    void addAndDeleteConference() {
        login("jane.doe@localhost", "user", List.of(Role.USER));
        RouterUtilsKt.navigateTo("/conferences");
        final var conferencesView = _get(ConferencesView.class);

        @SuppressWarnings("unchecked") final Grid<Conference> grid = _get(conferencesView, Grid.class);
        GridKt.expectRows(grid, 2);

        _get(conferencesView, Button.class, spec -> spec.withIcon(VaadinIcon.FILE_ADD)).click();
        final var conferenceDialog = _get(ConferenceDialog.class);
        final var saveButton = _get(conferenceDialog, Button.class, spec -> spec.withCaption("Save"));
        _assertDisabled(saveButton);
        _get(conferenceDialog, TextField.class, spec -> spec.withCaption("Name")).setValue("Test Conference 3");
        _assertDisabled(saveButton);
        _get(conferenceDialog, CustomDatePicker.class, spec -> spec.withCaption("Begin date")).setValue(LocalDate.now());
        _assertDisabled(saveButton);
        _get(conferenceDialog, CustomDatePicker.class, spec -> spec.withCaption("End date")).setValue(LocalDate.now().plusDays(1));
        _assertDisabled(saveButton);
        _get(conferenceDialog, TextField.class, spec -> spec.withCaption("Website")).setValue("https://localhost/");
        _assertDisabled(saveButton);
        _get(conferenceDialog, IntegerField.class, spec -> spec.withCaption("Ticket")).setValue(100);
        _assertDisabled(saveButton);
        _get(conferenceDialog, IntegerField.class, spec -> spec.withCaption("Travel")).setValue(50);
        _assertDisabled(saveButton);
        _get(conferenceDialog, IntegerField.class, spec -> spec.withCaption("Accommodation")).setValue(150);
        _assertEnabled(saveButton);
        _click(saveButton);

        GridKt.expectRows(grid, 3);
        assertEquals("Test Conference 2", GridKt._get(grid, 0).name());
        assertEquals("Test Conference 3", GridKt._get(grid, 1).name());
        assertEquals("Test Conference 1", GridKt._get(grid, 2).name());

        final var actionButtons = (HorizontalLayout) GridKt._getCellComponent(grid, 1, "actions");
        final var deleteButton = _get(actionButtons, Button.class, spec -> spec.withIcon(VaadinIcon.TRASH));
        _assertEnabled(deleteButton);
        _click(deleteButton);
        _fireConfirm(_get(ConfirmDialog.class));

        GridKt.expectRows(grid, 2);
    }

}
