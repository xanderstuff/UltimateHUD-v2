package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class HotbarWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("minecraft", "hotbar");
    private static final HotbarWidget INSTANCE = new HotbarWidget();
    @Expose boolean rotateItems;

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
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getName() {
        return "Hotbar"; //TODO: use translation file
    }

    @Override
    public double getWidth(PlayerEntity player) {
        return 182;
    }

    @Override
    public double getHeight(PlayerEntity player) {
        return 22;
    }

    @Override
    public boolean isSingleInstance() {
        return true;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float tickDelta, PlayerEntity player) {
        //no op, rendering is handled in minecraft's InGameHud
    }
}
