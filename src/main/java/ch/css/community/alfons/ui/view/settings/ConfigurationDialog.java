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

package ch.css.community.alfons.ui.view.settings;

import ch.css.community.alfons.Callback;
import ch.css.community.alfons.data.db.tables.records.ConfigurationRecord;
import ch.css.community.alfons.data.service.DatabaseService;
import ch.css.community.alfons.ui.component.EditDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

public final class ConfigurationDialog extends EditDialog<ConfigurationRecord> {

    private final DatabaseService databaseService;
    private Callback afterOpen;

    public ConfigurationDialog(@NotNull final String title,
                               @NotNull final DatabaseService databaseService) {
        super(title);
        this.databaseService = databaseService;
    }

    @Override
    public void createForm(@NotNull final FormLayout formLayout,
                           @NotNull final Binder<ConfigurationRecord> binder) {
        final var key = new TextField("Key");
        key.setRequiredIndicatorVisible(true);
        key.setValueChangeMode(EAGER);
        formLayout.add(key);

        final var value = new TextField("Value");
        value.setRequiredIndicatorVisible(true);
        value.setValueChangeMode(EAGER);
        formLayout.add(value);

        binder.forField(key)
                .withValidator(new StringLengthValidator(
                        "Please enter the key (max. 255 chars)", 1, 255))
                .bind(ConfigurationRecord::getKey, ConfigurationRecord::setKey);

        binder.forField(value)
                .withValidator(new StringLengthValidator(
                        "Please enter the value (max. 255 chars)", 1, 255))
                .bind(ConfigurationRecord::getValue, ConfigurationRecord::setValue);

        afterOpen = () -> key.setReadOnly(!key.getValue().isBlank());
    }

    @Override
    public void open(@NotNull final ConfigurationRecord configurationRecord, @Nullable final Callback afterSave) {
        super.open(configurationRecord,
                () -> {
                    if (afterOpen != null) {
                        afterOpen.execute();
                    }
                },
                () -> {
                    databaseService.reloadConfiguration();
                    if (afterSave != null) {
                        afterSave.execute();
                    }
                }
        );
    }

}
