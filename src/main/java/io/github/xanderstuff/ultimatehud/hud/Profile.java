package io.github.xanderstuff.ultimatehud.hud;

import com.google.gson.annotations.Expose;

import java.util.LinkedList;
import java.util.UUID;

public class Profile {
    public final UUID uuid;
    @Expose public final String name;

    @Expose public final LinkedList<Overlay> aboveHudOverlays;
    @Expose public final LinkedList<Overlay> belowHudOverlays;
    @Expose public final LinkedList<Widget> widgets;
    @Expose public WidgetTreeNode widgetPositioningTreeRoot; //TODO: we're going to need to make a (Generic?) Tree for the Widget positioning structure

}
