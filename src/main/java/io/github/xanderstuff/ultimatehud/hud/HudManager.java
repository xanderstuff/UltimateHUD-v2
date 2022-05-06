package io.github.xanderstuff.ultimatehud.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.UUID;

public class HudManager {
    public static Map<String, UUID> serverProfiles; //TODO: serialize this in main config file
    public static Map<UUID, Profile> profilesByUUID;
    public static final Profile defaultProfile; //TODO: set this to an uneditable, built-in "vanilla" profile
    public static Profile currentProfile;

    public static void onServerSwitch(){
        //TODO: select profile to use based on which server/singleplayer world we logged on to, or use the default profile
    }

    public static void updateWidgetPositions(float tickDelta, PlayerEntity player){
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        //TODO: the rest of the position calculations (recursively)
    }

    public static void renderWidgets(MatrixStack matrixStack, float tickDelta, PlayerEntity player) {
        for (Widget widget : currentProfile.widgets) {
            widget.render(matrixStack, (int) widget.cachedPosition.x, (int) widget.cachedPosition.y, tickDelta, player);
        }
    }

    public static void renderAboveHudOverlays(MatrixStack matrixStack, float tickDelta, PlayerEntity player) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        for (Overlay overlay : currentProfile.aboveHudOverlays) {
            overlay.render(matrixStack, 0, 0, width, height, tickDelta, player);
        }
    }

    public static void renderBelowHudOverlays(MatrixStack matrixStack, float tickDelta, PlayerEntity player) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        for (Overlay overlay : currentProfile.belowHudOverlays) {
            overlay.render(matrixStack, 0, 0, width, height, tickDelta, player);
        }
    }
}
