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

package ch.css.community.alfons.ui.view.settings;

import ch.css.community.alfons.data.entity.Role;
import ch.css.community.alfons.data.service.DatabaseService;
import ch.css.community.alfons.ui.component.ResizableView;
import ch.css.community.alfons.ui.view.MainLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.RolesAllowed;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Route(value = "settings", layout = MainLayout.class)
@RouteAlias(value = "settings/:id", layout = MainLayout.class)
@PageTitle("Alfons Settings")
@CssImport(value = "./themes/alfons/views/settings-view.css")
@CssImport(value = "./themes/alfons/views/alfons-dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
@RolesAllowed(Role.Type.ADMIN)
public final class SettingsView extends ResizableView implements BeforeEnterObserver {

    private static final String ANCHOR_PREFIX = "settings/";
    @Serial
    private static final long serialVersionUID = -2617826959693372843L;

    private final DatabaseService databaseService;
    private final List<Tab> settingTabs;
    private final Div content;
    private final Tabs tabs;

    public SettingsView(@NotNull final DatabaseService databaseService) {
        this.databaseService = databaseService;
        addClassNames("settings-view", "flex", "flex-col", "h-full");
        settingTabs = new ArrayList<>();

        content = new Div();
        content.addClassName("tab-content");

        final var configuration = new Tab(new Anchor(ANCHOR_PREFIX + "configuration", "Configuration"));
        configuration.setId("configuration");
        settingTabs.add(configuration);

        final var mailTemplates = new Tab(new Anchor(ANCHOR_PREFIX + "mail-templates", "Mail templates"));
        mailTemplates.setId("mail-templates");
        settingTabs.add(mailTemplates);

        tabs = new Tabs(settingTabs.toArray(new Tab[0]));

        add(tabs, content);
    }

    @Override
    public void beforeEnter(@NotNull final BeforeEnterEvent beforeEnterEvent) {
        final var params = beforeEnterEvent.getRouteParameters();
        final var tabId = params.get("id").orElse("");
        final var tabToSelect = getTab(tabId);
        tabs.setSelectedTab(tabToSelect);
        setContent(tabToSelect);
    }

    private Tab getTab(@NotNull final String tabId) {
        return settingTabs.stream()
                .filter(tab -> tabId.equals(tab.getId().orElse("")))
                .findFirst()
                .orElse(settingTabs.get(0));
    }

    private void setContent(@NotNull final Tab tab) {
        content.removeAll();
        final var tabId = tab.getId().orElse("");
        final var tabContent = switch (tabId) {
            case "configuration" -> new ConfigurationSetting(databaseService);
            case "mail-templates" -> new MailTemplateSetting(databaseService);
            default -> new Paragraph("This setting has not been implemented yet!");
        };
        content.add(tabContent);
    }

}
