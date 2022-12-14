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

package ch.css.community.alfons.ui.view.about;

import ch.css.community.alfons.ui.view.MainLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.security.PermitAll;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@CssImport(value = "./themes/alfons/views/about-view.css")
public class AboutView extends VerticalLayout {

    public AboutView(final AboutViewVersionController versionController) {
        addClassNames("about-view");
        setSpacing(false);

        final var img = new Image("/images/alfons.png", "Alfons");
        img.setWidth("200px");
        add(img);

        add(new H2("Alfons v" + versionController.getVersion()));
        add(new H3("Make Community Management Great Again"));
        add(new Paragraph("Made with Vaadin Flow, jOOQ, Peace, and Love 🥰"));
        add(new Paragraph(new Anchor("https://www.gnu.org/licenses/agpl-3.0.en.html", "GNU Affero General Public License")));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    @Controller
    static class AboutViewVersionController {

        @Value("${alfons.version}")
        private String version = "UNKNOWN";

        String getVersion() {
            return version;
        }
    }
}
