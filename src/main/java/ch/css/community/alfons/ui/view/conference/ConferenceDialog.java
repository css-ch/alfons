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

package ch.css.community.alfons.ui.view.conference;

import ch.css.community.alfons.data.db.tables.records.ConferenceRecord;
import ch.css.community.alfons.ui.component.CustomDatePicker;
import ch.css.community.alfons.ui.component.EditDialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.Objects;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

public final class ConferenceDialog extends EditDialog<ConferenceRecord> {

    @Serial
    private static final long serialVersionUID = 8013889745455047755L;

    public ConferenceDialog(@NotNull final String title) {
        super(title);
    }

    @Override
    public void createForm(@NotNull final FormLayout formLayout, @NotNull final Binder<ConferenceRecord> binder) {
        final var name = new TextField("Name");
        final var beginDate = new CustomDatePicker("Begin date");
        final var endDate = new CustomDatePicker("End date");
        final var website = new TextField("Website");
        final var ticket = new IntegerField("Ticket");
        final var travel = new IntegerField("Travel");
        final var accommodation = new IntegerField("Accommodation");

        name.setRequiredIndicatorVisible(true);
        name.setValueChangeMode(EAGER);

        beginDate.setRequiredIndicatorVisible(true);
        endDate.setRequiredIndicatorVisible(true);
        endDate.addFocusListener(event -> {
            if (endDate.isEmpty() && !beginDate.isInvalid()) {
                endDate.setValue(beginDate.getValue());
            }
        });

        website.setRequiredIndicatorVisible(true);
        website.setValueChangeMode(EAGER);

        ticket.setRequiredIndicatorVisible(true);
        ticket.setPrefixComponent(new Div(new Text("CHF")));
        travel.setRequiredIndicatorVisible(true);
        travel.setPrefixComponent(new Div(new Text("CHF")));
        accommodation.setRequiredIndicatorVisible(true);
        accommodation.setPrefixComponent(new Div(new Text("CHF")));

        formLayout.add(name, beginDate, endDate, website, ticket, travel, accommodation);

        binder.forField(name)
                .withValidator(new StringLengthValidator(
                        "Please enter the name of the conference (max. 255 chars)", 1, 255))
                .bind(ConferenceRecord::getName, ConferenceRecord::setName);

        binder.forField(beginDate)
                .withValidator(value -> value != null
                                && (endDate.isEmpty() || value.isBefore(endDate.getValue()) || value.isEqual(endDate.getValue())),
                        "The begin date must be before the end date or they must be the same (1-day-conference)")
                .bind(ConferenceRecord::getBeginDate, ConferenceRecord::setBeginDate);

        binder.forField(endDate)
                .withValidator(value -> value != null
                                && (beginDate.isEmpty() || value.isEqual(beginDate.getValue()) || value.isAfter(beginDate.getValue())),
                        "The end date must be after the begin date or they must be the same (1-day-conference)")
                .bind(ConferenceRecord::getEndDate, ConferenceRecord::setEndDate);

        binder.forField(website)
                .withValidator(value -> value.startsWith("https://"),
                        "The website address must start with \"https://\"")
                .withValidator(new StringLengthValidator(
                        "The website address is too long (max. 255 chars)", 0, 255))
                .bind(ConferenceRecord::getWebsite, ConferenceRecord::setWebsite);

        binder.forField(ticket)
                .withValidator(Objects::nonNull,
                        "Please enter the ticket price for the conference (minimum 0)")
                .withValidator(new IntegerRangeValidator(
                        "Please enter the ticket price for the conference (minimum 0)", 0, Integer.MAX_VALUE))
                .bind(ConferenceRecord::getTicket, ConferenceRecord::setTicket);

        binder.forField(travel)
                .withValidator(Objects::nonNull,
                        "Please enter the travel expenses for the conference (minimum 0)")
                .withValidator(new IntegerRangeValidator(
                        "Please enter the travel expenses for the conference (minimum 0)", 0, Integer.MAX_VALUE))
                .bind(ConferenceRecord::getTravel, ConferenceRecord::setTravel);

        binder.forField(accommodation)
                .withValidator(Objects::nonNull,
                        "Please enter the accommodation costs for the conference (minimum 0)")
                .withValidator(new IntegerRangeValidator(
                        "Please enter the accommodation costs for the conference (minimum 0)", 0, Integer.MAX_VALUE))
                .bind(ConferenceRecord::getAccommodation, ConferenceRecord::setAccommodation);
    }
}
