package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class HotbarWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("minecraft", "hotbar");
    private static final HotbarWidget INSTANCE = new HotbarWidget();
    @Expose
    @AutoConfig.ConfigEntry
    public boolean twirlItems = false; //TODO: make this an enum for different item movement effects (like item entity -style rotation)
    @Expose
    @AutoConfig.ConfigEntry
    public boolean twirlOnlyBlocks = true;
    @Expose
    @AutoConfig.ConfigEntry(min = 0.1F, max = 5)
    public float twirlSpeed = 0.5F;

    static {
        WidgetRegistry.register(IDENTIFIER, HotbarWidget::getInstance);
    }

    private HotbarWidget() {
        //populate with defaults
    }

    public static HotbarWidget getInstance() {
        return INSTANCE;
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
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
