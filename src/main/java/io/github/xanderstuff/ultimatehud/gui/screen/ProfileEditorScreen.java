package io.github.xanderstuff.ultimatehud.gui.screen;

import io.github.xanderstuff.ultimatehud.UltimateHud;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.HudManager;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.hud.overlays.ultimatehud.DamageFlashOverlay;
import io.github.xanderstuff.ultimatehud.hud.widgets.ultimatehud.TextWidget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import io.github.xanderstuff.ultimatehud.util.TreeNode;
import io.github.xanderstuff.ultimatehud.util.Vector2d;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

public class ProfileEditorScreen extends Screen {
    private static final int WIDGET_OVERLAY_COLOR = 0x802050FF; //blue
    private static final int HOVERED_OVERLAY_COLOR = 0x80FFFFFF; //white
    private static final int SELECTED_OVERLAY_COLOR = 0x8020FF50; //green
    private final Screen previousScreen;
    private Widget hoveredWidget = null;
    private Widget selectedWidget = null;

    //TODO: remove this temporary widget selection code
    private WidgetToAdd widgetToAdd = WidgetToAdd.TEXT;

    private enum WidgetToAdd {
        TEXT("Text", () -> WidgetRegistry.get(TextWidget.IDENTIFIER)),
        TEXT3("Text3", () -> WidgetRegistry.get(TextWidget.IDENTIFIER)),
        TEXT2("Text2", () -> WidgetRegistry.get(TextWidget.IDENTIFIER));

        String name;
        Supplier<Widget> makeWidget;

        WidgetToAdd(String name, Supplier<Widget> makeWidget) {
            this.name = name;
            this.makeWidget = makeWidget;
        }
    }


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
        hoveredWidget = locateWidget(mouseX, mouseY);

        //TODO: remove this temporary instructions message
        int line = 0;
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "Hi there!", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "You've found the §9UltimateHUD Editor Screen§r", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "This mod is a WIP, so this editor isn't finished", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "Changes will not be saved after restart (yet)", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "And you can't delete vanilla hud elements (yet)", 48, 32 + 9 * line++, 0xAAAAAAAA);

        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "- §6Drag§r to move widgets", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "- §6Left click§r to select a widget", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "- §6Right click§r to change a widget's settings", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "- §6WASD / Arrow keys§r to move a selected widget by 1px", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "- §6Scroll wheel§r to choose a new widget", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "- §6Middle click§r to add a new widget", 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "Widget to add: §a" + widgetToAdd.name, 48, 32 + 9 * line++, 0xAAAAAAAA);

        String name = "";
        String id = "";
        if (hoveredWidget != null) {
            name = hoveredWidget.getName();
            id = hoveredWidget.getIdentifier().toString();
        }
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "Widget name: " + name, 48, 32 + 9 * line++, 0xAAAAAAAA);
        DrawUtil.getTextRenderer().drawWithShadow(matrixStack, "Widget ID: " + id, 48, 32 + 9 * line++, 0xAAAAAAAA);


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
        }

        var screenPos = new Vector2d(0, 0);
        var screenSize = new Vector2d(width, height);
        for (TreeNode<Widget> widgetTreeNode : HudManager.currentProfile.widgetPositioningTree) {
            renderWidgetConnections(matrixStack, widgetTreeNode, screenPos, screenSize);
        }
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (UltimateHud.profileEditorKB.matchesKey(keyCode, scanCode)) {
            // press profileEditorKB (default: RIGHT SHIFT) to exit editor
            onClose();
            return true;
        }

        if (selectedWidget != null) {
            // press WASD / arrow keys to move the selectedWidget's offset
            // client.options.forwardKey.matchesKey(keyCode, scanCode)
            if (keyCode == GLFW_KEY_UP || keyCode == GLFW_KEY_W) {
                selectedWidget.offset.add(0, -1);
                return true;
            } else if (keyCode == GLFW_KEY_DOWN || keyCode == GLFW_KEY_S) {
                selectedWidget.offset.add(0, 1);
                return true;
            } else if (keyCode == GLFW_KEY_LEFT || keyCode == GLFW_KEY_A) {
                selectedWidget.offset.add(-1, 0);
                return true;
            } else if (keyCode == GLFW_KEY_RIGHT || keyCode == GLFW_KEY_D) {
                selectedWidget.offset.add(1, 0);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            return onLeftMouseClick(mouseX, mouseY);
        } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            return onRightMouseClick(mouseX, mouseY);
        } else if (button == GLFW_MOUSE_BUTTON_MIDDLE) {
            //TODO: remove this temporary widget selection code
//            var newWidget = (TextWidget) WidgetRegistry.get(TextWidget.IDENTIFIER);
            var newWidget = widgetToAdd.makeWidget.get();
            newWidget.referencePosition = new Vector2d(0.0, 0.0);
            newWidget.offset = new Vector2d(mouseX, mouseY);
            newWidget.anchorPosition = new Vector2d(0.0, 0.0);
            HudManager.currentProfile.widgetsInRenderingOrder.add(newWidget);
            HudManager.currentProfile.widgetPositioningTree.add(new TreeNode<Widget>(newWidget, null));
            return true;
        }
        return false;
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        //TODO: remove this temporary widget selection code
        int index = widgetToAdd.ordinal() + 1;
        widgetToAdd = WidgetToAdd.values()[index >= WidgetToAdd.values().length ? 0 : index];
        return true;
    }


    private boolean onLeftMouseClick(double mouseX, double mouseY) {
        selectedWidget = hoveredWidget; // note: hoveredWidget may be null, in which case the selected widget gets cleared as well
        return true;
    }


    private boolean onRightMouseClick(double mouseX, double mouseY) {
        if (hoveredWidget != null) {
            var previousScreen = client.currentScreen;
            var id = hoveredWidget.getIdentifier().toUnderscoreSeparatedString();
            client.setScreen(AutoConfig.getScreen(previousScreen, id, hoveredWidget));
            return true;
        } else {
            //TODO: open OverlaySelectionScreen instead of this temporary hack
            if (!HudManager.currentProfile.belowHudOverlays.isEmpty()) {
                var firstOverlay = HudManager.currentProfile.belowHudOverlays.get(0);
                if (firstOverlay instanceof DamageFlashOverlay damageFlashOverlay) {
                    var previousScreen = client.currentScreen;
                    var id = damageFlashOverlay.getIdentifier().toUnderscoreSeparatedString();
                    client.setScreen(AutoConfig.getScreen(previousScreen, id, damageFlashOverlay));
                    return true;
                }
            }
        }

        return false;
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
