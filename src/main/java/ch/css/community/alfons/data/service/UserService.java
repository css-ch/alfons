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

import ch.css.community.alfons.data.entity.User;
import ch.css.community.alfons.data.service.getter.DSLContextGetter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

import static ch.css.community.alfons.data.db.tables.User.USER;

interface UserService extends DSLContextGetter {

    default Optional<User> getUserByEmail(@NotNull final String email) {
        return dsl().selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .limit(1)
                .fetchOptionalInto(User.class);
    }

    default Stream<User> getAllUsers() {
        return dsl().selectFrom(USER)
                .orderBy(USER.FIRST_NAME, USER.LAST_NAME)
                .fetchInto(User.class)
                .stream();
    }

}
