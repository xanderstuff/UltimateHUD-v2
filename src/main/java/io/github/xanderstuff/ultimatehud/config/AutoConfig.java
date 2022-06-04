package io.github.xanderstuff.ultimatehud.config;

import io.github.xanderstuff.ultimatehud.UltimateHud;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Based on MidnightConfig v2.1.0 by TeamMidnightDust & Motschen
 * https://github.com/TeamMidnightDust/MidnightLib/blob/main/src/main/java/eu/midnightdust/lib/config/MidnightConfig.java
 * <p>
 * MidnightConfig is based on https://github.com/Minenash/TinyConfig (Credits to Minenash)
 */

@SuppressWarnings("unchecked")
public abstract class AutoConfig {
    private static final Pattern INTEGER_ONLY = Pattern.compile("(-?[0-9]*)");
    private static final Pattern DECIMAL_ONLY = Pattern.compile("-?([\\d]+\\.?[\\d]*|[\\d]*\\.?[\\d]+|\\.)");
    private static final Pattern HEX_COLOR_ONLY = Pattern.compile("(-?[#0-9a-fA-F]*)");

    private static final List<ConfigEntryData> configEntries = new ArrayList<>();

    protected static class ConfigEntryData {
        Field field;
        Object widget;
        int maxLength;
        int max;
        Map.Entry<TextFieldWidget, Text> error;
        Object defaultValue;
        Object value;
        String tempValue;
        boolean inLimits = true;
        String id;
        Text name;
        int index;
        ClickableWidget colorButton;
    }


    public static void init(String configurableName, Class<?> configurable, Object instance) {
        for (Field field : configurable.getFields()) {
            ConfigEntryData configEntryData = new ConfigEntryData();
            if (field.isAnnotationPresent(ConfigEntry.class) || field.isAnnotationPresent(ConfigComment.class))
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
                    initClient(configurableName, field, configEntryData);
            if (field.isAnnotationPresent(ConfigEntry.class)) try {
                configEntryData.defaultValue = field.get(instance);
            } catch (IllegalAccessException ignored) {
            }
        }

        for (ConfigEntryData configEntryData : configEntries) {
            if (configEntryData.field.isAnnotationPresent(ConfigEntry.class)) try {
                configEntryData.value = configEntryData.field.get(instance);
                configEntryData.tempValue = configEntryData.value.toString();
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private static void initClient(String configurableName, Field field, ConfigEntryData configEntryData) {
        Class<?> type = field.getType();
        ConfigEntry configEntryAnnotation = field.getAnnotation(ConfigEntry.class);
        configEntryData.maxLength = configEntryAnnotation != null ? configEntryAnnotation.maxLength() : 0;
        configEntryData.field = field;
        configEntryData.id = configurableName;

        if (configEntryAnnotation != null) {
            if (!configEntryAnnotation.name().equals("")) configEntryData.name = new TranslatableText(configEntryAnnotation.name());
            if (type == int.class) useTextFieldWidget(configEntryData, Integer::parseInt, INTEGER_ONLY, (int) configEntryAnnotation.min(), (int) configEntryAnnotation.max(), true);
            else if (type == float.class)
                useTextFieldWidget(configEntryData, Float::parseFloat, DECIMAL_ONLY, (float) configEntryAnnotation.min(), (float) configEntryAnnotation.max(), false);
            else if (type == double.class) useTextFieldWidget(configEntryData, Double::parseDouble, DECIMAL_ONLY, configEntryAnnotation.min(), configEntryAnnotation.max(), false);
            else if (type == String.class || type == List.class) {
                configEntryData.max = configEntryAnnotation.max() == Double.MAX_VALUE ? Integer.MAX_VALUE : (int) configEntryAnnotation.max();
                useTextFieldWidget(configEntryData, String::length, null, Math.min(configEntryAnnotation.min(), 0), Math.max(configEntryAnnotation.max(), 1), true);
            } else if (type == boolean.class) {
                Function<Object, Text> textFunction = value -> new LiteralText((Boolean) value ? "True" : "False").formatted((Boolean) value ? Formatting.GREEN : Formatting.RED);
                configEntryData.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
                    configEntryData.value = !(Boolean) configEntryData.value;
                    button.setMessage(textFunction.apply(configEntryData.value));
                }, textFunction);
            } else if (type.isEnum()) {
                List<?> values = Arrays.asList(field.getType().getEnumConstants());
                Function<Object, Text> textFunction = value -> new TranslatableText("config." + UltimateHud.MODID + "." + configurableName + ".enum." + configEntryData.field.getType().getSimpleName() + "." + configEntryData.value.toString());
                configEntryData.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
                    int index = values.indexOf(configEntryData.value) + 1;
                    configEntryData.value = values.get(index >= values.size() ? 0 : index);
                    button.setMessage(textFunction.apply(configEntryData.value));
                }, textFunction);
            }
        }
        configEntries.add(configEntryData);
    }

    private static void useTextFieldWidget(ConfigEntryData configEntryData, Function<String, Number> parseNumberFunction, Pattern pattern, double min, double max, boolean cast) {
        configEntryData.widget = (BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) (textFieldWidget, buttonWidget) -> inputString -> {
            boolean isNumber = pattern != null;

            inputString = inputString.trim();
            if (!(inputString.isEmpty() || !isNumber || pattern.matcher(inputString).matches())) return false;

            Number value = 0;
            boolean inLimits = false;
            configEntryData.error = null;
            if (!(isNumber && inputString.isEmpty()) && !inputString.equals("-") && !inputString.equals(".")) {
                value = parseNumberFunction.apply(inputString);
                inLimits = value.doubleValue() >= min && value.doubleValue() <= max;
                configEntryData.error = inLimits ? null : new AbstractMap.SimpleEntry<>(textFieldWidget, new LiteralText(value.doubleValue() < min ? "§cMinimum " + (isNumber ? "value" : "length") + (cast ? " is " + (int) min : " is " + min) : "§cMaximum " + (isNumber ? "value" : "length") + (cast ? " is " + (int) max : " is " + max)));
            }

            configEntryData.tempValue = inputString;
            textFieldWidget.setEditableColor(inLimits ? 0xFFFFFFFF : 0xFFFF7777);
            configEntryData.inLimits = inLimits;
            buttonWidget.active = configEntries.stream().allMatch(entry -> entry.inLimits);

            if (inLimits && configEntryData.field.getType() != List.class) configEntryData.value = isNumber ? value : inputString;
            else if (inLimits) {
                if (((List<String>) configEntryData.value).size() == configEntryData.index) ((List<String>) configEntryData.value).add("");
                ((List<String>) configEntryData.value).set(configEntryData.index, Arrays.stream(configEntryData.tempValue.replace("[", "").replace("]", "").split(", ")).toList().get(0));
            }

            if (configEntryData.field.getAnnotation(ConfigEntry.class).isColor()) {
                if (!inputString.contains("#")) inputString = '#' + inputString;
                if (!HEX_COLOR_ONLY.matcher(inputString).matches()) return false;
                try {
                    configEntryData.colorButton.setMessage(new LiteralText("⬛").setStyle(Style.EMPTY.withColor(Color.decode(configEntryData.tempValue).getRGB())));
                } catch (Exception ignored) {
                }
            }
            return true;
        };
    }


    @Environment(EnvType.CLIENT)
    public static Screen getScreen(Screen parent, String configurableName, Object instance) {
        return new AutoConfigScreen(parent, configurableName, instance);
    }

    @Environment(EnvType.CLIENT)
    private static class AutoConfigScreen extends Screen {
        protected AutoConfigScreen(Screen parent, String configurableName, Object instance) {
            super(new TranslatableText("config." + UltimateHud.MODID + "." + configurableName + ".title"));
            this.parent = parent;
            this.configurableName = configurableName;
            this.translationPrefix = "config." + UltimateHud.MODID + "." + configurableName + ".";
            this.instance = instance;
        }

        private final String translationPrefix;
        private final Screen parent;
        private final String configurableName;
        private final Object instance;
        private AutoConfigListWidget listWidget;
        private boolean reload = false;

        private void loadValues() {
            for (ConfigEntryData configEntryData : configEntries) {
                if (configEntryData.field.isAnnotationPresent(ConfigEntry.class)) try {
                    configEntryData.value = configEntryData.field.get(instance);
                    configEntryData.tempValue = configEntryData.value.toString();
                } catch (IllegalAccessException ignored) {
                }
            }
        }

        @Override
        public void tick() {
            super.tick();
            // Real Time config update
            for (ConfigEntryData configEntryData : configEntries) {
                try {
                    configEntryData.field.set(instance, configEntryData.value);
                } catch (IllegalAccessException ignored) {
                }
            }
        }

        @Override
        protected void init() {
            super.init();
            if (!reload) loadValues();

            this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> {
                loadValues();
                Objects.requireNonNull(client).setScreen(parent);
            }));

            ButtonWidget done = this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 28, 150, 20, ScreenTexts.DONE, (button) -> {
                for (ConfigEntryData configEntryData : configEntries)
                    if (configEntryData.id.equals(configurableName)) {
                        try {
                            configEntryData.field.set(instance, configEntryData.value);
                        } catch (IllegalAccessException ignored) {
                        }
                    }
                Objects.requireNonNull(client).setScreen(parent);
            }));

            this.listWidget = new AutoConfigListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
            if (this.client != null && this.client.world != null) this.listWidget.setRenderBackground(false);
            this.addSelectableChild(this.listWidget);
            for (ConfigEntryData configEntryData : configEntries) {
                if (configEntryData.id.equals(configurableName)) {
                    Text name = Objects.requireNonNullElseGet(configEntryData.name, () -> new TranslatableText(translationPrefix + configEntryData.field.getName()));
                    ButtonWidget resetButton = new ButtonWidget(width - 205, 0, 40, 20, new TranslatableText("Reset"), (button -> {
                        configEntryData.value = configEntryData.defaultValue;
                        configEntryData.tempValue = configEntryData.defaultValue.toString();
                        configEntryData.index = 0;
                        double scrollAmount = listWidget.getScrollAmount();
                        this.reload = true;
                        Objects.requireNonNull(client).setScreen(this);
                        listWidget.setScrollAmount(scrollAmount);
                    }));

                    if (configEntryData.widget instanceof Map.Entry) {
                        Map.Entry<ButtonWidget.PressAction, Function<Object, Text>> widget = (Map.Entry<ButtonWidget.PressAction, Function<Object, Text>>) configEntryData.widget;
                        if (configEntryData.field.getType().isEnum())
                            widget.setValue(value -> new TranslatableText(translationPrefix + "enum." + configEntryData.field.getType().getSimpleName() + "." + configEntryData.value.toString()));
                        this.listWidget.addButton(List.of(new ButtonWidget(width - 160, 0, 150, 20, widget.getValue().apply(configEntryData.value), widget.getKey()), resetButton), name);
                    } else if (configEntryData.field.getType() == List.class) {
                        if (!reload) configEntryData.index = 0;
                        TextFieldWidget widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, null);
                        widget.setMaxLength(configEntryData.maxLength);
                        if (configEntryData.index < ((List<String>) configEntryData.value).size())
                            widget.setText((String.valueOf(((List<String>) configEntryData.value).get(configEntryData.index))));
                        else widget.setText("");
                        Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) configEntryData.widget).apply(widget, done);
                        widget.setTextPredicate(processor);
                        resetButton.setWidth(20);
                        resetButton.setMessage(new LiteralText("R"));
                        ButtonWidget cycleButton = new ButtonWidget(width - 185, 0, 20, 20, new LiteralText(String.valueOf(configEntryData.index)).formatted(Formatting.GOLD), (button -> {
                            ((List<String>) configEntryData.value).remove("");
                            double scrollAmount = listWidget.getScrollAmount();
                            this.reload = true;
                            configEntryData.index = configEntryData.index + 1;
                            if (configEntryData.index > ((List<String>) configEntryData.value).size()) configEntryData.index = 0;
                            Objects.requireNonNull(client).setScreen(this);
                            listWidget.setScrollAmount(scrollAmount);
                        }));
                        this.listWidget.addButton(List.of(widget, resetButton, cycleButton), name);
                    } else if (configEntryData.widget != null) {
                        TextFieldWidget widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, null);
                        widget.setMaxLength(configEntryData.maxLength);
                        widget.setText(configEntryData.tempValue);
                        Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) configEntryData.widget).apply(widget, done);
                        widget.setTextPredicate(processor);
                        if (configEntryData.field.getAnnotation(ConfigEntry.class).isColor()) {
                            resetButton.setWidth(20);
                            resetButton.setMessage(new LiteralText("R"));
                            ButtonWidget colorButton = new ButtonWidget(width - 185, 0, 20, 20, new LiteralText("⬛"), (button -> {
                            }));
                            colorButton.setMessage(new LiteralText("⬛").setStyle(Style.EMPTY.withColor(DrawUtil.decodeARGB(configEntryData.tempValue))));

                            configEntryData.colorButton = colorButton;
                            this.listWidget.addButton(List.of(widget, colorButton, resetButton), name);
                        } else this.listWidget.addButton(List.of(widget, resetButton), name);
                    } else {
                        this.listWidget.addButton(List.of(), name);
                    }
                }
            }
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            this.listWidget.render(matrices, mouseX, mouseY, delta);
            drawCenteredText(matrices, textRenderer, title, width / 2, 15, 0xFFFFFF);

            for (ConfigEntryData configEntryData : configEntries) {
                if (configEntryData.id.equals(configurableName)) {
                    if (listWidget.getHoveredButton(mouseX, mouseY).isPresent()) {
                        ClickableWidget buttonWidget = listWidget.getHoveredButton(mouseX, mouseY).get();
                        Text text = ButtonEntry.buttonsWithText.get(buttonWidget);
                        Text name = new TranslatableText(this.translationPrefix + configEntryData.field.getName());
                        String key = translationPrefix + configEntryData.field.getName() + ".tooltip";

                        if (configEntryData.error != null && text.equals(name))
                            renderTooltip(matrices, configEntryData.error.getValue(), mouseX, mouseY);
                        else if (I18n.hasTranslation(key) && text.equals(name)) {
                            List<Text> list = new ArrayList<>();
                            for (String str : I18n.translate(key).split("\n"))
                                list.add(new LiteralText(str));
                            renderTooltip(matrices, list, mouseX, mouseY);
                        }
                    }
                }
            }
            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class AutoConfigListWidget extends ElementListWidget<ButtonEntry> {
        TextRenderer textRenderer;

        public AutoConfigListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
            super(minecraftClient, i, j, k, l, m);
            this.centerListVertically = false;
            textRenderer = minecraftClient.textRenderer;
        }

        @Override
        public int getScrollbarPositionX() {
            return this.width - 7;
        }

        public void addButton(List<ClickableWidget> buttons, Text text) {
            this.addEntry(ButtonEntry.create(buttons, text));
        }

        @Override
        public int getRowWidth() {
            return 10000;
        }

        public Optional<ClickableWidget> getHoveredButton(double mouseX, double mouseY) {
            for (ButtonEntry buttonEntry : this.children()) {
                if (!buttonEntry.buttons.isEmpty() && buttonEntry.buttons.get(0).isMouseOver(mouseX, mouseY)) {
                    return Optional.of(buttonEntry.buttons.get(0));
                }
            }
            return Optional.empty();
        }
    }

    public static class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {
        private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        public final List<ClickableWidget> buttons;
        private final Text text;
        private final List<ClickableWidget> children = new ArrayList<>();
        public static final Map<ClickableWidget, Text> buttonsWithText = new HashMap<>();

        private ButtonEntry(List<ClickableWidget> buttons, Text text) {
            if (!buttons.isEmpty()) buttonsWithText.put(buttons.get(0), text);
            this.buttons = buttons;
            this.text = text;
            children.addAll(buttons);
        }

        public static ButtonEntry create(List<ClickableWidget> buttons, Text text) {
            return new ButtonEntry(buttons, text);
        }

        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            buttons.forEach(button -> {
                button.y = y;
                button.render(matrices, mouseX, mouseY, tickDelta);
            });
            if (text != null && (!text.getString().contains("spacer") || !buttons.isEmpty()))
                DrawableHelper.drawTextWithShadow(matrices, textRenderer, text, 12, y + 5, 0xFFFFFF);
        }

        public List<? extends Element> children() {
            return children;
        }

        public List<? extends Selectable> selectableChildren() {
            return children;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ConfigEntry {
        int maxLength() default 100;

        double min() default Double.MIN_NORMAL;

        double max() default Double.MAX_VALUE;

        String name() default "";

        boolean isColor() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ConfigComment {
    }
}