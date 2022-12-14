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

package ch.css.community.alfons.data.entity;

import ch.css.community.alfons.data.db.tables.records.UserRecord;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

public class User extends UserRecord {

    @Serial
    private static final long serialVersionUID = -3933831578443623455L;

    /**
     * Get the full name (first and last) of the member.
     * @return full name
     */
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName()).trim();
    }

    /**
     * Get the roles of the member.
     * @return a set of roles (maybe empty)
     */
    public Set<Role> getRoles() {
        final var roles = new HashSet<Role>();
        roles.add(Role.USER);
        if (getAdmin()) {
            roles.add(Role.ADMIN);
        }
        return Set.copyOf(roles);
    }

}
