package io.github.xanderstuff.ultimatehud.hud.overlays.ultimatehud;

import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.hud.Overlay;
import io.github.xanderstuff.ultimatehud.registry.OverlayRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class DamageFlashOverlay extends Overlay {
    public static final Identifier IDENTIFIER = new Identifier("ultimate-hud", "damageflash");
    @Expose public int defaultDamageFlashColor = 0x64FF2020;

    static {
        OverlayRegistry.register(IDENTIFIER, DamageFlashOverlay::new);
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getName() {
        return "Damage Flash";
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, int width, int height, float tickDelta, PlayerEntity player) {
        //TODO: select different colors based on what category of damage is taken (such as green for poison damage and blueish-white for freezing)
        float opacity = (float) MathHelper.clampedMap(player.hurtTime, 0, 10, 0.0, 1.0);
        DrawUtil.fill(matrixStack, 0, 0, width, height, DrawUtil.multiplyOpacity(defaultDamageFlashColor, opacity));
    }
}
