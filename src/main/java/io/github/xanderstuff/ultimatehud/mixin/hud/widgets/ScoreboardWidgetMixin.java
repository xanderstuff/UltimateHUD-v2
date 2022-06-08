package io.github.xanderstuff.ultimatehud.mixin.hud.widgets;

import io.github.xanderstuff.ultimatehud.hud.widgets.minecraft.ScoreboardWidget;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class ScoreboardWidgetMixin {
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "renderScoreboardSidebar")
    private void ultimatehud$ScoreboardWidgetMixin$renderPre(MatrixStack matrixStack, ScoreboardObjective objective, CallbackInfo ci) {
        int originalX = scaledWidth - (int) ScoreboardWidget.getInstance().getWidth(client.player) - 1;
        int originalY = (int) ((scaledHeight * 0.5) - (ScoreboardWidget.getInstance().getHeight(client.player) * 0.666666) - 3);
        int newX = (int) ScoreboardWidget.getInstance().cachedPosition.x - originalX;
        int newY = (int) ScoreboardWidget.getInstance().cachedPosition.y - originalY;

        matrixStack.push();
        matrixStack.translate(newX, newY, 0);
    }

    @Inject(at = @At("RETURN"), method = "renderScoreboardSidebar")
    private void ultimatehud$ScoreboardWidgetMixin$renderPost(MatrixStack matrixStack, ScoreboardObjective objective, CallbackInfo ci) {
        matrixStack.pop();
    }


    @ModifyArgs(method = "renderScoreboardSidebar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", ordinal = 0))
    private void ultimatehud$ScoreboardWidgetMixin$playerText(Args args) {
//        int newColor = MathHelper.hsvToRgb(DrawUtil.timeMillis() * 0.0005F % 1.0F, 0.8F, 1.0F);
//        args.set(4, newColor);
        args.set(4, DrawUtil.decodeARGB(ScoreboardWidget.getInstance().playerTextColor));
    }


    @ModifyArgs(method = "renderScoreboardSidebar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", ordinal = 1))
    private void ultimatehud$ScoreboardWidgetMixin$headerText(Args args) {
//        int newColor = MathHelper.hsvToRgb(DrawUtil.timeMillis() * 0.0005F % 1.0F, 0.8F, 1.0F);
//        args.set(4, newColor);
        args.set(4, DrawUtil.decodeARGB(ScoreboardWidget.getInstance().headerTextColor));
    }


    @ModifyArgs(method = "renderScoreboardSidebar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I", ordinal = 0))
    private void ultimatehud$ScoreboardWidgetMixin$scoreText(Args args) {
        String scoreString;
        if (ScoreboardWidget.getInstance().hideScores) {
            scoreString = "";
        } else {
            // Apparently the scoreboard scores are prefixed with Formatting.RED (§c) to make it red, rather than just setting the text color.
            // We need to take out the formatting code because it overrides the text color
            scoreString = ((String) args.get(1)).replaceFirst("§c", "");
        }
        args.set(1, scoreString);


//        int newColor = MathHelper.hsvToRgb(DrawUtil.timeMillis() * 0.0005F % 1.0F, 0.8F, 1.0F);
//        args.set(4, newColor);
        args.set(4, DrawUtil.decodeARGB(ScoreboardWidget.getInstance().scoreTextColor));
    }


    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 0))
    private int ultimatehud$ScoreboardWidgetMixin$scoreTextSize(TextRenderer textRenderer, String string) {
        if (ScoreboardWidget.getInstance().hideScores) {
            return 0; // set width to 0
        }
        return textRenderer.getWidth(string);
    }


    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 1))
    private int ultimatehud$ScoreboardWidgetMixin$scoreTextSize2(TextRenderer textRenderer, String string) {
        if (ScoreboardWidget.getInstance().hideScores) {
            return 0; // set width to 0
        }
        return textRenderer.getWidth(string);
    }


    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I", ordinal = 0))
    private int ultimatehud$ScoreboardWidgetMixin$scoreboardBodyBackgroundColor(GameOptions gameOptions, float fallbackOpacity) {
//        int newColor = MathHelper.hsvToRgb(DrawUtil.timeMillis() * 0.0005F % 1.0F, 0.8F, 0.6F);
//        return DrawUtil.toArgb((int) (0.4 * 255), DrawUtil.getRed(newColor), DrawUtil.getGreen(newColor), DrawUtil.getBlue(newColor));
        var color = DrawUtil.decodeARGB(ScoreboardWidget.getInstance().backgroundColor);
        var opacity = ScoreboardWidget.getInstance().backgroundOpacity;
        return DrawUtil.setOpacity(color, opacity);
    }


    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(F)I", ordinal = 1))
    private int ultimatehud$ScoreboardWidgetMixin$scoreboardHeaderBackgroundColor(GameOptions gameOptions, float fallbackOpacity) {
//        int newColor = MathHelper.hsvToRgb(DrawUtil.timeMillis() * 0.0005F % 1.0F, 0.8F, 0.6F);
//        return DrawUtil.toArgb((int) (0.6 * 255), DrawUtil.getRed(newColor), DrawUtil.getGreen(newColor), DrawUtil.getBlue(newColor));

        var color = DrawUtil.decodeARGB(ScoreboardWidget.getInstance().headerBackgroundColor);
        var opacity = ScoreboardWidget.getInstance().headerBackgroundOpacity;
        return DrawUtil.setOpacity(color, opacity);
    }
}
