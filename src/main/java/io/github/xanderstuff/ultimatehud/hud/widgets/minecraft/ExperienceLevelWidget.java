package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ExperienceLevelWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("minecraft", "experience_level");
    private static final ExperienceLevelWidget INSTANCE = new ExperienceLevelWidget();
    @Expose
    public int textColor = 0xFF80FF20;
    @Expose
    public int outlineColor = 0xFF000000;

    static {
        WidgetRegistry.register(IDENTIFIER, ExperienceLevelWidget::getInstance);
    }

    private ExperienceLevelWidget() {
        //populate with defaults
    }

    public static ExperienceLevelWidget getInstance() {
        return INSTANCE;
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public double getWidth(PlayerEntity player) {
        return DrawUtil.getTextRenderer().getWidth(String.valueOf(player.experienceLevel)) + 1;
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
