package ch.fihlon.alfons.ui.view.about;

import ch.fihlon.alfons.data.entity.Role;
import ch.fihlon.alfons.ui.KaribuTest;
import com.github.mvysny.kaributools.RouterUtilsKt;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AboutViewTest extends KaribuTest {

    @Test
    void aboutView() {
        login("test@localhost", "password", List.of(Role.USER));
        RouterUtilsKt.navigateTo("/about");
        final var aboutView = _get(AboutView.class);
        assertEquals("Alfons", _get(aboutView, Image.class).getAlt().orElseThrow());
        assertEquals("Alfons v0.0.0-TEST", _get(aboutView, H2.class).getText());
        assertEquals("Make Community Management Great Again", _get(aboutView, H3.class).getText());
    }

}
