package io.github.xanderstuff.ultimatehud;

import io.github.xanderstuff.ultimatehud.gui.screen.ProfileEditorScreen;
import io.github.xanderstuff.ultimatehud.hud.HudManager;
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
    public static final KeyBind profileEditorKB = new KeyBind("key.ultimate-hud.open_profile_editor", GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.ultimate-hud");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(profileEditorKB);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (profileEditorKB.wasPressed()) {
                var previousScreen = client.currentScreen;
                client.setScreen(new ProfileEditorScreen(previousScreen));
            }
        });

        HudManager.init();
    }
}
