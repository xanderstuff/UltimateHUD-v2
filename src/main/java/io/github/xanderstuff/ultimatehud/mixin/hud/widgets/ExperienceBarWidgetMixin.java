package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.ExperienceBarWidget;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class ExperienceBarWidgetMixin {
    @Shadow
    private int scaledHeight;

    @Inject(method = "renderExperienceBar", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = "ldc=expBar"))
    private void ultimatehud$ExperienceBarWidgetMixin$renderPre(MatrixStack matrixStack, int x, CallbackInfo ci) {
        int originalY = scaledHeight - 32 + 3;
        int newX = (int) ExperienceBarWidget.getInstance().cachedPosition.x - x;
        int newY = (int) ExperienceBarWidget.getInstance().cachedPosition.y - originalY;

        matrixStack.push();
        matrixStack.translate(newX, newY, 0);
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0))
    private void ultimatehud$ExperienceBarWidgetMixin$renderPost(MatrixStack matrixStack, int x, CallbackInfo ci) {
        matrixStack.pop();
    }
}
