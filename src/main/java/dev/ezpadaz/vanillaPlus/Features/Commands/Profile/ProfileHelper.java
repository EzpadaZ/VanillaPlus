package dev.ezpadaz.vanillaPlus.Features.Commands.Profile;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import org.bukkit.Statistic;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;

import java.util.Objects;


public class ProfileHelper {
    public static double getHealth(Player player) {
        return GeneralHelper.formatDouble(player.getHealth());
    }

    public static double getMaxHealth(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        return attr != null ? GeneralHelper.formatDouble(attr.getValue()) : 0.0; // fallback to errored out health
    }

    public static double getMana(Player player) {
        boolean auraSkills = GeneralHelper.isPluginPresent("AuraSkills");

        if(auraSkills) {
            return AuraSkillsApi.get().getUserManager().getUser(player.getUniqueId()).getMana();
        }

        return 0.0;
    }

    public static double getFoodSaturation(Player player) {
        return GeneralHelper.formatDouble(player.getSaturation());
    }

    public static double getHunger(Player player) {
        return GeneralHelper.formatDouble(player.getFoodLevel());
    }

    public static String getPlaytime(Player player) {
        int ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long totalSeconds = ticks / 20L;

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
}
