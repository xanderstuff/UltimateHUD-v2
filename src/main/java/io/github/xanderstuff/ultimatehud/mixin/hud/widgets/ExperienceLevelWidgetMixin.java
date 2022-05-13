package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.ExperienceLevelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class ExperienceLevelWidgetMixin {
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "renderExperienceBar", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = "ldc=expLevel"))
    private void ultimatehud$ExperienceLevelWidgetMixin$renderPre(MatrixStack matrixStack, int x, CallbackInfo ci) {
        int originalX = (scaledWidth - (int) ExperienceLevelWidget.getInstance().getWidth(client.player) - 1) / 2;
        int originalY = scaledHeight - 31 - 4 - 1;
        int newX = (int) ExperienceLevelWidget.getInstance().cachedPosition.x - originalX;
        int newY = (int) ExperienceLevelWidget.getInstance().cachedPosition.y - originalY;

        matrixStack.push();
        //TESTING: try scaling by 2x (note: widget width/height also needs to be scaled, and the position still seems incorrect)
//        matrixStack.translate(originalX, originalY, 0);
//        matrixStack.scale(2, 2, 0);
//        matrixStack.translate(-originalX, -originalY, 0);
//        matrixStack.translate(newX / 2.0, newY / 2.0, 0);

        matrixStack.translate(newX, newY, 0);
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 1))
    private void ultimatehud$ExperienceLevelWidgetMixin$renderPost(MatrixStack matrixStack, int x, CallbackInfo ci) {
        matrixStack.pop();
    }

    @ModifyArgs(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private void ultimatehud$ExperienceLevelWidgetMixin$modifyText(Args args) {
        if((int) args.get(4) == 0x80FF20) { // the 0x80FF20 magic number is the hardcoded lime-green text color
            args.set(4, ExperienceLevelWidget.getInstance().textColor);
        }
        else if((int) args.get(4) == 0x000000){ // the 0x000000 magic number is the hardcoded black text outline color
            args.set(4, ExperienceLevelWidget.getInstance().outlineColor);
        }
    }
}
