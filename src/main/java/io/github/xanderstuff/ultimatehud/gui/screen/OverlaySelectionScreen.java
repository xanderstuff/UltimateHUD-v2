package io.github.xanderstuff.ultimatehud.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class OverlaySelectionScreen extends Screen {
    private final Screen previousScreen;

    protected OverlaySelectionScreen(Screen previousScreen) {
        super(new LiteralText("Ultimate HUD overlay selection screen")); //TODO: put in language file

        this.previousScreen = previousScreen;
        // use this to go back to the previous screen:
//         MinecraftClient.getInstance().setScreen(previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        //...
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        //...
    }


    private void drawBackground(MatrixStack matrices) {
        if (client.world != null) {
            fill(matrices, 0, 0, this.width, this.height, 0xC0101010);
        } else {
            renderBackgroundTexture(0);
        }
    }
}
