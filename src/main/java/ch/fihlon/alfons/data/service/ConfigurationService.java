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

package ch.fihlon.alfons.data.service;

import ch.fihlon.alfons.data.db.tables.Configuration;
import ch.fihlon.alfons.data.db.tables.records.ConfigurationRecord;
import ch.fihlon.alfons.data.service.getter.DSLContextGetter;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.DSL;

import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;

interface ConfigurationService extends DSLContextGetter {

    default ConfigurationRecord newConfiguration() {
        return dsl().newRecord(Configuration.CONFIGURATION);
    }

    default Stream<ConfigurationRecord> findConfiguration(final int offset, final int limit, @Nullable final String filter) {
        final var filterValue = filter == null || filter.isBlank() ? null : "%" + filter.trim() + "%";
        return dsl().selectFrom(Configuration.CONFIGURATION)
                .where(filterValue == null ? DSL.noCondition()
                        : Configuration.CONFIGURATION.KEY.like(filterValue).or(Configuration.CONFIGURATION.VALUE.like(filterValue)))
                .orderBy(Configuration.CONFIGURATION.KEY)
                .offset(offset)
                .limit(limit)
                .stream();
    }

    default ch.fihlon.alfons.configuration.Configuration loadConfigurationFromDatabase() {
        final var configurationData = new HashMap<String, String>();
        dsl().selectFrom(Configuration.CONFIGURATION)
                .forEach(configurationRecord -> configurationData.put(configurationRecord.getKey(), configurationRecord.getValue()));
        return new ch.fihlon.alfons.configuration.Configuration(Collections.unmodifiableMap(configurationData));
    }

}
