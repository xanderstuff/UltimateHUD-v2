package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.AirIndicatorWidget;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class AirIndicatorWidgetMixin {
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;

    @Inject(method = "renderStatusBars", at = @At(shift = At.Shift.AFTER, value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=air"))
    private void ultimatehud$AirIndicatorWidgetMixin$renderPre(MatrixStack matrixStack, CallbackInfo ci) {
        int originalX = scaledWidth / 2 + 91 - 81;
        //FIXME: this widget will be in the wrong spot if the player is riding a living mount with more than 1 row of health
        // fortunately, it will be a long time until someone notices this bug because usually you get kicked off your mount when underwater XD
//        int heartCount = getHeartCount(getRiddenEntity());
//        int originalY = scaledHeight - 39 - 10 - ((int)Math.ceil((double)heartCount / 10.0) - 1) * 10;
        int originalY = scaledHeight - 39 - 10;
        int newX = (int) AirIndicatorWidget.getInstance().cachedPosition.x - originalX;
        int newY = (int) AirIndicatorWidget.getInstance().cachedPosition.y - originalY;

        matrixStack.push();
        matrixStack.translate(newX, newY, 0);
    }

    @Inject(method = "renderStatusBars", at = @At(shift = At.Shift.BEFORE, value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void ultimatehud$AirIndicatorWidgetMixin$renderPost(MatrixStack matrixStack, CallbackInfo ci) {
        matrixStack.pop();
    }

//    @Inject(method = "renderStatusBars", at = @At(shift = At.Shift.AFTER, value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=health"))
//    private void ultimatehud$HungerIndicatorWidgetMixin$renderPost2(MatrixStack matrixStack, CallbackInfo ci) {
//        matrixStack.pop();
//    }
}
