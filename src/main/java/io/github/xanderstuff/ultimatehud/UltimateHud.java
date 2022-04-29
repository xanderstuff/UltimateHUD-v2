package io.github.xanderstuff.ultimatehud;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UltimateHud implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("UltimateHUD");

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

//		LOGGER.info("Hello Minecraft world!");
	}
}
