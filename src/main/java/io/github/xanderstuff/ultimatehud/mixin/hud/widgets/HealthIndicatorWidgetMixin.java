package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.HealthIndicatorWidget;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class HealthIndicatorWidgetMixin {

    @Inject(method = "renderHealthBar", at = @At("HEAD"))
    private void ultimatehud$HealthIndicatorWidgetMixin$renderPre(MatrixStack matrixStack, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        int heartRows = MathHelper.ceil((maxHealth + (float) absorption) / 20.0F);
        int rowHeight = Math.max(12 - heartRows, 3);

        int originalY = y - (heartRows - 1) * rowHeight;
        int newX = (int) HealthIndicatorWidget.getInstance().cachedPosition.x - x;
        int newY = (int) HealthIndicatorWidget.getInstance().cachedPosition.y - originalY;

        matrixStack.push();
        matrixStack.translate(newX, newY, 0);
    }

    @Inject(method = "renderHealthBar", at = @At("RETURN"))
    private void ultimatehud$HealthIndicatorWidgetMixin$renderPost(MatrixStack matrixStack, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        matrixStack.pop();
    }
}
