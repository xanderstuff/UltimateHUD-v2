package io.github.xanderstuff.ultimatehud.hud;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.util.TreeNode;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Profile {
    public final UUID uuid;
    @Expose public String name;

    @Expose public final List<Overlay> aboveHudOverlays = new LinkedList<>();
    @Expose public final List<Overlay> belowHudOverlays = new LinkedList<>();
    @Expose public final List<Widget> widgetsInRenderingOrder = new LinkedList<>();
    public final List<TreeNode<Widget>> widgetPositioningTree = new LinkedList<>(); //TODO: how should this be serialized?

    private Widget findWidgetByUUID(UUID id){
        for(Widget widget : widgetsInRenderingOrder){
            if(widget.uuid.equals(id)){
                return widget;
            }
        }
        return null;
    }
}
