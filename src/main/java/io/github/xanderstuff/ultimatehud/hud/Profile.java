package io.github.xanderstuff.ultimatehud.hud;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.util.TreeNode;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Profile {
    //    public final UUID uuid;
    @Expose
    public String name;

    @Expose
    public final List<Overlay> aboveHudOverlays = new LinkedList<>();
    @Expose
    public final List<Overlay> belowHudOverlays = new LinkedList<>();
    @Expose
    public final List<Widget> widgetsInRenderingOrder = new LinkedList<>();
    public final List<TreeNode<Widget>> widgetPositioningTree = new LinkedList<>(); //TODO: how should this be serialized?

    private Widget findWidgetByUUID(UUID id) {
        for (Widget widget : widgetsInRenderingOrder) {
            if (widget.uuid.equals(id)) {
                return widget;
            }
        }
        return null;
    }

    public boolean deleteWidget(Widget widget) {
        if (!widgetsInRenderingOrder.contains(widget)) {
            return false;
        }

        TreeNode<Widget> foundWidgetNode = null;
        for (TreeNode<Widget> child : widgetPositioningTree) {
            foundWidgetNode = child.get(widget);
            if (foundWidgetNode != null) {
                break;
            }
        }

        if (foundWidgetNode == null) {
            return false;
        }

        if (foundWidgetNode.get() != widget) {
            throw new AssertionError("It seems Profile#deleteWidget is broken...");
        }

        // delete
        widgetsInRenderingOrder.remove(foundWidgetNode.get());
        List<TreeNode<Widget>> remainingChildren = foundWidgetNode.delete();
        widgetPositioningTree.remove(foundWidgetNode); //FIXME: This (and other manual iterations over widgetPositioningTree) wouldn't be necessary if TreeNode had a proper root node. This could use a refactor

        //TODO: children should inherit the anchor position of the deleted Widget
        //TODO: adjust offset so final position is the same
        // note: if this is not done, the delete operation may leave the child widgets off screen, making it look like this delete method is broken
        widgetPositioningTree.addAll(remainingChildren);

        return true;
    }
}
