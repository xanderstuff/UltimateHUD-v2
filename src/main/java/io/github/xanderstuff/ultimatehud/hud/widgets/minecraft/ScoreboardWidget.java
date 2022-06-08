package io.github.xanderstuff.ultimatehud.hud.widgets.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import io.github.xanderstuff.ultimatehud.config.AutoConfig;
import io.github.xanderstuff.ultimatehud.hud.Widget;
import io.github.xanderstuff.ultimatehud.registry.WidgetRegistry;
import io.github.xanderstuff.ultimatehud.util.DrawUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardWidget extends Widget {
    public static final Identifier IDENTIFIER = new Identifier("minecraft", "scoreboard");
    private static final ScoreboardWidget INSTANCE = new ScoreboardWidget();

    @Expose
    @AutoConfig.ConfigEntry
    public boolean hideScores = false;
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String headerTextColor = "#FFFFFF";
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String playerTextColor = "#FFFFFF";
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String scoreTextColor = "#FF5555"; // this is the value of Formatting.RED.getColorValue()
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String headerBackgroundColor = "#000000"; //#66000000
    @Expose
    @AutoConfig.ConfigEntry(min = 0.0, max = 1.0)
    public float headerBackgroundOpacity = 0.4F;
    @Expose
    @AutoConfig.ConfigEntry(isColor = true, maxLength = 7)
    public String backgroundColor = "#000000"; //#4C000000
    @Expose
    @AutoConfig.ConfigEntry(min = 0.0, max = 1.0)
    public float backgroundOpacity = 0.3F;


    static {
        WidgetRegistry.register(IDENTIFIER, ScoreboardWidget::getInstance);
    }


    private ScoreboardWidget() {
        //populate with defaults
    }

    public static ScoreboardWidget getInstance() {
        return INSTANCE;
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public double getWidth(PlayerEntity player) {
        Scoreboard scoreboard = player.world.getScoreboard();
        ScoreboardObjective tempObjective = null;
        Team team = scoreboard.getPlayerTeam(player.getEntityName());
        if (team != null) {
            int colorIndex = team.getColor().getColorIndex();
            if (colorIndex >= 0) {
                tempObjective = scoreboard.getObjectiveForSlot(3 + colorIndex);
            }
        }

        ScoreboardObjective objective = tempObjective != null ? tempObjective : scoreboard.getObjectiveForSlot(1);
        if (objective == null) {
            // scoreboard is not rendered
            return 0;
        }

        // before InGameHud#renderScoreboardSidebar call   ^
        // inside InGameHud#renderScoreboardSidebar method v

        Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
        List<ScoreboardPlayerScore> list = collection.stream()
                .filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());
        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }

        if (list.size() == 0) {
            // if there's no entries in the scoreboard, it is hidden
            return 0;
        }

        int maxEntryWidth = DrawUtil.getTextRenderer().getWidth(objective.getDisplayName());
        int spacing = DrawUtil.getTextRenderer().getWidth(": ");
        for (ScoreboardPlayerScore scoreboardPlayerScore : collection) {
            Team playerTeam = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            Text playerName = Team.decorateName(playerTeam, new LiteralText(scoreboardPlayerScore.getPlayerName()));
            if (hideScores) {
                maxEntryWidth = Math.max(maxEntryWidth, DrawUtil.getTextRenderer().getWidth(playerName));
            } else {
                maxEntryWidth = Math.max(maxEntryWidth, DrawUtil.getTextRenderer().getWidth(playerName) + spacing + DrawUtil.getTextRenderer().getWidth(Integer.toString(scoreboardPlayerScore.getScore())));
            }
        }

        return maxEntryWidth + 4;
    }

    @Override
    public double getHeight(PlayerEntity player) {
        Scoreboard scoreboard = player.world.getScoreboard();
        ScoreboardObjective tempObjective = null;
        Team team = scoreboard.getPlayerTeam(player.getEntityName());
        if (team != null) {
            int colorIndex = team.getColor().getColorIndex();
            if (colorIndex >= 0) {
                tempObjective = scoreboard.getObjectiveForSlot(3 + colorIndex);
            }
        }

        ScoreboardObjective objective = tempObjective != null ? tempObjective : scoreboard.getObjectiveForSlot(1);
        if (objective == null) {
            // if there's no objective currently set, so the scoreboard is hidden
            return 0;
        }

        var numberOfEntries = scoreboard.getAllPlayerScores(objective).size();
        if (numberOfEntries == 0) {
            // if there's an objective set but no entries to display, then the scoreboard is also hidden
            return 0;
        } else {
            return numberOfEntries * 9 + 10; // +10 pixels for the header (which shows the objective's displayName)
        }
    }

    @Override
    public boolean isSingleInstance() {
        return true;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float tickDelta, PlayerEntity player) {
        //no op, rendering is handled in minecraft's InGameHud
    }
}
