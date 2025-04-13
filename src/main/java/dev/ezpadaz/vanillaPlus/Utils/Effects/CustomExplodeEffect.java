package dev.ezpadaz.vanillaPlus.Utils.Effects;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleUtil;
import de.slikey.effectlib.util.RandomUtils;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class CustomExplodeEffect extends Effect {
    public Particle particle1 = ParticleUtil.getParticle("EXPLOSION_NORMAL");
    public Particle particle2 = ParticleUtil.getParticle("EXPLOSION_HUGE");

    public int amount = 25;
    public Sound sound = Sound.ENTITY_GENERIC_EXPLODE;

    public CustomExplodeEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.INSTANT;
        speed = 0.5F;
    }

    @Override
    public void onRun() {
        Location location = getLocation();

        if (location == null || location.getWorld() == null) {
            cancel();
            return;
        }

        GeneralHelper.playSound(sound, location);
        display(particle1, location);
        display(particle2, location);
    }
}
