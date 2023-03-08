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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

class CustomDatePickerI18n extends DatePicker.DatePickerI18n {

    @Serial
    private static final long serialVersionUID = 9123662826442023086L;

    CustomDatePickerI18n() {
        this(UI.getCurrent().getSession().getBrowser().getLocale());
    }

    CustomDatePickerI18n(@NotNull final Locale locale) {
        final var symbols = new DateFormatSymbols(locale);
        this.setMonthNames(Arrays.asList(symbols.getMonths()));
        this.setFirstDayOfWeek(Calendar.getInstance(locale).getFirstDayOfWeek());
        this.setWeekdays(Arrays.stream(symbols.getWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        this.setWeekdaysShort(Arrays.stream(symbols.getShortWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
    }
}
