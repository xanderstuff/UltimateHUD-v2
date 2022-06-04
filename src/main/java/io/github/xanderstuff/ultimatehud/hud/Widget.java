package io.github.xanderstuff.ultimatehud.hud;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.util.Vector2d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public abstract class Widget {
    //@Expose annotation tells GSON it should (de)serialize a field
    @Expose
    public UUID uuid;
    @Expose
    public Vector2d anchorPosition; // x and y axis should be 0.0 to 1.0, inclusive
    @Expose
    public Vector2d referencePosition; // x and y axis should be 0.0 to 1.0, inclusive
    @Expose
    public Vector2d offset;
//    @Expose public double scale;
//    @Expose public boolean isBackgroundEnabled;
//    @Expose public int backgroundColor;
//    @Expose public boolean isBorderEnabled;
//    @Expose public int borderColor;

    public Vector2d cachedPosition = new Vector2d();
    //TODO: maybe cache size as well?

    public abstract Identifier getIdentifier();

    public abstract String getName();

    public abstract double getWidth(PlayerEntity player);

    public abstract double getHeight(PlayerEntity player);

    public Vector2d getSize(PlayerEntity player) {
        return new Vector2d(getWidth(player), getHeight(player));
    }

    public Vector2d getAnchorScreenPosition(Vector2d parentPosition, Vector2d parentSize) {
        return parentPosition.add(anchorPosition.multiplyEntrywise(parentSize));
    }

    public Vector2d getReferenceScreenPosition(PlayerEntity player) {
        return cachedPosition.add(referencePosition.multiplyEntrywise(getSize(player)));
    }

    public boolean isSingleInstance() {
        return false;
    }

    public abstract void render(MatrixStack matrixStack, int x, int y, float tickDelta, PlayerEntity player);

    public void updatePosition(Vector2d parentPosition, Vector2d parentSize, PlayerEntity player) {
        cachedPosition = parentPosition
                .add(anchorPosition.multiplyEntrywise(parentSize))
                .add(offset)
                .subtract(referencePosition.multiplyEntrywise(getSize(player)));
    }
}
