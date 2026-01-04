package dev.ezpadaz.vanillaPlus.Features.DoubleXP.Listener;

import dev.aurelium.auraskills.api.event.skill.DamageXpGainEvent;
import dev.aurelium.auraskills.api.event.skill.EntityXpGainEvent;
import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Locale;

public class ASDoubleXPListener implements Listener {

    @EventHandler
    public void onPlayerSkillExpChange(XpGainEvent event){
        event.setAmount(gainExpHandler(event.getAmount()));
    }

    @EventHandler
    public void onPlayerDamageSkillExpChange(DamageXpGainEvent event) {
        event.setAmount(gainExpHandler(event.getAmount()));
    }

    @EventHandler
    public void onEntitySkillExpChange(EntityXpGainEvent event) {
        event.setAmount(gainExpHandler(event.getAmount()));
    }


    public double gainExpHandler(double amount){
        int multiplier = GeneralHelper.getConfigInt("features.double-xp.multiplier");
        boolean isEnabled = DoubleXP.isEventEnabled();
        if(isEnabled) return (amount * multiplier);
        return amount;
    }
}
