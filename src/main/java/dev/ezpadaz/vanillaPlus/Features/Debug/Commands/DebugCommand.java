package dev.ezpadaz.vanillaPlus.Features.Debug.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.ezpadaz.vanillaPlus.Utils.EffectHelper;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.Utils.SchedulerHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("vpdebug|vpd")
@Description("Test Commands")
public class DebugCommand extends BaseCommand {
    @Subcommand("te smoke")
    @Description("Test Effects On You")
    public void onTestSmokeEffectCommand(CommandSender sender) {
        Player target = (Player) sender;
        EffectHelper.getInstance().smokeEffect(target, 3);
    }

    @Subcommand("te arc")
    @Description("Test Effects On You")
    public void onTestArcEffectCommand(CommandSender sender) {
        Player target = (Player) sender;
        EffectHelper.getInstance().arcEffect(target, 3);
    }

    @Subcommand("te explode")
    @Description("Test Effects On You")
    public void onTestExplodeEffectCommand(CommandSender sender) {
        Player target = (Player) sender;
        EffectHelper.getInstance().explodeEffect(target);
    }

    @Subcommand("te dna")
    @Description("Test Effects On You")
    public void onTestDnaEffectCommand(CommandSender sender) {
        Player target = (Player) sender;
        EffectHelper.getInstance().dnaEffect(target, 3);
    }

    @Subcommand("te smokeBurst")
    @Description("Test Effects On You")
    public void onTestSmokeBurstEffectCommand(CommandSender sender) {
        Player target = (Player) sender;
        EffectHelper.getInstance().smokeExplosionEffect(target);
    }

    @Subcommand("te tp")
    @Description("Test Effects On You")
    public void onTestTPEffectCommand(CommandSender sender) {
        Player target = (Player) sender;
        EffectHelper.getInstance().smokeEffect(target, 3);
        GeneralHelper.playSound(Sound.BLOCK_BEACON_POWER_SELECT, target.getLocation());
        SchedulerHelper.scheduleTask(null, () -> {
            EffectHelper.getInstance().strikeLightning(target);
            EffectHelper.getInstance().explodeEffect(target);
            EffectHelper.getInstance().smokeExplosionEffect(target);
        }, 3);
    }

    @CommandCompletion("@sounds")
    @Subcommand("te sound")
    @Description("Test Sounds On You")
    public void onTestSoundCommand(CommandSender sender, String soundName) {
        Player target = (Player) sender;

        NamespacedKey key = NamespacedKey.fromString(soundName.toLowerCase(Locale.ROOT));
        Sound sound = key != null ? Registry.SOUNDS.get(key) : null;

        if (sound == null) {
            try {
                sound = Sound.valueOf(soundName.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (sound == null) {
            MessageHelper.send(target, "&cSonido inválido: &e" + soundName);
            return;
        }

        GeneralHelper.playSound(sound, target.getLocation());
        MessageHelper.send(target, "&aReproduciendo: &e" + sound);
    }
}
