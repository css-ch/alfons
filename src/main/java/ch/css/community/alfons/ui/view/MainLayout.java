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

package ch.css.community.alfons.ui.view;


import ch.css.community.alfons.data.db.enums.UserTheme;
import ch.css.community.alfons.ui.view.about.AboutView;
import ch.css.community.alfons.ui.view.conference.ConferencesView;
import ch.css.community.alfons.ui.view.registration.RegistrationsView;
import ch.css.community.alfons.ui.view.settings.SettingsView;
import ch.css.community.alfons.util.GravatarUtil;
import ch.css.community.alfons.components.appnav.AppNav;
import ch.css.community.alfons.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Locale;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport(value = "./themes/alfons/main-layout.css")
public final class MainLayout extends AppLayout {

    @Serial
    private static final long serialVersionUID = -3101364083072426857L;

    private H2 viewTitle;
    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(@NotNull final AuthenticatedUser authenticatedUser,
                      @NotNull final AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();

        authenticatedUser.get().ifPresent(user -> UI.getCurrent().getElement().setAttribute("theme", user.getTheme().getLiteral()));
    }

    private void addHeaderContent() {
        final var layout = new HorizontalLayout();
        layout.setId("header");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        final var toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        layout.add(toggle);
        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        layout.add(viewTitle);
        layout.add(createAvatarMenu());

        addToNavbar(true, toggle, layout);
    }

    private MenuBar createAvatarMenu() {
        final var menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        final var menuItem = menuBar.addItem(createAvatar());
        final var subMenu = menuItem.getSubMenu();
        final var darkThemeItem = subMenu.addItem("Dark Theme");
        final var lightThemeItem = subMenu.addItem("Light Theme");
        subMenu.add(new Hr());
        subMenu.addItem("Logout", e -> authenticatedUser.logout());

        darkThemeItem.setCheckable(true);
        lightThemeItem.setCheckable(true);
        authenticatedUser.get().ifPresent(user -> {
            switch (user.getTheme()) {
                case dark -> darkThemeItem.setChecked(true);
                case light -> lightThemeItem.setChecked(true);
                default -> throw new IllegalStateException("Unexpected value: " + user.getTheme());
            }
        });

        darkThemeItem.addClickListener(clickEvent -> {
            authenticatedUser.get().ifPresent(user -> {
                user.setTheme(UserTheme.dark);
                user.store();
            });
            UI.getCurrent().getElement().setAttribute("theme", "dark");
            lightThemeItem.setChecked(false);
        });

        lightThemeItem.addClickListener(clickEvent -> {
            authenticatedUser.get().ifPresent(user -> {
                user.setTheme(UserTheme.light);
                user.store();
            });
            UI.getCurrent().getElement().setAttribute("theme", "light");
            darkThemeItem.setChecked(false);
        });

        return menuBar;
    }

    private Avatar createAvatar() {
        final var user = authenticatedUser.get().orElse(null);
        if (user != null) {
            final var avatar = new Avatar(String.format("%s %s", user.getFirstName(), user.getLastName()));
            avatar.setImage(GravatarUtil.getGravatarAddress(user.getEmail().toLowerCase(Locale.getDefault())));
            avatar.getStyle().set("cursor", "pointer");
            return avatar;
        } else {
            return new Avatar();
        }
    }

    private void addDrawerContent() {
        final var appName = new H1("Alfons");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        final var header = new Header(appName);
        final var scroller = new Scroller(createNavigation());
        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        final var nav = new AppNav();

        final var views = new ArrayList<MainMenuItem>();
        views.add(new MainMenuItem("Registrations", RegistrationsView.class, "la la-users"));
        views.add(new MainMenuItem("Conferences", ConferencesView.class, "la la-university"));
        views.add(new MainMenuItem("Settings", SettingsView.class, "la la-cog"));
        views.add(new MainMenuItem("About", AboutView.class, "la la-info-circle"));

        views.forEach(mainMenuItem -> {
            if (accessChecker.hasAccess(mainMenuItem.view())) {
                nav.addItem(mainMenuItem.toAppNavItem());
            }
        });

        return nav;
    }

    private Footer createFooter() {
        return new Footer();
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        final var title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
