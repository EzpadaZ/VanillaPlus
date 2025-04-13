package dev.ezpadaz.vanillaPlus.Features.Arbiter.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.ezpadaz.vanillaPlus.Features.Arbiter.Core.Watcher;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static dev.ezpadaz.vanillaPlus.Features.Arbiter.Core.Watcher.arbiterPrefix;

@CommandAlias("arbiter|arb")
@Description("Arbiter Control")
public class ControlCommand extends BaseCommand {

    @Subcommand("info|i")
    @Description("Gets information about the current watcher status")
    public void onInfoCommand(CommandSender sender) {
        Player p = (Player) sender;
        double[] tps = Watcher.getInstance().getTPS();
        List<String> strings = new ArrayList<>();
        double t1 = GeneralHelper.formatDouble(tps[0]);
        double t2 = GeneralHelper.formatDouble(tps[1]);
        strings.add("Status: &a[" + Watcher.getInstance().getStatus() + "&a]");
        strings.add("Task ID: &a[&c" + Watcher.getInstance().getTaskID() + "&a]");
        strings.add("TPS [1M]: &a[" + (t1 > Watcher.getInstance().WARNING_TPS ? "&a" + t1 : "&e" + t1) + "]");
        strings.add("TPS [5M]: &a[" + ((t2 > Watcher.getInstance().MIN_TPS)
                ? (t2 < Watcher.getInstance().WARNING_TPS ? "&e" + t2 : "&a" + t2)
                : "&c" + t2) + "]");
        strings.add("PLAYERS: &a[" + VanillaPlus.getInstance().getServer().getOnlinePlayers().size() + "]");
        strings.add("MEMORY: &a["+Watcher.getInstance().getUsedMemory()+"&a]");
        strings.add("REPORT: &a[" + Watcher.getInstance().getStatus() + "&a]");
        MessageHelper.sendMultipleMessage(p, "&6Arbiter Report", "OS", strings);
    }

    @Subcommand("disable|d")
    @Description("Terminates the watcher, disables safeguards")
    public void onDisableCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!GeneralHelper.isPlayerAllowed(p)) return;
        Watcher.getInstance().stopWatcher();
        MessageHelper.send(p, (arbiterPrefix + "&cel servicio de rendimiento ha sido apagado."));
    }

    @Subcommand("reload|r")
    @Description("Reloads the watcher")
    public void onReloadCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!GeneralHelper.isPlayerAllowed(p)) return;
        Watcher.getInstance().stopWatcher();
        Watcher.getInstance().reloadWatcher();
        Watcher.getInstance().startProtection();
        MessageHelper.send(p, (arbiterPrefix + "&cel servicio de rendimiento ha sido reiniciado."));
    }

    @Subcommand("enable|e")
    @Description("Enables the watcher and turns on the safeguard")
    public void onEnableCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!GeneralHelper.isPlayerAllowed(p)) return;
        Watcher.getInstance().initialize();
        Watcher.getInstance().startProtection();
        MessageHelper.send(p, (arbiterPrefix + "&cel servicio de rendimiento ha sido iniciado."));
    }

    @Subcommand("debug|db")
    @Description("Turns on debug on console")
    @CommandCompletion("true|false")
    public void onDebugCommand(CommandSender sender, boolean enabled) {
        Player p = (Player) sender;

        if (!GeneralHelper.isPlayerAllowed(p)) return;

        Watcher.watcherDebugMode = enabled;
        MessageHelper.send(p, (arbiterPrefix + "&cse inicio el logging en consola."));
    }

}
