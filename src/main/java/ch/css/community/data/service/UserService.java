/*
 * Komunumo - Open Source Community Manager
 * Copyright (C) Marcus Fihlon and the individual contributors to Komunumo.
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

package ch.css.community.data.service;

import ch.css.community.data.entity.User;
import ch.css.community.data.service.getter.DSLContextGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.DSL;

import java.util.Optional;
import java.util.stream.Stream;

import static ch.css.community.alfons.data.db.tables.User.USER;
import static org.jooq.impl.DSL.concat;

interface UserService extends DSLContextGetter {

    default User newUser() {
        final var user = dsl().newRecord(USER)
                .into(User.class);
        user.setFirstName("");
        user.setLastName("");
        user.setEmail("");
        user.setAdmin(false);
        return user;
    }

    default Stream<User> findUsers(final int offset, final int limit, @Nullable final String filter) {
        final var filterValue = filter == null || filter.isBlank() ? null : "%" + filter.trim() + "%";
        return dsl().select(USER.asterisk())
                .from(USER)
                .where(
                    filterValue == null ? DSL.noCondition()
                    : concat(concat(USER.FIRST_NAME, " "), USER.LAST_NAME).like(filterValue)
                            .or(USER.EMAIL.like(filterValue)))
                .orderBy(USER.FIRST_NAME, USER.LAST_NAME)
                .offset(offset)
                .limit(limit)
                .fetchInto(User.class)
                .stream();
    }

    default Optional<User> getUser(@NotNull final Long id) {
        return dsl().selectFrom(USER)
                .where(USER.ID.eq(id))
                .fetchOptionalInto(User.class);
    }

    default Optional<User> getUserByEmail(@NotNull final String email) {
        return dsl().selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .limit(1)
                .fetchOptionalInto(User.class);
    }

    default Stream<User> getAllAdmins() {
        return dsl().selectFrom(USER)
                .where(USER.ADMIN.isTrue())
                .orderBy(USER.FIRST_NAME, USER.LAST_NAME)
                .fetchInto(User.class)
                .stream();
    }

}
