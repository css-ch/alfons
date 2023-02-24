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

import ch.css.community.alfons.data.db.enums.RegistrationRole;
import ch.css.community.alfons.data.db.enums.RegistrationStatus;

import java.time.LocalDateTime;

public record RegistrationListEntity(Long userId, String userFirstName, String userLastName,
                                     Long conferenceId, String conferenceName, String conferenceWebsite,
                                     LocalDateTime registrationDate, RegistrationRole registrationRole, String registrationReason,
                                     RegistrationStatus status, LocalDateTime statusDate, String statusComment) { }
