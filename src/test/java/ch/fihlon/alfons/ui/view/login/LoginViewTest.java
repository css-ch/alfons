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

package ch.fihlon.alfons.ui.view.login;

import ch.fihlon.alfons.ui.KaribuTest;
import com.github.mvysny.kaributools.RouterUtilsKt;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginViewTest extends KaribuTest {

    @Test
    void loginView() {
        RouterUtilsKt.navigateTo("/login");
        final var loginView = _get(LoginView.class);
        assertTrue(loginView.isVisible());
    }

}
