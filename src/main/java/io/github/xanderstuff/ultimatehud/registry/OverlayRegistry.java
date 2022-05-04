package io.github.xanderstuff.ultimatehud.registry;

import io.github.xanderstuff.ultimatehud.hud.Overlay;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class OverlayRegistry {
    private static final Map<Identifier, Supplier<Overlay>> REGISTRY = new LinkedHashMap<>();

    public static void register(Identifier identifier, Supplier<Overlay> overlaySupplier) {
        REGISTRY.put(identifier, overlaySupplier);
    }

    public static Overlay get(Identifier identifier) {
        Supplier<Overlay> result = REGISTRY.get(identifier);
        if (result == null) {
//            return new InvalidOverlay();
            return null; //TODO: Implement InvalidOverlay
        }

        return result.get();
    }
}
