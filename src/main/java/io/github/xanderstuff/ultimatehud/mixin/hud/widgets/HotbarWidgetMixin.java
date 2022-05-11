package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.HotbarWidget;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class HotbarWidgetMixin {
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    //TODO: make this inject more specific, because "renderHotbar" includes the offhand slot and attack indicator (if set to be beside the hotbar)
    @Inject(at = @At("HEAD"), method = "renderHotbar")
    private void ultimatehud$HotbarWidgetMixin$renderPre(float tickDelta, MatrixStack matrixStack, CallbackInfo info) {
        int originalX = scaledWidth / 2 - 91;
        int originalY = scaledHeight - 22;
        int newX = (int) HotbarWidget.getInstance().cachedPosition.x - originalX;
        int newY = (int) HotbarWidget.getInstance().cachedPosition.y - originalY;

        MatrixStack ms = RenderSystem.getModelViewStack(); // We don't use the matrixStack argument here because the inner renderHotbarItem() calls do not use it, so changes to it doesn't seem to affect the rendered items.
        ms.push();
        ms.translate(newX, newY, 0);
        RenderSystem.applyModelViewMatrix();
    }

    //TODO: make this inject more specific, because "renderHotbar" includes the offhand slot and attack indicator (if set to be beside the hotbar)
    @Inject(at = @At("RETURN"), method = "renderHotbar")
    private void ultimatehud$HotbarWidgetMixin$renderPost(float tickDelta, MatrixStack matrixStack, CallbackInfo info) {
        MatrixStack ms = RenderSystem.getModelViewStack();
        ms.pop();
        RenderSystem.applyModelViewMatrix();
    }

    @Redirect(
            method = "renderHotbarItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderInGuiWithOverrides(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V")
    )
    private void ultimatehud$HotbarWidgetMixin$hotbarItemMovement(ItemRenderer itemRenderer, LivingEntity entity, ItemStack itemStack, int x, int y, int seed) {
        MatrixStack ms = RenderSystem.getModelViewStack();
        boolean isBlock = itemRenderer.getHeldItemModel(itemStack, null, entity, seed).hasDepth();
        if (isBlock && HotbarWidget.getInstance().twirlBlocks) {
            ms.push();
            ms.translate(x + 8, y + 12, (50 + 100));

            ms.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180 + 180 * MathHelper.sin(DrawUtil.timeMillis() / 2000.0F)));

            // Attempt at a "tumbling" effect. The rotation point doesn't seem to be centered and the lighting is rotated with the block for some reason. It doesn't look as good without fixing those things
//            ms.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(DrawUtil.timeMillis() / 20.0f)); // left-right
//            ms.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(DrawUtil.timeMillis() / 30.0f)); // up-down
//            ms.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(DrawUtil.timeMillis() / 50.0f)); // clockwise-counterclockwise
            //TODO: "rotateBlocks" - I also want to try a simple Y-axis rotation with up-down translation oscillation, akin to item entities on the ground. I think that could also look good

            ms.translate(-(x + 8), -(y + 12), -(50 + 100));
            RenderSystem.applyModelViewMatrix();
        }

        itemRenderer.renderInGuiWithOverrides(entity, itemStack, x, y, seed);

        if (isBlock && HotbarWidget.getInstance().twirlBlocks) {
            ms.pop();
            RenderSystem.applyModelViewMatrix();
        }
    }
}
