package ch.css.community.ui.view;

import ch.css.community.components.appnav.AppNavItem;
import com.vaadin.flow.component.Component;

public record MainMenuItem(String label, Class<? extends Component> view, String iconClass) {

    AppNavItem toAppNavItem() {
        return new AppNavItem(label(), view(), iconClass());
    }

}
