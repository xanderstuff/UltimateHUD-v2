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
        //FIXME: this widget will be translated to the wrong position if the player is riding a living mount with more than 1 row of health
        // fortunately, it will be a long time until someone notices this bug because usually you get kicked off your mount when underwater XD
//        int heartCount = getHeartCount(getRiddenEntity()); //TODO: simplify with getHeartRows()
//        int originalY = scaledHeight - 39 - 10 - ((int)Math.ceil((double)heartCount / 10.0) - 1) * 10; //FIXME: would this be correct?
        int originalY = scaledHeight - 39 - 10;
        int newX = (int) AirIndicatorWidget.getInstance().cachedPosition.x - originalX;
        int newY = (int) AirIndicatorWidget.getInstance().cachedPosition.y - originalY;

        matrixStack.push();
        matrixStack.translate(newX, newY, 0);
    }

    // inject before client.getProfiler().pop(), so that we don't affect any other mods that would probably inject at the TAIL of renderStatusBars()
    @Inject(method = "renderStatusBars", at = @At(shift = At.Shift.BEFORE, value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void ultimatehud$AirIndicatorWidgetMixin$renderPost(MatrixStack matrixStack, CallbackInfo ci) {
        matrixStack.pop();
    }
}
