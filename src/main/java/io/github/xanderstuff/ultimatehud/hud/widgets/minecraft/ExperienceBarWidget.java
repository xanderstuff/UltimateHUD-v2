package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ExperienceBarWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("minecraft", "experience_bar");
    private static final ExperienceBarWidget INSTANCE = new ExperienceBarWidget();

    static {
        WidgetRegistry.register(IDENTIFIER, ExperienceBarWidget::getInstance);
    }

    private ExperienceBarWidget() {
        //populate with defaults
    }

    public static ExperienceBarWidget getInstance() {
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
        return 5;
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
