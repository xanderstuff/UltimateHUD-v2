package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import io.github.xanderstuff.ultimatehud.hud.Widget;

public class HotbarWidget extends Widget {
    private static final HotbarWidget instance = new HotbarWidget();

    private HotbarWidget() {
        //populate with defaults
    }

    public static Widget getInstance() {
        return instance;
    }

    @Override
    public boolean isSingleInstance() {
        return true;
    }
}
