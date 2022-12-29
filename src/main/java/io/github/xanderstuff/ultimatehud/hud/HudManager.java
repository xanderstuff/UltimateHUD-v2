package io.github.xanderstuff.ultimatehud.hud;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.*;
import io.github.xanderstuff.ultimatehud.hud.widgets.ultimatehud.InventorySlotWidget;
import io.github.xanderstuff.ultimatehud.mixin.hud.widgets.serverIconWidget.ServerSessionAccessorMixin;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.TreeNode;
import io.github.xanderstuff.ultimatehud.util.Vector2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class HudManager {
    //    public static Map<String, UUID> serverProfiles; //TODO: serialize this in main config file
    //    public static Map<UUID, Profile> profilesByUUID;
    public static Profile currentProfile;
    private static Profile defaultProfile;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        defaultProfile = loadDefaultProfile();
        currentProfile = defaultProfile;
    }

    public static void onServerConnect() {
        if (client.isIntegratedServerRunning()) { // for singleplayer
            var levelSummary = ((ServerSessionAccessorMixin) client.getServer()).getSession().getLevelSummary();
            switchSession(SessionType.SINGLEPLAYER, levelSummary.getName(), levelSummary.getDisplayName());
        } else if (client.isConnectedToRealms()) { // connected to a realms server
            //TODO: I think this can just use the name of the player that owns the realm to uniquely identify the realm, but I'm not too familiar with realms, so I'm not sure what the desired behaviour should be
            // However realms only lets you connect with the latest minecraft version, so this mod would need to be updated first before this can be implemented/tested
            // see also: ServerIconWidget's code, which deals with differing session types as well
//                switchSession(SessionType.REALMS, ownerUuid, ownerUsername + "'s Realm"); //TODO: find a translation string that's similar to "<player>'s realm", which is probably used in a Realms menu
        } else if (client.getCurrentServerEntry() != null) { // connected to a multiplayer server
            var serverAddress = client.getCurrentServerEntry().address;
            var serverName = client.getCurrentServerEntry().name;

            if (client.getCurrentServerEntry().isLocal()) { // Mojang mappings calls this method "isLan()"
                // for lan connections, the ip address is unreliable and the port is random.
                // so instead, we will just use the name as the unique identifier, which comes in the format "<playername> - <world name>"
                switchSession(SessionType.LAN, serverName, serverName);
            } else {
                switchSession(SessionType.MULTIPLAYER, serverAddress, serverName);
            }
        }
    }

    private static void switchSession(SessionType sessionType, String sessionId, String sessionName) {
        //TODO: determine which profile to use based on which server/singleplayer world we logged in to, or use the default profile
        //String id = sessionType.prefix + ":" + sessionId;
        currentProfile = defaultProfile;
    }

    public static void updateWidgetPositions(PlayerEntity player) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
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
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        for (Overlay overlay : currentProfile.aboveHudOverlays) {
            overlay.render(matrixStack, 0, 0, width, height, tickDelta, player);
        }
    }

    public static void renderBelowHudOverlays(MatrixStack matrixStack, float tickDelta, PlayerEntity player) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        for (Overlay overlay : currentProfile.belowHudOverlays) {
            overlay.render(matrixStack, 0, 0, width, height, tickDelta, player);
        }
    }

    private static Profile loadDefaultProfile() {
        Profile profile = new Profile();

        // This replicates the vanilla HUD
        // TODO: move this to a file

        var hotbar = WidgetRegistry.get(HotbarWidget.IDENTIFIER);
        hotbar.referencePosition = new Vector2d(0.5, 1.0);
        hotbar.offset = new Vector2d(0, 0);
        hotbar.anchorPosition = new Vector2d(0.5, 1.0);
        profile.widgetsInRenderingOrder.add(hotbar);
        var hotbarNode = new TreeNode<Widget>(hotbar, null);
        profile.widgetPositioningTree.add(hotbarNode);

        var xpBar = WidgetRegistry.get(ExperienceBarWidget.IDENTIFIER);
        xpBar.referencePosition = new Vector2d(0.5, 1.0);
        xpBar.offset = new Vector2d(0, -2);
        xpBar.anchorPosition = new Vector2d(0.5, 0.0);
        profile.widgetsInRenderingOrder.add(xpBar);
        hotbarNode.addChild(xpBar); //TODO: make a TreeNode.addChild(TreeNode) method as well - or make this easier somehow

        var xpLevel = WidgetRegistry.get(ExperienceLevelWidget.IDENTIFIER);
        xpLevel.referencePosition = new Vector2d(0.5, 1.0);
        xpLevel.offset = new Vector2d(0, 2);
        xpLevel.anchorPosition = new Vector2d(0.5, 0.0);
        profile.widgetsInRenderingOrder.add(xpLevel);
        hotbarNode.getChildren().get(0).addChild(xpLevel); //TODO: fix this hack (see above TODO, 9 lines up)

        var health = WidgetRegistry.get(HealthIndicatorWidget.IDENTIFIER);
        health.referencePosition = new Vector2d(0.0, 1.0);
        health.offset = new Vector2d(0, -8);
        health.anchorPosition = new Vector2d(0.0, 0.0);
        profile.widgetsInRenderingOrder.add(health);
        hotbarNode.addChild(health); //TODO: fix this hack

        var armour = WidgetRegistry.get(ArmourIndicatorWidget.IDENTIFIER);
        armour.referencePosition = new Vector2d(0.0, 1.0);
        armour.offset = new Vector2d(0, -1);
        armour.anchorPosition = new Vector2d(0.0, 0.0);
        profile.widgetsInRenderingOrder.add(armour);
        hotbarNode.getChildren().get(1).addChild(armour); //TODO: fix this hack

        var food = WidgetRegistry.get(FoodIndicatorWidget.IDENTIFIER);
        food.referencePosition = new Vector2d(1.0, 1.0);
        food.offset = new Vector2d(0, -8);
        food.anchorPosition = new Vector2d(1.0, 0.0);
        profile.widgetsInRenderingOrder.add(food);
        hotbarNode.addChild(food); //TODO: fix this hack

        var air = WidgetRegistry.get(AirIndicatorWidget.IDENTIFIER);
        air.referencePosition = new Vector2d(1.0, 1.0);
        air.offset = new Vector2d(0, -1);
        air.anchorPosition = new Vector2d(1.0, 0.0);
        profile.widgetsInRenderingOrder.add(air);
        //TODO: this should be connected to the mount health widget instead of the food widget
        hotbarNode.getChildren().get(2).addChild(air); //TODO: fix this hack

        var offHandSlot = (InventorySlotWidget) WidgetRegistry.get(InventorySlotWidget.IDENTIFIER);
        offHandSlot.referencePosition = new Vector2d(1.0, 1.0);
        offHandSlot.offset = new Vector2d(-7, 0);
        offHandSlot.anchorPosition = new Vector2d(0.0, 1.0);
        offHandSlot.slotType = InventorySlotWidget.SlotType.OFF_HAND;
        offHandSlot.hideIfEmpty = true;
        profile.widgetsInRenderingOrder.add(offHandSlot);
        hotbarNode.addChild(offHandSlot);


        var scoreboard = WidgetRegistry.get(ScoreboardWidget.IDENTIFIER);
        scoreboard.referencePosition = new Vector2d(1.0, 0.666666);
        scoreboard.offset = new Vector2d(-1, -3);
        scoreboard.anchorPosition = new Vector2d(1.0, 0.5);
        profile.widgetsInRenderingOrder.add(scoreboard);
        var scoreboardNode = new TreeNode<Widget>(scoreboard, null);
        profile.widgetPositioningTree.add(scoreboardNode);

        return profile;
    }

    public enum SessionType {
        SINGLEPLAYER("singleplayer", "menu.singleplayer"),
        MULTIPLAYER("multiplayer", "menu.multiplayer"),
        LAN("lan", "title.multiplayer.lan"),
        REALMS("realms", "title.multiplayer.realms");

        public final String name;
        public final String translationString;

        SessionType(String name, String translationString) {
            this.name = name;
            this.translationString = translationString;
        }
    }
}
