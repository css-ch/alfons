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

package ch.css.community.alfons.components.appnav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

import java.io.Serial;

/**
 * A navigation menu with support for hierarchical and flat menus.
 * <p>
 * Items can be added using {@link #addItem(AppNavItem...)} and hierarchy can be
 * created by adding {@link AppNavItem} instances to other {@link AppNavItem}
 * instances.
 * </p>
 */
@JsModule("@vaadin-component-factory/vcf-nav")
@Tag("vcf-nav")
public class AppNav extends Component implements HasSize, HasStyle {

    @Serial
    private static final long serialVersionUID = -1887902015133627331L;

    /**
     * Creates a new menu without any label.
     */
    public AppNav() {
    }

    /**
     * Adds menu item(s) to the menu.
     *
     * @param appNavItems
     *            the menu item(s) to add
     * @return the menu for chaining
     */
    public AppNav addItem(final AppNavItem... appNavItems) {
        for (AppNavItem appNavItem : appNavItems) {
            getElement().appendChild(appNavItem.getElement());
        }

        return this;
    }
}
