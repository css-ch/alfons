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

package ch.fihlon.alfons.ui.component;

import com.vaadin.flow.component.datepicker.DatePicker;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

import static ch.fihlon.alfons.util.FormatterUtil.DATE_PATTERN;

public class CustomDatePicker extends DatePicker {

    @Serial
    private static final long serialVersionUID = 8502404630576201105L;

    public CustomDatePicker(@NotNull final String label) {
        super(label);
        final var i18n = new CustomDatePickerI18n();
        i18n.setDateFormat(DATE_PATTERN);
        this.setI18n(i18n);
        this.setWeekNumbersVisible(true);
    }

}
