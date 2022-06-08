package io.github.xanderstuff.ultimatehud.hud.widgets.ultimatehud;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import io.github.xanderstuff.ultimatehud.util.MiscUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class TextWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("ultimate-hud", "text");
//    @AutoConfig.ConfigComment() //FIXME: it seems Comments don't work
//    public AutoConfig.ConfigComment comment1;
    @Expose
    @AutoConfig.ConfigEntry
    public ContentType contentType = ContentType.CUSTOM_TEXT;
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

    //TODO: implement proper tokenizer (is that the right word?) and remove this (text replacement system)
    private enum ContentType {
        CUSTOM_TEXT((player) -> ""),
        POS_X((player) -> String.valueOf((long) player.getPos().x)),
        POS_Y((player) -> String.valueOf((long) player.getPos().y)),
        POS_Z((player) -> String.valueOf((long) player.getPos().z)),
        HEALTH((player) -> MathHelper.ceil(player.getHealth()) + " / " + (int) player.getMaxHealth()),
        //        MAX_HEALTH((player) -> ), // let's just make it simpler and hardcode the format of everything as "XX / YY". This feature is temporary anyway
        HUNGER((player) -> player.getHungerManager().getFoodLevel() + " / 20"),
        ARMOUR((player) -> player.getArmor() + " / 20"),
        AIR((player) -> (player.getAir() / 15) + " / 20"),
        XP_LEVEL((player) -> String.valueOf(player.experienceLevel)),
        TOTAL_XP((player) -> String.valueOf(MiscUtil.calculateTotalXP(player)));

        final Function<PlayerEntity, String> output;

        ContentType(Function<PlayerEntity, String> output) {
            this.output = output;
        }
    }

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
    public double getWidth(PlayerEntity player) {
        String string = text;
        if (contentType != ContentType.CUSTOM_TEXT) {
            string = contentType.output.apply(player);
        }
        var width = DrawUtil.getTextRenderer().getWidth(string) - 1 + (borderSize * 2);
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

        String string = text;
        if (contentType != ContentType.CUSTOM_TEXT) {
            string = contentType.output.apply(player);
        }

        switch (textShadowType) {
            case NONE -> DrawUtil.getTextRenderer().draw(matrixStack, string, x + borderSize, y + borderSize, DrawUtil.decodeARGB(textColor));
            case SHADOW -> DrawUtil.getTextRenderer().drawWithShadow(matrixStack, string, x + borderSize, y + borderSize, DrawUtil.decodeARGB(textColor));
            case OUTLINE -> {
                DrawUtil.getTextRenderer().draw(matrixStack, string, x + borderSize + 1, y + borderSize, DrawUtil.decodeARGB(textShadowColor));
                DrawUtil.getTextRenderer().draw(matrixStack, string, x + borderSize - 1, y + borderSize, DrawUtil.decodeARGB(textShadowColor));
                DrawUtil.getTextRenderer().draw(matrixStack, string, x + borderSize, y + borderSize + 1, DrawUtil.decodeARGB(textShadowColor));
                DrawUtil.getTextRenderer().draw(matrixStack, string, x + borderSize, y + borderSize - 1, DrawUtil.decodeARGB(textShadowColor));

                DrawUtil.getTextRenderer().draw(matrixStack, string, x + borderSize, y + borderSize, DrawUtil.decodeARGB(textColor));
            }
        }
    }
}
