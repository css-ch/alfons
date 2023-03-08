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

package ch.css.community.alfons.security;

import ch.css.community.alfons.ui.view.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.security.SecureRandom;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    public static final String LOGIN_URL = "login";
    public static final String LOGOUT_URL = "/";

    /**
     * Get a password encoder.
     * @return a password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    /**
     * @see VaadinWebSecurity#configure(HttpSecurity)
     */
    @Override
    protected void configure(@NotNull final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers(
                // Client-side JS
                new AntPathRequestMatcher("/VAADIN/**"),

                // the standard favicon URI
                new AntPathRequestMatcher("/favicon.ico"),

                // the robots exclusion standard
                new AntPathRequestMatcher("/robots.txt"),

                // web application manifest
                new AntPathRequestMatcher("/manifest.webmanifest"),
                new AntPathRequestMatcher("/sw.js"),
                new AntPathRequestMatcher("/offline.html"),

                // icons and images
                new AntPathRequestMatcher("/icons/**"),
                new AntPathRequestMatcher("/images/**"),
                new AntPathRequestMatcher("/styles/**"),

                // (development mode) H2 debugging console
                new AntPathRequestMatcher("/h2-console/**")
        ).permitAll();

        super.configure(http);

        setLoginView(http, LoginView.class, LOGOUT_URL);
    }
}
