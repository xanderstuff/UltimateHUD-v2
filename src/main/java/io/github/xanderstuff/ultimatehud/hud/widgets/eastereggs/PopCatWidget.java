package io.github.xanderstuff.ultimatehud.hud.widgets.eastereggs;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.xanderstuff.ultimatehud.UltimateHud;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;


public class PopCatWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("easter_eggs", "pop_cat");
    public static final Identifier POPCAT_MOUTH_CLOSED = new Identifier(UltimateHud.MODID, "textures/widgets/eastereggs/popcat/popcat_mouth_closed.png");
    public static final Identifier POPCAT_MOUTH_OPEN = new Identifier(UltimateHud.MODID, "textures/widgets/eastereggs/popcat/popcat_mouth_open.png");
    //TODO: get popcat sound effects


    @Expose
    @AutoConfig.ConfigEntry(min = 16, max = 256)
    public int size = 64;
    @Expose
    @AutoConfig.ConfigEntry
    public Rotation rotation = Rotation.UP;
    @Expose
    @AutoConfig.ConfigEntry
    public Direction facingDirection = Direction.RIGHT;

    enum Rotation {
        DOWN,
        LEFT,
        UP,
        RIGHT
    }

    enum Direction {
        LEFT,
        RIGHT
    }

    static {
        WidgetRegistry.register(IDENTIFIER, PopCatWidget::new);
    }

    private PopCatWidget() {

    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public double getWidth(PlayerEntity player) {
        return size;
    }

    @Override
    public double getHeight(PlayerEntity player) {
        return size;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float tickDelta, PlayerEntity player) {
        float rotationAngle = switch (rotation) {
            case DOWN -> 180;
            case UP -> 0;
            case RIGHT -> 90;
            case LEFT -> 270;
        };

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (DrawUtil.timeMillis() / 200 % 2 == 0) {
            RenderSystem.setShaderTexture(0, POPCAT_MOUTH_OPEN);
        } else {
            RenderSystem.setShaderTexture(0, POPCAT_MOUTH_CLOSED);
        }

        matrixStack.push();
        matrixStack.translate(x + size / 2.0, y + size / 2.0, 0);
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationAngle));
        matrixStack.translate(-(x + size / 2.0), -(y + size / 2.0), 0);

        int dir = facingDirection == Direction.RIGHT ? 1 : -1;
        DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, 0, size, size, size * dir, size);
        matrixStack.pop();

        RenderSystem.disableBlend();
    }
}
