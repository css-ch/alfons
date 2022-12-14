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

package ch.css.community.components.appnav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.StateTree;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.server.VaadinService;

import java.util.Optional;

/**
 * A menu item for the {@link AppNav} component.
 * <p>
 * Can contain a label and/or an icon and links to a given {@code path}.
 * </p>
 */
@SuppressWarnings("UnusedReturnValue") // fluent API
@JsModule("@vaadin-component-factory/vcf-nav")
@Tag("vcf-nav-item")
public class AppNavItem extends Component {

    /**
     * Creates a new menu item using the given label and icon that links to the
     * given path.
     *
     * @param label
     *            the label for the item
     * @param view
     *            the view to link to
     * @param iconClass
     *            the CSS class to use for showing the icon
     */
    public AppNavItem(final String label, final Class<? extends Component> view, final String iconClass) {
        setPath(view);
        setLabel(label);

        setIconClass(iconClass);
    }

    /**
     * Set a textual label for the item.
     * <p>
     * The label is also available for screen rader users.
     *
     * @param label
     *            the label to set
     * @return this instance for chaining
     */
    public AppNavItem setLabel(final String label) {
        getLabelElement().setText(label);
        return this;
    }

    private Optional<Element> getExistingLabelElement() {
        return getElement().getChildren().filter(child -> !child.hasAttribute("slot")).findFirst();
    }

    private Element getLabelElement() {
        return getExistingLabelElement().orElseGet(() -> {
            Element element = Element.createText("");
            getElement().appendChild(element);
            return element;
        });
    }

    /**
     * Sets the path this item links to.
     *
     * @param path
     *            the path to link to
     * @return this instance for chaining
     */
    public AppNavItem setPath(final String path) {
        getElement().setAttribute("path", path);
        return this;
    }

    /**
     * Sets the view this item links to.
     *
     * @param view
     *            the view to link to
     * @return this instance for chaining
     */
    public AppNavItem setPath(final Class<? extends Component> view) {
        String url = RouteConfiguration.forRegistry(getRouter().getRegistry()).getUrl(view);
        setPath(url);
        return this;
    }

    private Router getRouter() {
        Router router = null;
        if (getElement().getNode().isAttached()) {
            StateTree tree = (StateTree) getElement().getNode().getOwner();
            router = tree.getUI().getInternals().getRouter();
        }
        if (router == null) {
            router = VaadinService.getCurrent().getRouter();
        }
        if (router == null) {
            throw new IllegalStateException("Implicit router instance is not available. "
                    + "Use overloaded method with explicit router parameter.");
        }
        return router;
    }

    /**
     * Gets the path this item links to.
     *
     * @return the path to link to
     */
    public String getPath() {
        return getElement().getAttribute("path");
    }

    private int getIconElementIndex() {
        for (int i = 0; i < getElement().getChildCount(); i++) {
            if ("prefix".equals(getElement().getChild(i).getAttribute("slot"))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Sets the icon for the item.
     * <p>
     * Can also be used to set a custom component to be shown in front of the label.
     *
     * @param icon
     *            the icon to show
     * @return this instance for chaining
     */
    public AppNavItem setIcon(final Component icon) {
        icon.getElement().setAttribute("slot", "prefix");
        int iconElementIndex = getIconElementIndex();
        if (iconElementIndex != -1) {
            getElement().setChild(iconElementIndex, icon.getElement());
        } else {
            getElement().appendChild(icon.getElement());
        }
        return this;
    }

    /**
     * Sets the icon using a CSS class for the item.
     * <p>
     * Can also be used to set a custom component to be shown in front of the label.
     * </p>
     *
     * @param iconClass
     *            the CSS class to use for showing the icon
     * @return this instance for chaining
     */
    public AppNavItem setIconClass(final String iconClass) {
        Span icon = new Span();
        icon.setClassName(iconClass);
        setIcon(icon);
        return this;
    }
}
