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

package ch.css.community.alfons.data.service;

import ch.css.community.alfons.configuration.Configuration;
import ch.css.community.alfons.data.service.getter.ConfigurationGetter;
import ch.css.community.alfons.data.service.getter.DSLContextGetter;
import ch.css.community.alfons.data.service.getter.MailSenderGetter;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class DatabaseService implements ConferenceService, ConfigurationGetter, ConfigurationService, DSLContextGetter,
        MailSenderGetter, MailService, MailTemplateService, RegistrationService, UserService {

    private final DSLContext dsl;
    private final MailSender mailSender;

    private Configuration configuration;

    public DatabaseService(@NotNull final DSLContext dsl,
                           @NotNull final MailSender mailSender) {
        this.dsl = dsl;
        this.mailSender = mailSender;
        this.configuration = loadConfigurationFromDatabase();
    }

    /**
     * Reload the configuration from the database.
     */
    public void reloadConfiguration() {
        configuration = loadConfigurationFromDatabase();
    }

    /**
     * Get the {@link Configuration}.
     * @return the {@link Configuration}
     */
    @Override
    public Configuration configuration() {
        return configuration;
    }

    /**
     * Get the {@link DSLContext} to access the database.
     * @return the {@link DSLContext}
     */
    @Override
    public DSLContext dsl() {
        return dsl;
    }

    /**
     * Get the {@link MailSender} to send emails.
     * @return the {@link MailSender}
     */
    @Override
    public MailSender mailSender() {
        return mailSender;
    }

}
