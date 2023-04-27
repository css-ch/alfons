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

package ch.fihlon.alfons.ui.view.about;

import ch.fihlon.alfons.data.entity.Role;
import ch.fihlon.alfons.ui.KaribuTest;
import com.github.mvysny.kaributools.RouterUtilsKt;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._find;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AboutIT extends KaribuTest {

    @Test
    void aboutView() {
        login("test@localhost", "password", List.of(Role.USER));
        RouterUtilsKt.navigateTo("/about");
        final var aboutView = _get(AboutView.class);
        assertEquals("Alfons", _get(aboutView, Image.class).getAlt().orElseThrow());
        assertEquals("Alfons v0.0.0-TEST", _get(aboutView, H2.class).getText());
        assertEquals("Make Community Management Great Again", _get(aboutView, H3.class).getText());
        assertNotNull(_find(Anchor.class).stream().filter(a -> a.getText().contains("License")).findFirst().orElse(null));
        assertNotNull(_find(Anchor.class).stream().filter(a -> a.getText().contains("Copyright")).findFirst().orElse(null));
    }

}
