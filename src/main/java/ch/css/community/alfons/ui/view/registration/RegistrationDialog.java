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

package ch.css.community.alfons.ui.view.registration;

import ch.css.community.alfons.data.db.enums.RegistrationRole;
import ch.css.community.alfons.data.db.tables.records.RegistrationRecord;
import ch.css.community.alfons.data.entity.Conference;
import ch.css.community.alfons.data.entity.Employee;
import ch.css.community.alfons.data.service.DatabaseService;
import ch.css.community.alfons.ui.component.EditDialog;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.Objects;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

public final class RegistrationDialog extends EditDialog<RegistrationRecord> {

    @Serial
    private static final long serialVersionUID = 8013889745455047755L;

    private final DatabaseService databaseService;

    public RegistrationDialog(@NotNull final String title,
                              @NotNull final DatabaseService databaseService) {
        super(title);
        this.databaseService = databaseService;
    }

    @Override
    public void createForm(@NotNull final FormLayout formLayout, @NotNull final Binder<RegistrationRecord> binder) {
        final var editMode = binder.getBean().getConferenceId() != null;

        final var employee = new ComboBox<Employee>("Employee");
        final var conference = new ComboBox<Conference>("Conference");
        final var role = new ComboBox<RegistrationRole>("Role");
        final var reason = new TextArea("Reason");

        final var employees = databaseService.getAllEmployees().toList();
        final var conferences = databaseService.getFutureConferences().toList();

        employee.setRequiredIndicatorVisible(true);
        employee.setItems(employees);
        employee.setItemLabelGenerator(item -> String.format("%s %s", item.getFirstName(), item.getLastName()));
        employee.setReadOnly(editMode);

        conference.setRequiredIndicatorVisible(true);
        conference.setItems(conferences);
        conference.setItemLabelGenerator(Conference::name);
        conference.setReadOnly(editMode);

        role.setRequiredIndicatorVisible(true);
        role.setItems(RegistrationRole.values());
        role.setItemLabelGenerator(item -> item.toString().substring(0, 1).toUpperCase() + item.toString().substring(1));

        reason.setRequiredIndicatorVisible(true);
        reason.setValueChangeMode(EAGER);

        formLayout.add(employee, conference, role, reason);

        binder.forField(employee)
                .withValidator(Objects::nonNull,
                        "Please select the employee who wants to attend the conference")
                .bind(
                record -> employees.stream().filter(e -> e.getId().equals(record.getEmployeeId())).findFirst().orElse(null),
                (registrationRecord, item) -> registrationRecord.setEmployeeId(item.getId()));
        binder.forField(conference)
                .withValidator(Objects::nonNull,
                        "Please select the conference the employee wants to attend")
                .bind(
                record -> conferences.stream().filter(c -> c.id().equals(record.getConferenceId())).findFirst().orElse(null),
                (registrationRecord, item) -> registrationRecord.setConferenceId(item.id()));
        binder.forField(role)
                .withValidator(Objects::nonNull,
                        "Please select the role at the conference")
                .bind(RegistrationRecord::getRole, RegistrationRecord::setRole);
        binder.forField(reason)
                .withValidator(new StringLengthValidator(
                        "Please state the reason for the conference visit", 30, 500))
                .bind(RegistrationRecord::getReason, RegistrationRecord::setReason);
    }
}
