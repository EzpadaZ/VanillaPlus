package dev.ezpadaz.vanillaPlus.Features.DoubleXP;

import dev.ezpadaz.vanillaPlus.Features.DoubleXP.Commands.DoubleXPCommand;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.Listener.ASDoubleXPListener;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.Listener.DoubleXPListener;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DoubleXP {
    private static boolean eventEnabled = false;

    public static ArrayList<String> optedPlayers = new ArrayList<>();

    public static void initialize() {
        if (!GeneralHelper.getConfigBool("features.double-xp.enabled")) {
            MessageHelper.console("&6DoubleXP &c[OFF]");
            return;
        }

        // Attach commands.
        GeneralHelper.registerCommand(new DoubleXPCommand());

        // Attach Listeners.
        if (GeneralHelper.isPluginPresent("AuraSkills")) {
            GeneralHelper.registerListener(new ASDoubleXPListener());
            MessageHelper.console("&6DoubleXP Integrated with &cAuraSkills");
        }
        GeneralHelper.registerListener(new DoubleXPListener());
        MessageHelper.console("&6DoubleXP &a[ON]");
    }

    public static boolean isPlayerOptedIn(String target) {
        for (String name : optedPlayers) {
            if (name.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEventEnabled() {
        return eventEnabled;
    }

    public static void setEventEnabled(boolean eventEnabled) {
        DoubleXP.eventEnabled = eventEnabled;
    }
}
