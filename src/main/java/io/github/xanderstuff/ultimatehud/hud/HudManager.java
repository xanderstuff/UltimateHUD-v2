package io.github.xanderstuff.ultimatehud.hud;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.*;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.TreeNode;
import io.github.xanderstuff.ultimatehud.util.Vector2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HudManager {
    //    public static Map<String, UUID> serverProfiles; //TODO: serialize this in main config file
    //    public static Map<UUID, Profile> profilesByUUID;
    public static Profile currentProfile;
    private static final Profile defaultProfile = new Profile(); //TODO: make this profile uneditable

    public static void init() {
        loadDefaultProfile();
        currentProfile = defaultProfile;
    }

    public static void onServerSwitch() {
        //TODO: select profile to use based on which server/singleplayer world we logged on to, or use the default profile
    }

    public static void updateWidgetPositions(PlayerEntity player) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        var windowPosition = new Vector2d(0, 0);
        var windowSize = new Vector2d(width, height);

        updateWidgetPositions(currentProfile.widgetPositioningTree, windowPosition, windowSize, player);
    }

    private static void updateWidgetPositions(List<TreeNode<Widget>> widgetsToUpdate, Vector2d parentPosition, Vector2d parentSize, PlayerEntity player) {
        for (TreeNode<Widget> widgetNode : widgetsToUpdate) {
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

    private static void loadDefaultProfile() {
        // This replicates the vanilla HUD
        // TODO: move this to a file

        var hotbar = WidgetRegistry.get(HotbarWidget.IDENTIFIER);
        hotbar.referencePosition = new Vector2d(0.5, 1.0);
        hotbar.offset = new Vector2d(0, 0);
        hotbar.anchorPosition = new Vector2d(0.5, 1.0);
        defaultProfile.widgetsInRenderingOrder.add(hotbar);
        var hotbarNode = new TreeNode<Widget>(hotbar, null);
        defaultProfile.widgetPositioningTree.add(hotbarNode);

        var xpBar = WidgetRegistry.get(ExperienceBarWidget.IDENTIFIER);
        xpBar.referencePosition = new Vector2d(0.5, 1.0);
        xpBar.offset = new Vector2d(0, -2);
        xpBar.anchorPosition = new Vector2d(0.5, 0.0);
        defaultProfile.widgetsInRenderingOrder.add(xpBar);
        hotbarNode.addChild(xpBar); //TODO: make a TreeNode.addChild(TreeNode) method as well - or make this easier somehow

        var xpLevel = WidgetRegistry.get(ExperienceLevelWidget.IDENTIFIER);
        xpLevel.referencePosition = new Vector2d(0.5, 1.0);
        xpLevel.offset = new Vector2d(0, 2);
        xpLevel.anchorPosition = new Vector2d(0.5, 0.0);
        defaultProfile.widgetsInRenderingOrder.add(xpLevel);
        hotbarNode.getChildren().get(0).addChild(xpLevel); //TODO: fix this hack (see above TODO, 9 lines up)

        var health = WidgetRegistry.get(HealthIndicatorWidget.IDENTIFIER);
        health.referencePosition = new Vector2d(0.0, 1.0);
        health.offset = new Vector2d(0, -8);
        health.anchorPosition = new Vector2d(0.0, 0.0);
        defaultProfile.widgetsInRenderingOrder.add(health);
        hotbarNode.addChild(health); //TODO: fix this hack

        var armour = WidgetRegistry.get(ArmourIndicatorWidget.IDENTIFIER);
        armour.referencePosition = new Vector2d(0.0, 1.0);
        armour.offset = new Vector2d(0, -1 - 17);
        armour.anchorPosition = new Vector2d(0.0, 0.0);
        defaultProfile.widgetsInRenderingOrder.add(armour);
        //TODO: this should be connected to the health widget instead
//        hotbarNode.getChildren().get(0).addChild(armour); //TODO: fix this hack
        hotbarNode.addChild(armour); //TODO: fix this hack

        var food = WidgetRegistry.get(FoodIndicatorWidget.IDENTIFIER);
        food.referencePosition = new Vector2d(1.0, 1.0);
        food.offset = new Vector2d(0, -8);
        food.anchorPosition = new Vector2d(1.0, 0.0);
        defaultProfile.widgetsInRenderingOrder.add(food);
        hotbarNode.addChild(food); //TODO: fix this hack

        var air = WidgetRegistry.get(AirIndicatorWidget.IDENTIFIER);
        air.referencePosition = new Vector2d(1.0, 1.0);
        air.offset = new Vector2d(0, -1 - 17);
        air.anchorPosition = new Vector2d(1.0, 0.0);
        defaultProfile.widgetsInRenderingOrder.add(air);
        //TODO: this should be connected to the mount health widget instead
        hotbarNode.addChild(air); //TODO: fix this hack


        var scoreboard = WidgetRegistry.get(ScoreboardWidget.IDENTIFIER);
        scoreboard.referencePosition = new Vector2d(1.0, 0.666666);
        scoreboard.offset = new Vector2d(-1, -3);
        scoreboard.anchorPosition = new Vector2d(1.0, 0.5);
        defaultProfile.widgetsInRenderingOrder.add(scoreboard);
        var scoreboardNode = new TreeNode<Widget>(scoreboard, null);
        defaultProfile.widgetPositioningTree.add(scoreboardNode);
    }
}
