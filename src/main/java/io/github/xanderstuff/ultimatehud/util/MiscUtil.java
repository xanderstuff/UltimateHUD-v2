package io.github.xanderstuff.ultimatehud.util;

import net.minecraft.entity.player.PlayerEntity;

public class MiscUtil {
    public static int calculateTotalXP(PlayerEntity player) {
        return calculateTotalXP(player.experienceLevel, player.getNextLevelExperience(), player.experienceProgress);
    }

    public static int calculateTotalXP(int experienceLevel, int nextLevelExperience, float experienceProgress) {
        // copied from the mod "XPDisplay" by xanderstuff
        //TODO add link to XPDisplay mod
        // total experience equation is from http://web.archive.org/web/20200714234013/https://minecraft.gamepedia.com/Experience#Leveling_up
        // note: client.player.totalExperience gives the all experience ever gained in the world; does not decrement when xp gets used

        int totalLevelExperience = 0;
        if (0 <= experienceLevel && experienceLevel <= 16) {
            totalLevelExperience += Math.round(experienceLevel * experienceLevel + 6 * experienceLevel);
        } else if (17 <= experienceLevel && experienceLevel <= 31) {
            totalLevelExperience += Math.round(2.5F * experienceLevel * experienceLevel - 40.5F * experienceLevel + 360);
        } else {
            totalLevelExperience += Math.round(4.5F * experienceLevel * experienceLevel - 162.5F * experienceLevel + 2220);
        }
        return totalLevelExperience + (int) Math.floor(experienceProgress * nextLevelExperience);
    }
}
