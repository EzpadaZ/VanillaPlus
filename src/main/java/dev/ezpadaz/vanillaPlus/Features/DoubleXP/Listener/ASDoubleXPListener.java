package dev.ezpadaz.vanillaPlus.Features.DoubleXP.Listener;

import dev.aurelium.auraskills.api.event.skill.XpGainEvent;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ASDoubleXPListener implements Listener {

    @EventHandler
    public void onPlayerSkillExpChange(XpGainEvent event){
        // Do The Stuff.
        int multiplier = GeneralHelper.getConfigInt("features.double-xp.multiplier");
        boolean isEnabled = GeneralHelper.getConfigBool("features.double-xp.enabled");

        if(isEnabled){
            double amount = event.getAmount();
            event.setAmount(amount * multiplier);
        }
    }
}
