package io.github.xanderstuff.ultimatehud.hud;

import io.github.xanderstuff.ultimatehud.UltimateHud;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public abstract class Overlay {

    public abstract Identifier getIdentifier();

    public String getName() {
        return I18n.translate("overlay." + UltimateHud.MODID + "." + getIdentifier().toUnderscoreSeparatedString() + ".name");
    }

    public boolean isSingleInstance() {
        return false;
    }

    public abstract void render(MatrixStack matrixStack, int x, int y, int windowWidth, int windowHeight, float tickDelta, PlayerEntity player);
}
