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

import ch.fihlon.alfons.data.db.tables.records.RegistrationRecord;
import ch.fihlon.alfons.data.entity.Employee;
import ch.fihlon.alfons.data.entity.RegistrationListEntity;
import ch.fihlon.alfons.data.service.getter.DSLContextGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.DSL;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.Optional;
import java.util.stream.Stream;

import static ch.fihlon.alfons.data.db.tables.Conference.CONFERENCE;
import static ch.fihlon.alfons.data.db.tables.Employee.EMPLOYEE;
import static ch.fihlon.alfons.data.db.tables.Registration.REGISTRATION;
import static org.jooq.impl.DSL.concat;

interface RegistrationService extends DSLContextGetter {

    default RegistrationRecord newRegistration(@Nullable final Employee employee) {
        final var registration = dsl().newRecord(REGISTRATION);
        if (employee != null) {
            registration.setEmployeeId(employee.getId());
        }
        return registration;
    }

    default Stream<RegistrationListEntity> findRegistrations(
            final int offset, final int limit, @Nullable final String filter) {
        final var filterValue = filter == null || filter.isBlank() ? null : "%" + filter.trim() + "%";
        return dsl().select(EMPLOYEE.ID, EMPLOYEE.FIRST_NAME, EMPLOYEE.LAST_NAME,
                        CONFERENCE.ID, CONFERENCE.NAME, CONFERENCE.WEBSITE,
                        REGISTRATION.REGISTRATION_DATE, REGISTRATION.ROLE, REGISTRATION.REASON,
                        REGISTRATION.STATUS, REGISTRATION.STATUS_DATE, REGISTRATION.STATUS_COMMENT)
                .from(REGISTRATION)
                .leftJoin(EMPLOYEE).on(REGISTRATION.EMPLOYEE_ID.eq(EMPLOYEE.ID))
                .leftJoin(CONFERENCE).on(REGISTRATION.CONFERENCE_ID.eq(CONFERENCE.ID))
                .where(filterValue == null ? DSL.noCondition()
                        : concat(EMPLOYEE.FIRST_NAME, DSL.value(" "), EMPLOYEE.LAST_NAME).like(filterValue)
                                .or(CONFERENCE.NAME.like(filterValue)))
                .orderBy(REGISTRATION.REGISTRATION_DATE.desc().nullsFirst())
                .offset(offset)
                .limit(limit)
                .fetchInto(RegistrationListEntity.class)
                .stream();
    }

    default Optional<RegistrationRecord> getRegistrationRecord(@NotNull final Long employeeId, @NotNull final Long conferenceId) {
        return dsl().selectFrom(REGISTRATION)
                .where(REGISTRATION.EMPLOYEE_ID.eq(employeeId).and(REGISTRATION.CONFERENCE_ID.eq(conferenceId)))
                .fetchOptional();
    }

    default void deleteRegistration(final long employeeId, final long conferenceId) {
        getRegistrationRecord(employeeId, conferenceId).ifPresent(UpdatableRecordImpl::delete);
    }
}
