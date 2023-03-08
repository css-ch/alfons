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

import ch.fihlon.alfons.security.LoginAttemptService;
import ch.fihlon.alfons.security.SecurityService;
import ch.fihlon.alfons.ui.view.about.AboutView;
import ch.fihlon.alfons.security.AuthenticatedEmployee;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

@Route(value = "login")
@PageTitle("Login")
@AnonymousAllowed
public final class LoginView extends LoginOverlay implements BeforeEnterObserver {

    @Serial
    private static final long serialVersionUID = 3136950494923039756L;
    private final AuthenticatedEmployee authenticatedEmployee;

    public LoginView(@NotNull final AuthenticatedEmployee authenticatedEmployee,
                     @NotNull final SecurityService securityService,
                     @NotNull final LoginAttemptService loginAttemptService) {
        this.authenticatedEmployee = authenticatedEmployee;
        setAction("login");

        final var i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Alfons");
        i18n.getHeader().setDescription("Make Community Management Great Again");
        i18n.setAdditionalInformation(null);

        i18n.getForm().setSubmit("Login");
        i18n.getForm().setTitle("Login");
        i18n.getForm().setUsername("Email");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setForgotPassword("I forgot my password");

        if (loginAttemptService.isBlocked(securityService.getClientIP())) {
            i18n.getErrorMessage().setTitle("IP address blocked for 24 hours");
            i18n.getErrorMessage().setMessage("Too many failed login attempts from your IP address. Try again in 24 hours or later.");
        } else {
            i18n.getErrorMessage().setTitle("Incorrect email or password");
            i18n.getErrorMessage().setMessage("Check that you have entered the correct email and password and try again.");
        }

        setI18n(i18n);

        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(event -> UI.getCurrent().getPage().executeJs(
                "var field = document.getElementById('vaadinLoginUsername'); if (field !== null) { return field.value; } else { return null; }")
                .then(String.class, email -> {
                            if (email.isBlank()) {
                                Notification.show("Please enter your email address first.");
                                UI.getCurrent().getPage().executeJs(
                                        "var field = document.getElementById('vaadinLoginUsername'); if (field !== null) { field.focus(); }");
                            } else {
                                securityService.resetPassword(email);
                                Notification.show("Please check your email account for further instructions.");
                                UI.getCurrent().getPage().executeJs(
                                        "var field = document.getElementById('vaadinLoginPassword'); if (field !== null) { field.focus(); }");
                            }
                        }
                ));

        UI.getCurrent().getPage().executeJs(
                "var field = document.getElementById('vaadinLoginUsername'); if (field !== null) { field.focus(); }");
    }

    @Override
    public void beforeEnter(@NotNull final BeforeEnterEvent event) {
        if (authenticatedEmployee.get().isPresent()) {
            event.forwardTo(AboutView.class);
        } else {
            setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
            setOpened(true);
        }
    }

}
