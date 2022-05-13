package io.github.xanderstuff.ultimatehud.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class DrawUtil {
    private static final long startTime = System.currentTimeMillis();

    public static TextRenderer getTextRenderer() {
        return MinecraftClient.getInstance().textRenderer;
    }

    public static ItemRenderer getItemRenderer() {
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
        int newAlpha = MathHelper.clamp((int) (getAlpha(argb) * opacity), 0, 255);
        return (argb & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static long timeMillis() {
        return System.currentTimeMillis() - startTime; // Offset by a fixed amount so that the return value starts at 0 (so we don't have precision issues when using this in calculations)
    }

    public static void drawBox(MatrixStack matrixStack, int x, int y, int width, int height, int color) {
        DrawableHelper.fill(matrixStack, x, y, x + width, y + height, color);
    }

    public static void drawArrow(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int arrowColor) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int dist = (int) Math.sqrt(dx * dx + dy * dy);
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        final float ARROW_TIP_ANGLE = 45;

        matrixStack.push();
        matrixStack.translate(x1 - 0.5, y1 - 0.5, 0);

        // Draw main line
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle));
//        matrixStack.scale(1.0F, thickness, 1.0F); // Make line thinner than 1 minecraft hud pixel (thickness = 0.5)
        drawHorizontalLine(matrixStack, 0, dist, 0, arrowColor);

        if (dist > 8) {
            // Draw arrow head
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-ARROW_TIP_ANGLE));
            drawHorizontalLine(matrixStack, 0, 4, 0, arrowColor);
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(ARROW_TIP_ANGLE * 2));
            drawHorizontalLine(matrixStack, 0, 5, 0, arrowColor);
        }
        matrixStack.pop();
    }

    public static void drawHorizontalLine(MatrixStack matrixStack, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        DrawableHelper.fill(matrixStack, x1, y, x2 + 1, y + 1, color);
    }

    public static void drawVerticalLine(MatrixStack matrixStack, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }

        DrawableHelper.fill(matrixStack, x, y1 + 1, x + 1, y2, color);
    }
}
