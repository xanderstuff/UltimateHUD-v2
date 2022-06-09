package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HealthIndicatorWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("minecraft", "health_indicator");
    private static final HealthIndicatorWidget INSTANCE = new HealthIndicatorWidget();

    static {
        WidgetRegistry.register(IDENTIFIER, HealthIndicatorWidget::getInstance);
    }

    private HealthIndicatorWidget() {
        //populate with defaults
    }

    public static HealthIndicatorWidget getInstance() {
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
        float maxHealth = (float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        float absorption = MathHelper.ceil(player.getAbsorptionAmount());
        int totalRows = MathHelper.ceil((maxHealth + absorption) / 20.0F);
        int rowHeight = Math.max(12 - totalRows, 3);
        return 9 + (totalRows - 1) * rowHeight;
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
