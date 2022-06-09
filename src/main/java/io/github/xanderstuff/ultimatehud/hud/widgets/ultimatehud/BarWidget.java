package io.github.xanderstuff.ultimatehud.hud.widgets.ultimatehud;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import java.awt.Color;
import java.util.function.Function;

public class BarWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("ultimate-hud", "bar");
    private static final Identifier BARS_TEXTURE = new Identifier("textures/gui/bars.png");


    @Expose
    @AutoConfig.ConfigEntry()
    public ContentType contentType = ContentType.HEALTH;
    @Expose
    @AutoConfig.ConfigEntry(min = 16, max = 182)
    public int length = 182;
    @Expose
    @AutoConfig.ConfigEntry()
    public Direction direction = Direction.LEFT;
    @Expose
    @AutoConfig.ConfigEntry
    public ColorStyle colorStyle = ColorStyle.SOLID;
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String barColor = "#FFFFFF";
    @Expose
    @AutoConfig.ConfigEntry(min = 0.1F, max = 10.0F)
    public float RGBSpeed = 4.0F;
    @Expose
    @AutoConfig.ConfigEntry
    public BossBar.Style notchStyle = BossBar.Style.PROGRESS;


    enum Direction {
        LEFT,
        RIGHT,
        CENTER
    }

    enum ColorStyle {
        SOLID,
        RGB,
        CHASING_RGB
    }

    private enum ContentType {
        HEALTH((player) -> player.getHealth() / player.getMaxHealth()),
        HUNGER((player) -> player.getHungerManager().getFoodLevel() / 20.0F),
        ARMOUR((player) -> player.getArmor() / 20.0F),
        AIR((player) -> player.getAir() / 300.0F),
        XP_PROGRESS((player) -> player.experienceProgress),
        TEST((player) -> 0.5f + 0.5f * MathHelper.sin(DrawUtil.timeMillis() * 0.001f));

        final Function<PlayerEntity, Float> output;

        ContentType(Function<PlayerEntity, Float> output) {
            this.output = output;
        }

        public float get(PlayerEntity player) {
            try {
                //TODO: see if any functions can result in a division by zero and actually fix them rather than this try/catch hack
                return MathHelper.clamp(output.apply(player), 0.0F, 1.0F);
            } catch (Exception e) {
                return 0.0F;
            }
        }
    }

    static {
        WidgetRegistry.register(IDENTIFIER, BarWidget::new);
    }

    private BarWidget() {
        //populate with defaults
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public double getWidth(PlayerEntity player) {
        return length; //TODO: user-configurable length
    }

    @Override
    public double getHeight(PlayerEntity player) {
        return 5;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float tickDelta, PlayerEntity player) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // draw background
        RenderSystem.setShaderTexture(0, BARS_TEXTURE);
        DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, 60, length / 2, 5, 256, 256); // left side
        DrawableHelper.drawTexture(matrixStack, x + length / 2, y, 0, 182 - length / 2, 60, length / 2, 5, 256, 256); // right side

        float progress = contentType.get(player);
        int halfProgress = Math.round((length * progress) / 2.0F);
        int halfProgressInverted = Math.round(length * (1 - progress) / 2.0F);
        int leftOffset = 0;
        int rightOffset = 0;
        int leftTexOffset = 0;
        int rightTexOffset = 0;

        switch (direction) {
            //FIXME: LEFT/RIGHT directions make the far end side texture move twice as fast as it should (since the left and right side parts stack on top of each other)
            case LEFT -> {
                leftOffset = 0;
                leftTexOffset = 0;

                rightOffset = halfProgress;
                rightTexOffset = 182 - length / 2;
            }
            case RIGHT -> {
                leftOffset = halfProgressInverted * 2;
                leftTexOffset = length / 2 - halfProgress;

                rightOffset = length - halfProgress;
                rightTexOffset = 182 - halfProgress;
            }
            case CENTER -> {
                leftOffset = halfProgressInverted;
                leftTexOffset = length / 2 - halfProgress;

                rightOffset = length / 2;
                rightTexOffset = 182 - length / 2;
            }
        }

        if (colorStyle == ColorStyle.CHASING_RGB) {
//        renderRainbowTexture(
//                matrixStack,
//                x,
//                y,
//                1.0f,
//                182.0f,
//                5.0f,
//                0.0f,
//                65.0f,
//                182.0f,
//                5.0f,
//                256,
//                256,
//                //TODO: we should be able to use less quads than just the number of pixels, however the last quad needs to be rendered a different size - the remainder amount.
//                182, //50 is a good totalQuads value
//                -0.0004,
//                0.9f,
//                1.0f,
//                progress
//        );
            //TODO: renderRainbowTexture should adhere to the direction variable
            renderRainbowTexture(matrixStack, x, y, 1.0f, length, 5.0f, 0.0f, 65.0f,
                    182.0f,
                    5.0f,
                    256,
                    256,
                    //TODO: we should be able to use less quads than just the number of pixels, however the last quad needs to be rendered a different size - the remainder amount.
                    length,
                    RGBSpeed * -0.0002F,
                    0.9f,
                    1.0f,
                    progress
            );
        } else {
            var color = DrawUtil.decodeARGB(barColor);
            if (colorStyle == ColorStyle.RGB) {
                double multipliedMs = DrawUtil.timeMillis() * RGBSpeed * -0.0001F;
                float timeHueShift = (float) (multipliedMs - Math.floor(multipliedMs));
                color = Color.HSBtoRGB(timeHueShift, 0.9F, 1.0F);
            }

            RenderSystem.setShaderColor(DrawUtil.getRed(color) / 256F, DrawUtil.getGreen(color) / 256F, DrawUtil.getBlue(color) / 256F, 1.0F);
            //FIXME: rounding to nearest int makes total progress bar length only able to be multiples of 2
            DrawableHelper.drawTexture(matrixStack, x + leftOffset, y, leftTexOffset, 65, halfProgress, 5, 256, 256);
            DrawableHelper.drawTexture(matrixStack, x + rightOffset, y, rightTexOffset, 65, halfProgress, 5, 256, 256);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            //DEBUG
//            DrawableHelper.drawTexture(matrixStack, x, y - 11, 0, 0, 60, 182, 5, 256, 256);
//            DrawUtil.drawBox(matrixStack, x + leftTexOffset, y - 8, halfProgress, 3, 0x8FFF0000);
//            DrawUtil.drawBox(matrixStack, x + leftOffset, y - 3, halfProgress, 3, 0x8FFFFF00);
//            DrawUtil.drawBox(matrixStack, x + rightTexOffset, y - 8, halfProgress, 3, 0x8F00FF00);
//            DrawUtil.drawBox(matrixStack, x + rightOffset, y - 3, halfProgress, 3, 0x8F0000FF);
        }


        if (notchStyle != BossBar.Style.PROGRESS) {
            DrawableHelper.drawTexture(matrixStack, x, y, 0, 80 + (notchStyle.ordinal() - 1) * 5 * 2 + 5, length, 5, 256, 256);
        }

        RenderSystem.disableBlend();
    }


    private static void renderRainbowTexture(MatrixStack matrixStack, float x, float y, float z, float width, float height, float u, float v, float uWidth, float vHeight, int texWidth, int texHeight, int totalQuads, double speed, float saturation, float brightness, float percentFull) {
        // this method is based on "rainbow animated gui elements (texture and solid)" by burgerguy:
        // https://gist.github.com/burgerguy/70f1d6422fbaec6e4a6dd6848b57e9e3

//        RenderSystem.setShaderTexture(0, BARS_TEXTURE);

        // all colors will be shifted by this value before the modulo
        // we only get the decimal portion for the float, so we don't lose too much precision
        double multipliedMs = DrawUtil.timeMillis() * speed;
        float timeHueShift = (float) (multipliedMs - Math.floor(multipliedMs));

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        // determine constant values
        Matrix4f matrix = matrixStack.peek().getModel();
        float y1 = y;
        float y2 = y + height;
        float v1 = v / (float) texHeight;
        float v2 = (v + vHeight) / (float) texHeight;

        // determine initial values for previous vars
        int prevColor = Color.HSBtoRGB(timeHueShift, saturation, brightness);
        float prevPosOffset = 0.0f;
        float prevTexOffset = 0.0f;
        for (int currentQuad = 1; currentQuad <= (totalQuads * percentFull); currentQuad++) {
            // create current values
            float partialQuad = (float) currentQuad / totalQuads;
            float currentPosOffset = partialQuad * width;
            float currentTexOffset = partialQuad * uWidth;
            float currentHue = partialQuad + timeHueShift;
            // the function will get the decimal portion of our input float for us
            int currentColor = Color.HSBtoRGB(currentHue, saturation, brightness);

            float x1 = x + prevPosOffset;
            float x2 = x + currentPosOffset;
            float u1 = (u + prevTexOffset) / (float) texWidth;
            float u2 = (u + currentTexOffset) / (float) texWidth;
            // add vertices for current quad
            bufferBuilder.vertex(matrix, x1, y2, z).texture(u1, v2).method_39415(prevColor).next(); // method_39415 is "color"
            bufferBuilder.vertex(matrix, x2, y2, z).texture(u2, v2).method_39415(currentColor).next();
            bufferBuilder.vertex(matrix, x2, y1, z).texture(u2, v1).method_39415(currentColor).next();
            bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1).method_39415(prevColor).next();
            // set previous stuff to current for the next quad
            prevPosOffset = currentPosOffset;
            prevTexOffset = currentTexOffset;
            prevColor = currentColor;
        }

//        bufferBuilder.vertex(matrix, x1, y2, z).texture(u1, v2).method_39415(prevColor).next(); // method_39415 is "color"
//        bufferBuilder.vertex(matrix, x2, y2, z).texture(u2, v2).method_39415(currentColor).next();
//        bufferBuilder.vertex(matrix, x2, y1, z).texture(u2, v1).method_39415(currentColor).next();
//        bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1).method_39415(prevColor).next();

        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder); // send draw call

//        RenderSystem.disableBlend(); // reset glstate
    }
}
