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

import ch.css.community.alfons.data.entity.User;
import ch.css.community.alfons.data.service.DatabaseService;
import ch.css.community.alfons.data.entity.MailTemplateId;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public final class SecurityService implements UserDetailsService {

    private final DatabaseService databaseService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUser authenticatedUser;
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest request;

    public SecurityService(@NotNull final DatabaseService databaseService,
                           @NotNull final PasswordEncoder passwordEncoder,
                           @NotNull final AuthenticatedUser authenticatedUser,
                           @NotNull final LoginAttemptService loginAttemptService,
                           @NotNull final HttpServletRequest request) {
        this.databaseService = databaseService;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUser = authenticatedUser;
        this.loginAttemptService = loginAttemptService;
        this.request = request;
    }

    @Override
    public UserDetails loadUserByUsername(@NotNull final String email) throws UsernameNotFoundException {
        if (loginAttemptService.isBlocked(getClientIP())) {
            throw new RuntimeException("Too many failed login attempts, IP address blocked for 24 hours!");
        }

        final var optionalUser = databaseService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("No user present with email: " + email);
        } else {
            final var user = optionalUser.get();
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(), getAuthorities(user));
        }
    }

    private static List<GrantedAuthority> getAuthorities(@NotNull final User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());

    }

    public void resetPassword(@NotNull final String email) {
        final var user = databaseService.getUserByEmail(email);
        if (user.isPresent()) {
            final var record = user.get();
            final var password = RandomStringUtils.randomAscii(32).replaceAll("\\s", "_");
            final var passwordHash = passwordEncoder.encode(password);
            record.setPasswordHash(passwordHash);
            record.setPasswordChange(true);
            record.store();

            databaseService.sendMail(MailTemplateId.SECURITY_RESET_PASSWORD, Map.of("password", password), email);
        }
    }

    public void changePassword(@NotNull final String oldPassword,
                               @NotNull final String newPassword) {
        final var member = authenticatedUser.get()
                .orElseThrow(() -> new InsufficientAuthenticationException("Password change denied!"));
        if (passwordEncoder.matches(oldPassword, member.getPasswordHash())) {
            final var newPasswordHash = passwordEncoder.encode(newPassword);
            member.setPasswordHash(newPasswordHash);
            member.setPasswordChange(false);
            member.store();
        } else {
            throw new BadCredentialsException("Password change denied!");
        }
    }

    public String getClientIP() {
        final var xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
