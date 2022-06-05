package io.github.xanderstuff.ultimatehud.gui.screen;

import io.github.xanderstuff.ultimatehud.UltimateHud;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.HudManager;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.hud.overlays.ultimatehud.DamageFlashOverlay;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import io.github.xanderstuff.ultimatehud.util.TreeNode;
import io.github.xanderstuff.ultimatehud.util.Vector2d;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class ProfileEditorScreen extends Screen {
    private static final int WIDGET_OVERLAY_COLOR = 0x802050FF; //blue
    private static final int HOVERED_OVERLAY_COLOR = 0x80FFFFFF; //white
    private static final int SELECTED_OVERLAY_COLOR = 0x8020FF50; //green
    private final Screen previousScreen;
    private Widget hoveredWidget = null;
    private Widget selectedWidget = null;

    public ProfileEditorScreen(Screen previousScreen) {
        super(new LiteralText("Ultimate HUD profile editor screen")); //TODO: put in language file

        this.previousScreen = previousScreen;
    }


    @Override
    protected void init() {
        super.init();

        //...
    }


    @Override
    public void onClose() {
        client.setScreen(previousScreen);
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float tickDelta) {
        super.render(matrixStack, mouseX, mouseY, tickDelta); // we probably don't need this

        //TESTING:
//        HotbarWidget.getInstance().offset = new Vector2d(mouseX-100, -mouseY);


        hoveredWidget = locateWidget(mouseX, mouseY);

        for (Widget widget : HudManager.currentProfile.widgetsInRenderingOrder) {
            // draw a blue box on top of *all widgets
            // *except if the widget is being hovered over (white), or is currently selected (green)
            int color;
            if (widget == selectedWidget) {
                color = SELECTED_OVERLAY_COLOR;
            } else if (widget == hoveredWidget) {
                color = HOVERED_OVERLAY_COLOR;
            } else {
                color = WIDGET_OVERLAY_COLOR;
            }
            DrawUtil.drawBox(matrixStack, (int) widget.cachedPosition.x, (int) widget.cachedPosition.y, (int) widget.getWidth(client.player), (int) widget.getHeight(client.player), color);

            // show the name of the widget
            //TODO: this would probably be better to be in a dedicated position on-screen (like in the toolbar)
//            int x = (int) (widget.cachedPosition.x + widget.getWidth(client.player) / 2);
//            int y = (int) (widget.cachedPosition.y + widget.getHeight(client.player) / 2) - 4;
//            DrawableHelper.drawCenteredText(matrixStack, textRenderer, widget.getName(), x, y, 0xFFFFFFFF);
//            DrawableHelper.drawCenteredText(matrixStack, textRenderer, widget.getIdentifier().toString(), x, y, 0xFFFFFFFF);
        }

        var screenPos = new Vector2d(0, 0);
        var screenSize = new Vector2d(width, height);
        for (TreeNode<Widget> widgetTreeNode : HudManager.currentProfile.widgetPositioningTree) {
            renderWidgetConnections(matrixStack, widgetTreeNode, screenPos, screenSize);
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            return onLeftMouseClick(mouseX, mouseY);
        } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            return onRightMouseClick(mouseX, mouseY);
        }
        return false;
    }


    private boolean onLeftMouseClick(double mouseX, double mouseY) {
        selectedWidget = hoveredWidget; // note: hoveredWidget may be null, in which case the selected widget gets cleared as well
        return true;
    }


    private boolean onRightMouseClick(double mouseX, double mouseY) {
        if (hoveredWidget != null) {
            UltimateHud.LOGGER.info("OPEN CONFIG MENU FOR: " + hoveredWidget.getName()); //TODO: remove
//            var previousScreen = client.currentScreen;
//            var id = hoveredWidget.getIdentifier().toUnderscoreSeparatedString();
//            client.setScreen(AutoConfig.getScreen(previousScreen, id, hoveredWidget));
            return true;
        } else {
            //TODO: open OverlaySelectionScreen instead of this temporary hack
            var firstOverlay = HudManager.currentProfile.belowHudOverlays.get(0);
            if (firstOverlay instanceof DamageFlashOverlay damageFlashOverlay) {
                UltimateHud.LOGGER.info("OPEN CONFIG MENU FOR: " + damageFlashOverlay.getName()); //TODO: remove
//                var previousScreen = client.currentScreen;
//                var id = hoveredWidget.getIdentifier().toUnderscoreSeparatedString();
//                client.setScreen(AutoConfig.getScreen(previousScreen, id, hoveredWidget));
            }
            return true;
        }
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == GLFW_MOUSE_BUTTON_LEFT && selectedWidget != null) {
            selectedWidget.offset.add(deltaX, deltaY);
            return true;
        }
        return false;
    }


    private @Nullable Widget locateWidget(int screenX, int screenY) {
        for (Widget widget : HudManager.currentProfile.widgetsInRenderingOrder) {
            if (widget.cachedPosition.x <= screenX && screenX <= widget.cachedPosition.x + widget.getSize(client.player).x) {
                if (widget.cachedPosition.y <= screenY && screenY <= widget.cachedPosition.y + widget.getSize(client.player).y) {
                    return widget;
                }
            }
        }
        return null;
    }


    public void drawBackground(MatrixStack matrixStack) {
        // Note: this method is run by InGameHudMixin#ultimatehud$renderPre so that this background is rendered before the HUD
        if (client.world != null) {
            fill(matrixStack, 0, 0, this.width, this.height, 0xC0101010);
        } else {
            renderBackgroundTexture(0);
        }
    }


    private void renderWidgetConnections(MatrixStack matrixStack, TreeNode<Widget> widgetTreeNode, Vector2d parentPosition, Vector2d parentSize) {
        var widget = widgetTreeNode.get();

        // draw a small blue dot at the widget's calculated position (top left corner)
//        DrawUtil.drawBox(matrixStack, (int) widget.cachedPosition.x - 1, (int) widget.cachedPosition.y - 1, 2, 2, 0xFF4040FF);

        // draw a small green dot at the widget's reference position
        var refPos = widget.getReferenceScreenPosition(client.player);
        DrawUtil.drawBox(matrixStack, (int) refPos.x - 1, (int) refPos.y - 1, 2, 2, 0xFF40FF40);

        // draw a small red dot at the widget's anchor position
        var anchorPos = widget.getAnchorScreenPosition(parentPosition, parentSize);
        DrawUtil.drawBox(matrixStack, (int) anchorPos.x - 1, (int) anchorPos.y - 1, 2, 2, 0xFFFF4040);

        // draw an arrow between the widget's anchor and reference positions (this visualizes the offset)
        DrawUtil.drawArrow(matrixStack, (int) anchorPos.x, (int) anchorPos.y, (int) refPos.x, (int) refPos.y, 0x80FFFFFF);

        // Recursion!
        for (TreeNode<Widget> next : widgetTreeNode.getChildren()) {
            renderWidgetConnections(matrixStack, next, widget.cachedPosition, widget.getSize(client.player));
        }
    }
}
