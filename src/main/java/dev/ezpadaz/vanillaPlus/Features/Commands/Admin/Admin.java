package dev.ezpadaz.vanillaPlus.Features.Commands.Admin;

import dev.ezpadaz.vanillaPlus.Features.Commands.Admin.Commands.AdminCommand;
import dev.ezpadaz.vanillaPlus.Features.Commands.Admin.Listeners.AdminInventoryListener;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Admin {
    public static void initialize() {
        if (!GeneralHelper.getConfigBool("features.admin.enabled")) {
            return;
        }

        // Register commands and listeners.
        VanillaPlus.getInstance().commandManager.getCommandCompletions().registerAsyncCompletion("op_or_owner", c -> {
            Player sender = (Player) c.getSender();
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.isOp() || p.getName().equalsIgnoreCase(GeneralHelper.getConfigString("owner")))
                    .map(Player::getName)
                    .toList();
        });

        GeneralHelper.registerCommand(new AdminCommand());
        GeneralHelper.registerListener(new AdminInventoryListener());
    }
}
