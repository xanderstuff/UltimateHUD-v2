package io.github.xanderstuff.ultimatehud.registry;

import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.HotbarWidget;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WidgetRegistry {
    private static final Map<Identifier, Supplier<Widget>> REGISTRY = new LinkedHashMap<>();

    static {
        register(new Identifier("minecraft", "hotbar"), HotbarWidget::getInstance);

//        register(new Identifier(UltimateHud.MODID, "itemslot"), InventorySlotWidget::new);
    }

    public static void register(Identifier identifier, Supplier<Widget> widgetSupplier) {
        REGISTRY.put(identifier, widgetSupplier);
    }

    public static Widget get(Identifier identifier) {
        Supplier<Widget> result = REGISTRY.get(identifier);
        if (result == null) {
//            return new InvalidWidget();
            return null; //TODO: Implement InvalidWidget
        }

        return result.get();
    }
}
