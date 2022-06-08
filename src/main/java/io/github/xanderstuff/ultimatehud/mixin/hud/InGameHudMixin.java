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
        var isEditorScreenOpen = client.currentScreen instanceof ProfileEditorScreen;

        if (!client.options.hudHidden && !isEditorScreenOpen) {
            HudManager.renderBelowHudOverlays(matrixStack, tickDelta, client.player);
        }

        if (isEditorScreenOpen) {
            ((ProfileEditorScreen) client.currentScreen).drawBackground(matrixStack);
        }

        HudManager.updateWidgetPositions(client.player);
    }

    //FIXME: RETURN is after the debug hud (F3), but HUD stuff should be rendered before the debug hud (we don't want to cover it)
    @Inject(at = @At("RETURN"), method = "render")
    private void ultimatehud$renderPost(MatrixStack matrixStack, float tickDelta, CallbackInfo info) {
        var isEditorScreenOpen = client.currentScreen instanceof ProfileEditorScreen;

        if (!client.options.hudHidden) {
            HudManager.renderWidgets(matrixStack, tickDelta, client.player);
        }

        if (!client.options.hudHidden && !isEditorScreenOpen && !client.options.debugEnabled) {
            HudManager.renderAboveHudOverlays(matrixStack, tickDelta, client.player);
        }
    }
}
