package io.github.xanderstuff.ultimatehud.hud;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.util.Vector2d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public abstract class Widget {
    //@Expose annotation tells GSON it should (de)serialize a field
    public UUID uuid; //TODO: how do we want to serialize the UUID? as (key=uuid, value=Widget)? Just as another Widget field? or how else?
    @Expose public Vector2d anchorPosition; // x and y axis should be 0.0 to 1.0, inclusive
    @Expose public Vector2d referencePosition; // x and y axis should be 0.0 to 1.0, inclusive
    @Expose public Vector2d offset;
    @Expose public double scale;
//    @Expose public boolean isBackgroundEnabled;
//    @Expose public Color backgroundColor; //TODO: figure out which variable type to use. Maybe Color is ok?
//    @Expose public boolean isBorderEnabled;
//    @Expose public Color borderColor;

    public Vector2d cachedPosition = new Vector2d();

    public abstract Identifier getIdentifier();

    public abstract String getName();

    public abstract double getWidth(PlayerEntity player);

    public abstract double getHeight(PlayerEntity player);

    public Vector2d getSize(PlayerEntity player) {
        return new Vector2d(getWidth(player), getHeight(player));
    }

    public boolean isSingleInstance() {
        return false;
    }

    public abstract void render(MatrixStack matrixStack, int x, int y, float tickDelta, PlayerEntity player);

    public void updatePosition(Vector2d parentPosition, Vector2d parentSize){
//        Vector2d newPosition = new Vector2d();
//        newPosition.add(parentPosition);
    }
}
