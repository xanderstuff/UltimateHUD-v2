package io.github.xanderstuff.ultimatehud.hud;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public abstract class Overlay {

    public abstract Identifier getIdentifier();

    public abstract String getName();

    public boolean isSingleInstance() {
        return false;
    }

    public abstract void render(MatrixStack matrixStack, int x, int y, int width, int height, float tickDelta, PlayerEntity player);
}
