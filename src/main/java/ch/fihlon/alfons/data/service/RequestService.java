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

import ch.fihlon.alfons.data.db.tables.records.RequestRecord;
import ch.fihlon.alfons.data.entity.Employee;
import ch.fihlon.alfons.data.entity.RequestListEntity;
import ch.fihlon.alfons.data.service.getter.DSLContextGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.DSL;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.Optional;
import java.util.stream.Stream;

import static ch.fihlon.alfons.data.db.tables.Conference.CONFERENCE;
import static ch.fihlon.alfons.data.db.tables.Employee.EMPLOYEE;
import static ch.fihlon.alfons.data.db.tables.Request.REQUEST;
import static org.jooq.impl.DSL.concat;

interface RequestService extends DSLContextGetter {

    default RequestRecord newRequestRecord(@Nullable final Employee employee) {
        final var requestRecord = dsl().newRecord(REQUEST);
        if (employee != null) {
            requestRecord.setEmployeeId(employee.getId());
        }
        return requestRecord;
    }

    default Stream<RequestListEntity> findRequest(
            final int offset, final int limit, @Nullable final String filter) {
        final var filterValue = filter == null || filter.isBlank() ? null : "%" + filter.trim() + "%";
        return dsl().select(EMPLOYEE.ID, EMPLOYEE.FIRST_NAME, EMPLOYEE.LAST_NAME,
                        CONFERENCE.ID, CONFERENCE.NAME, CONFERENCE.WEBSITE,
                        REQUEST.REQUEST_DATE, REQUEST.ROLE, REQUEST.REASON,
                        REQUEST.STATUS, REQUEST.STATUS_DATE, REQUEST.STATUS_COMMENT)
                .from(REQUEST)
                .leftJoin(EMPLOYEE).on(REQUEST.EMPLOYEE_ID.eq(EMPLOYEE.ID))
                .leftJoin(CONFERENCE).on(REQUEST.CONFERENCE_ID.eq(CONFERENCE.ID))
                .where(filterValue == null ? DSL.noCondition()
                        : concat(EMPLOYEE.FIRST_NAME, DSL.value(" "), EMPLOYEE.LAST_NAME).like(filterValue)
                                .or(CONFERENCE.NAME.like(filterValue)))
                .orderBy(REQUEST.REQUEST_DATE.desc().nullsFirst())
                .offset(offset)
                .limit(limit)
                .fetchInto(RequestListEntity.class)
                .stream();
    }

    default Optional<RequestRecord> getRequestRecord(@NotNull final Long employeeId, @NotNull final Long conferenceId) {
        return dsl().selectFrom(REQUEST)
                .where(REQUEST.EMPLOYEE_ID.eq(employeeId).and(REQUEST.CONFERENCE_ID.eq(conferenceId)))
                .fetchOptional();
    }

    default void deleteRequest(final long employeeId, final long conferenceId) {
        getRequestRecord(employeeId, conferenceId).ifPresent(UpdatableRecordImpl::delete);
    }
}
