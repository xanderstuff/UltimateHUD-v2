package io.github.xanderstuff.ultimatehud.mixin.hud;

import io.github.xanderstuff.ultimatehud.gui.screen.ProfileEditorScreen;
import io.github.xanderstuff.ultimatehud.hud.HudManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "render")
    private void ultimatehud$renderPre(MatrixStack matrixStack, float tickDelta, CallbackInfo info) {
        if (!client.options.hudHidden) {
            HudManager.renderBelowHudOverlays(matrixStack, tickDelta, client.player);
        }

        if (client.currentScreen instanceof ProfileEditorScreen) {
            ((ProfileEditorScreen) client.currentScreen).drawBackground(matrixStack);
        }
    }

    @Inject(at = @At("RETURN"), method = "render")
    private void ultimatehud$renderPost(MatrixStack matrixStack, float tickDelta, CallbackInfo info) {
        if (!client.options.hudHidden) {
            HudManager.renderWidgets(matrixStack, tickDelta, client.player);
            HudManager.renderAboveHudOverlays(matrixStack, tickDelta, client.player);
        }
    }
}
