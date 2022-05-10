package io.github.xanderstuff.ultimatehud.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.math.MathHelper;

public class DrawUtil extends DrawableHelper {
    private static final long startTime = System.currentTimeMillis();

    public static TextRenderer getTextRenderer(){
        return MinecraftClient.getInstance().textRenderer;
    }

    public static ItemRenderer getItemRenderer(){
        return MinecraftClient.getInstance().getItemRenderer();
    }

    public static int getAlpha(int argb) {
        return argb >>> 24;
    }

    public static int getRed(int argb) {
        return (argb >> 16) & 0xFF;
    }

    public static int getGreen(int argb) {
        return (argb >> 8) & 0xFF;
    }

    public static int getBlue(int argb) {
        return argb & 0xFF;
    }

    public static int toArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int multiplyOpacity(int argb, float opacity) {
        int newAlpha = MathHelper.clamp((int)(getAlpha(argb) * opacity), 0, 255);
        return (argb & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static long timeMillis(){
        return System.currentTimeMillis() - startTime; // Offset by a fixed amount so that the return value starts at 0 (so we don't have precision issues when using this in calculations)
    }
}
