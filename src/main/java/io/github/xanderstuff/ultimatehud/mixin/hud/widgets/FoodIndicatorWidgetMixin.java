package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.FoodIndicatorWidget;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class FoodIndicatorWidgetMixin {
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
    private void ultimatehud$FoodIndicatorWidgetMixin$renderPre(MatrixStack matrixStack, CallbackInfo ci) {
        int originalX = scaledWidth / 2 + 91 - 81;
        int originalY = scaledHeight - 39;
        int newX = (int) FoodIndicatorWidget.getInstance().cachedPosition.x - originalX;
        int newY = (int) FoodIndicatorWidget.getInstance().cachedPosition.y - originalY;

        matrixStack.push();
        matrixStack.translate(newX, newY, 0);
    }

    @Inject(method = "renderStatusBars", at = @At(shift = At.Shift.BEFORE, value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=air"))
    private void ultimatehud$FoodIndicatorWidgetMixin$renderPost(MatrixStack matrixStack, CallbackInfo ci) {
        matrixStack.pop();
    }
}
