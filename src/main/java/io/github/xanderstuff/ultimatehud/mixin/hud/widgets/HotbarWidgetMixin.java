package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.HotbarWidget;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
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

    //TODO: replace with MixinExtra's @WrapOperation
    @Redirect(
            method = "renderHotbarItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderInGuiWithOverrides(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V")
    )
    private void ultimatehud$HotbarWidgetMixin$hotbarItemMovement(ItemRenderer itemRenderer, LivingEntity entity, ItemStack itemStack, int x, int y, int seed) {
        MatrixStack ms = RenderSystem.getModelViewStack();
        // (itemStack.getItem() instanceof BlockItem) - apparently powdered snow buckets are BlockItems...
        // itemRenderer.getHeldItemModel(...).hasDepth - it seems spyglasses and tridents return true, due to their hardcoded 3D models (and shields use a 3D model too)
        // itemRenderer.getModels().getModel(...).hasDepth() - I think this actually gives the item model used in the gui rather than in the hand, although shields still return true, due to their 3D model
        // but combining these conditions seems to give a decent result
        boolean isBlock = (itemStack.getItem() instanceof BlockItem) && itemRenderer.getModels().getModel(itemStack).hasDepth();
        boolean runAnimation = (isBlock || !HotbarWidget.getInstance().twirlOnlyBlocks) && HotbarWidget.getInstance().twirlItems;
        if (runAnimation) {
            ms.push();
            ms.translate(x + 8, y + 12, (50 + 100));

            ms.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180 + 180 * MathHelper.sin(DrawUtil.timeMillis() * 0.001F * HotbarWidget.getInstance().twirlSpeed)));

            // Attempt at a "tumbling" effect. The rotation point doesn't seem to be centered and the lighting is rotated with the block for some reason. It doesn't look as good without fixing those things
//            ms.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(DrawUtil.timeMillis() / 20.0f)); // left-right
//            ms.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(DrawUtil.timeMillis() / 30.0f)); // up-down
//            ms.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(DrawUtil.timeMillis() / 50.0f)); // clockwise-counterclockwise
            //TODO: "rotateBlocks" - I also want to try a simple Y-axis rotation with up-down translation oscillation, akin to item entities on the ground. I think it could also look good

            ms.translate(-(x + 8), -(y + 12), -(50 + 100));
            RenderSystem.applyModelViewMatrix();
        }

        itemRenderer.renderInGuiWithOverrides(entity, itemStack, x, y, seed);

        if (runAnimation) {
            ms.pop();
            RenderSystem.applyModelViewMatrix();
        }
    }

    @Redirect(
            method = "renderHotbar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack ultimatehud$HotbarWidgetMixin$disableOffHandSlot(PlayerEntity playerEntity) {
        // we want to disable this so that an InventorySlotWidget can be used instead, which has more customization options (such as changing its position)
        return ItemStack.EMPTY; // InGameHud::renderHotbar will hide the off hand slot if it's empty
    }
}
