package io.github.xanderstuff.ultimatehud.hud;

import io.github.xanderstuff.ultimatehud.util.TreeNode;
import io.github.xanderstuff.ultimatehud.util.Vector2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HudManager {
    public static Map<String, UUID> serverProfiles; //TODO: serialize this in main config file
    public static Map<UUID, Profile> profilesByUUID;
//    public static final Profile defaultProfile; //TODO: set this to an uneditable, built-in "vanilla" profile
    public static Profile currentProfile;

    public static void onServerSwitch(){
        //TODO: select profile to use based on which server/singleplayer world we logged on to, or use the default profile
    }

    public static void updateWidgetPositions(PlayerEntity player){
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        var windowPosition = new Vector2d(0, 0);
        var windowSize = new Vector2d(width, height);

        updateWidgetPositions(currentProfile.widgetPositioningTree, windowPosition, windowSize, player);
    }

    private static void updateWidgetPositions(List<TreeNode<Widget>> widgetsToUpdate, Vector2d parentPosition, Vector2d parentSize, PlayerEntity player){
        for(TreeNode<Widget> widgetNode : widgetsToUpdate){
            var widget = widgetNode.get();
            // update selected widget position
            widget.updatePosition(parentPosition, parentSize, player);
            // recursively update all it's children
            updateWidgetPositions(widgetNode.getChildren(), widget.cachedPosition, widget.getSize(player), player);
        }
    }

    public static void renderWidgets(MatrixStack matrixStack, float tickDelta, PlayerEntity player) {
        for (Widget widget : currentProfile.widgetsInRenderingOrder) {
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
