package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.ArmourIndicatorWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class ArmourIndicatorWidgetMixin {
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = "ldc=armor"))
    private void ultimatehud$ArmourIndicatorWidgetMixin$renderPre(MatrixStack matrixStack, CallbackInfo ci) {
        //TODO: this can definitely be simplified
        float maxHealth = Math.max((float) client.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float) MathHelper.ceil(client.player.getHealth()));
        int absorption = MathHelper.ceil(client.player.getAbsorptionAmount());
        int heartRows = MathHelper.ceil((maxHealth + (float) absorption) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (heartRows - 2), 3);


        int originalX = scaledWidth / 2 - 91;
        int originalY = scaledHeight - 49 - (heartRows - 1) * rowHeight;
        int newX = (int) ArmourIndicatorWidget.getInstance().cachedPosition.x - originalX;
        int newY = (int) ArmourIndicatorWidget.getInstance().cachedPosition.y - originalY;

        matrixStack.push();
        matrixStack.translate(newX, newY, 0);
    }

    @Inject(method = "renderStatusBars", at = @At(shift = At.Shift.BEFORE, value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=health"))
    private void ultimatehud$ArmourIndicatorWidgetMixin$renderPost(MatrixStack matrixStack, CallbackInfo ci) {
        matrixStack.pop();
    }

//    @Inject(method = "renderStatusBars", at = @At(shift = At.Shift.AFTER, value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=health"))
//    private void ultimatehud$ArmourIndicatorWidgetMixin$renderPost2(MatrixStack matrixStack, CallbackInfo ci) {
//        matrixStack.pop();
//    }
}
