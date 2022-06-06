package io.github.xanderstuff.ultimatehud.hud.widgets.ultimatehud;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class TextWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("ultimate-hud", "text");
    @Expose
    @AutoConfig.ConfigEntry
    public String text = "Sample Text";
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String textColor = "#FFFFFF";
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public TextShadowType textShadowType = TextShadowType.NONE;
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String textShadowColor = "#808080";
    @Expose
    @AutoConfig.ConfigEntry(min = 0, max = 32)
    public int borderSize = 1;
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String backgroundColor = "#000000";
    @Expose
    @AutoConfig.ConfigEntry(min = 0.0, max = 1.0)
    public float backgroundOpacity = 0.0F;

    private enum TextShadowType {
        NONE,
        SHADOW,
        OUTLINE
    }

    static {
        WidgetRegistry.register(IDENTIFIER, TextWidget::new);
    }

    private TextWidget() {
        //populate with defaults
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getName() {
        return "Text"; //TODO: use translation file
    }

    @Override
    public double getWidth(PlayerEntity player) {
        var width = DrawUtil.getTextRenderer().getWidth(text) - 1 + (borderSize * 2);
        return width > 0 ? width : 9; //TODO: maybe there's a better way to show 0-sized widgets in the ProfileEditorScreen?
    }

    @Override
    public double getHeight(PlayerEntity player) {
        return DrawUtil.getTextRenderer().fontHeight - 1 + (borderSize * 2);
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float tickDelta, PlayerEntity player) {
        var color = DrawUtil.decodeARGB(backgroundColor);
        DrawUtil.drawBox(matrixStack, x, y, (int) getWidth(player), (int) getHeight(player), DrawUtil.setOpacity(color, backgroundOpacity));

        switch (textShadowType) {
            case NONE -> DrawUtil.getTextRenderer().draw(matrixStack, text, x + borderSize, y + borderSize, DrawUtil.decodeARGB(textColor));
            case SHADOW -> DrawUtil.getTextRenderer().drawWithShadow(matrixStack, text, x + borderSize, y + borderSize, DrawUtil.decodeARGB(textColor));
            case OUTLINE -> {
                DrawUtil.getTextRenderer().draw(matrixStack, text, x + borderSize + 1, y + borderSize, DrawUtil.decodeARGB(textShadowColor));
                DrawUtil.getTextRenderer().draw(matrixStack, text, x + borderSize - 1, y + borderSize, DrawUtil.decodeARGB(textShadowColor));
                DrawUtil.getTextRenderer().draw(matrixStack, text, x + borderSize, y + borderSize + 1, DrawUtil.decodeARGB(textShadowColor));
                DrawUtil.getTextRenderer().draw(matrixStack, text, x + borderSize, y + borderSize - 1, DrawUtil.decodeARGB(textShadowColor));

                DrawUtil.getTextRenderer().draw(matrixStack, text, x + borderSize, y + borderSize, DrawUtil.decodeARGB(textColor));
            }
        }
    }
}
