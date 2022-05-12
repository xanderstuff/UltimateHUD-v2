package io.github.xanderstuff.ultimatehud.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class EasterEggManagerScreen extends Screen {
    private final Screen previousScreen;

    public EasterEggManagerScreen(Screen previousScreen) {
        super(new LiteralText("Ultimate HUD easter egg manager screen")); //TODO: put in language file

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
        drawBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, tickDelta);

        //...
    }


    private void drawBackground(MatrixStack matrixStack) {
        if (client.world != null) {
            fill(matrixStack, 0, 0, this.width, this.height, 0xC0101010);
        } else {
            renderBackgroundTexture(0);
        }
    }
}
