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

package ch.css.community.configuration;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public final class Configuration {

    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    private final Map<String, String> configuration;

    public Configuration(@NotNull final Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public String getWebsiteBaseUrl() {
        return configuration.getOrDefault("website.url", "http://localhost:8080");
    }
    public String getEmailSenderAddress() {
        return configuration.getOrDefault("email.sender.address", "noreply@localhost");
    }

}
