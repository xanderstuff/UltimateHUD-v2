package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ArmourIndicatorWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("minecraft", "armour_indicator");
    private static final ArmourIndicatorWidget INSTANCE = new ArmourIndicatorWidget();

    static {
        WidgetRegistry.register(IDENTIFIER, ArmourIndicatorWidget::getInstance);
    }

    private ArmourIndicatorWidget() {
        //populate with defaults
    }

    public static ArmourIndicatorWidget getInstance() {
        return INSTANCE;
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public double getWidth(PlayerEntity player) {
        return 81;
    }

    @Override
    public double getHeight(PlayerEntity player) {
        return 9;
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
