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

import ch.fihlon.alfons.data.entity.Employee;
import ch.fihlon.alfons.data.service.getter.DSLContextGetter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

import static ch.fihlon.alfons.data.db.tables.Employee.EMPLOYEE;

interface EmployeeService extends DSLContextGetter {

    default Optional<Employee> getEmployeeByEmail(@NotNull final String email) {
        return dsl().selectFrom(EMPLOYEE)
                .where(EMPLOYEE.EMAIL.eq(email))
                .limit(1)
                .fetchOptionalInto(Employee.class);
    }

    default Stream<Employee> getAllEmployees() {
        return dsl().selectFrom(EMPLOYEE)
                .orderBy(EMPLOYEE.FIRST_NAME, EMPLOYEE.LAST_NAME)
                .fetchInto(Employee.class)
                .stream();
    }

}
