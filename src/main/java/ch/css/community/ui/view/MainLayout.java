package ch.css.community.ui.view;


import ch.css.community.components.appnav.AppNav;
import ch.css.community.components.appnav.AppNavItem;
import ch.css.community.security.AuthenticatedUser;
import ch.css.community.ui.view.about.AboutView;
import ch.css.community.ui.view.conference.ConferencesView;
import ch.css.community.ui.view.settings.SettingsView;
import ch.css.community.util.GravatarUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private final AuthenticatedUser authenticatedUser;

    public MainLayout(@NotNull final AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        if (authenticatedUser.get().isPresent()) {
            final var avatarMenu = createAvatarMenu();
            final var avatarMenuContainer = new HorizontalLayout();
            avatarMenuContainer.add(avatarMenu);
            avatarMenuContainer.setSizeFull();
            avatarMenuContainer.setAlignItems(FlexComponent.Alignment.END);
            avatarMenuContainer.setAlignSelf(FlexComponent.Alignment.END, avatarMenu);
            addToNavbar(true, toggle, viewTitle, avatarMenuContainer);
        } else {
            addToNavbar(true, toggle, viewTitle);
        }
    }

    private MenuBar createAvatarMenu() {
        final var menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        menuBar.setOpenOnHover(true);

        final var menuItem = menuBar.addItem(createAvatar());
        final var subMenu = menuItem.getSubMenu();
        subMenu.addItem("Logout", e -> authenticatedUser.logout());

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
        H1 appName = new H1("Alfons");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        nav.addItem(new AppNavItem("Conferences", ConferencesView.class, "la la-university"));
        nav.addItem(new AppNavItem("Settings", SettingsView.class, "la la-cog"));
        nav.addItem(new AppNavItem("About", AboutView.class, "la la-info-circle"));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
