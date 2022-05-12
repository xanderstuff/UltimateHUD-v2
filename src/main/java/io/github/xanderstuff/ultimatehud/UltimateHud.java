package io.github.xanderstuff.ultimatehud;

import io.github.xanderstuff.ultimatehud.gui.screen.ProfileEditorScreen;
import io.github.xanderstuff.ultimatehud.hud.HudManager;
import io.github.xanderstuff.ultimatehud.hud.Profile;
import io.github.xanderstuff.ultimatehud.hud.overlays.ultimatehud.DamageFlashOverlay;
import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.HotbarWidget;
import io.github.xanderstuff.ultimatehud.registry.OverlayRegistry;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.TreeNode;
import io.github.xanderstuff.ultimatehud.util.Vector2d;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBind;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UltimateHud implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("UltimateHUD");
    public static final String MODID = "ultimate-hud";

    @Override
    public void onInitializeClient() {
        KeyBind profileEditorKB = KeyBindingHelper.registerKeyBinding(new KeyBind("keybinding.ultimate-hud.mainCategory.openProfileEditor", GLFW.GLFW_KEY_RIGHT_SHIFT, "keybinding.ultimate-hud.mainCategory"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (profileEditorKB.wasPressed()) {
                var previousScreen = client.currentScreen;
                client.setScreen(new ProfileEditorScreen(previousScreen));
            }
        });

//		LOGGER.info("Hello Minecraft world!");
	}
}
