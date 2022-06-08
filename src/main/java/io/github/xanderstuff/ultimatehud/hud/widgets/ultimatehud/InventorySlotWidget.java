package io.github.xanderstuff.ultimatehud.hud.widgets.ultimatehud;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class InventorySlotWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("ultimate-hud", "inventory_slot");
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

    @Expose
    @AutoConfig.ConfigEntry
    public SlotType slotType = SlotType.ARMOUR_HEAD;
    //    @Expose
//    @AutoConfig.ConfigEntry
//    public int slotIndex = 0; //only applies to SlotType.HOTBAR or SlotType.INVENTORY
    //TODO setting for rotation style (like the hotbar)
    @Expose
    @AutoConfig.ConfigEntry
    public BackgroundTexture backgroundTexture = BackgroundTexture.ROUNDED;
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String backgroundColor = "#000000";
    @Expose
    @AutoConfig.ConfigEntry(min = 0.0, max = 1.0)
    public float backgroundOpacity = 0.4F;
    //    @Expose
//    @AutoConfig.ConfigEntry
//    public boolean drawOutlineIfSelected = true; //only applies to hotbar-type slots
//    @Expose
//    @AutoConfig.ConfigEntry
//    public boolean rotateItems = false;
    @Expose
    @AutoConfig.ConfigEntry
    public boolean hideIfEmpty = false;
    //    @Expose
//    @AutoConfig.ConfigEntry
//    public boolean showDurabilityBar = true;
//    @Expose
//    @AutoConfig.ConfigEntry
//    public boolean showItemCount = true;
    @Expose
    @AutoConfig.ConfigEntry
    public boolean showAndCountDurability = true;


    public enum SlotType {
        MAINHAND,
        OFFHAND,
        ARMOUR_HEAD,
        ARMOUR_CHEST,
        ARMOUR_LEGS,
        ARMOUR_FEET,
//        HOTBAR,
//        INVENTORY,
    }

    public enum BackgroundTexture {
        NONE,
        ROUNDED,
        SQUARE,
        SOLID_COLOR
    }

    static {
        WidgetRegistry.register(IDENTIFIER, InventorySlotWidget::new);
    }

    private InventorySlotWidget() {
        //populate with defaults
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public double getWidth(PlayerEntity player) {
        return 22;
    }

    @Override
    public double getHeight(PlayerEntity player) {
        return 22;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float tickDelta, PlayerEntity player) {
        ItemStack itemStack = getItemStack(player, slotType, 1);

        if (hideIfEmpty && itemStack.isEmpty()) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        switch (backgroundTexture) {
            case ROUNDED -> {
                RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
                // use the offhand slot texture
                DrawableHelper.drawTexture(matrixStack, x, y, 0, 24, 23, 22, 22, 256, 256);
            }
            case SQUARE -> {
                RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
                // use the left and right sides of the hotbar texture
                DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, 0, 11, 22, 256, 256);
                DrawableHelper.drawTexture(matrixStack, x + 11, y, 0, 171, 0, 11, 22, 256, 256);
            }
            case SOLID_COLOR -> {
                var color = DrawUtil.decodeARGB(backgroundColor);
                DrawUtil.drawBox(matrixStack, x, y, 22, 22, DrawUtil.setOpacity(color, backgroundOpacity));
            }
        }

        DrawUtil.getItemRenderer().renderInGui(itemStack, x + 3, y + 3);
//        DrawUtil.getItemRenderer().renderGuiItemIcon(itemStack, x + 3, y + 3); // I'm not sure what the difference is (perhaps this method is useful?)
        if (showAndCountDurability) {
            DrawUtil.getItemRenderer().renderGuiItemOverlay(DrawUtil.getTextRenderer(), itemStack, x + 3, y + 3);
        }
        RenderSystem.disableBlend();
    }

    private ItemStack getItemStack(PlayerEntity player, SlotType slotType, int slotIndex) {
        return switch (slotType) {
            case ARMOUR_HEAD -> player.getEquippedStack(EquipmentSlot.HEAD);
            case ARMOUR_CHEST -> player.getEquippedStack(EquipmentSlot.CHEST);
            case ARMOUR_LEGS -> player.getEquippedStack(EquipmentSlot.LEGS);
            case ARMOUR_FEET -> player.getEquippedStack(EquipmentSlot.FEET);
            case OFFHAND -> player.getEquippedStack(EquipmentSlot.OFFHAND);
            case MAINHAND -> player.getEquippedStack(EquipmentSlot.MAINHAND);
//            case HOTBAR -> inv.main.get(slotIndex % 9);
//            case INVENTORY -> inv.main.get((slotIndex - 9) % 27);
            default -> ItemStack.EMPTY;
        };
    }
}
