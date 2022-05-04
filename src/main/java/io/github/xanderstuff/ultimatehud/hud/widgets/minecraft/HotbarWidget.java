package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import net.minecraft.util.Identifier;

public class HotbarWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("minecraft", "hotbar");
    private static final HotbarWidget INSTANCE = new HotbarWidget();

    static {
        WidgetRegistry.register(IDENTIFIER, HotbarWidget::getInstance);
    }

    private HotbarWidget() {
        //populate with defaults
    }

    public static Widget getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isSingleInstance() {
        return true;
    }
}
