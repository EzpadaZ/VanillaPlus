package dev.ezpadaz.vanillaPlus.Utils;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.ArcEffect;
import de.slikey.effectlib.effect.SphereEffect;
import de.slikey.effectlib.util.DynamicLocation;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class EffectHelper {
    private static EffectHelper instance;
    private static EffectManager manager;

    private EffectHelper() {
        manager = new EffectManager(VanillaPlus.getInstance());
    }

    public static EffectHelper getInstance() {
        if (instance == null) instance = new EffectHelper();
        return instance;
    }

    public void strikeLightning(Player jugador) {
        jugador.getWorld().strikeLightningEffect(jugador.getLocation());
    }

    public void smokeEffect(Player jugador, int seconds) {
        SphereEffect se = new SphereEffect(manager);
        se.setEntity(jugador);
        se.iterations = seconds * 20; // 20 ticks = 1 sec, * 2 = 2 seconds.
        se.particles *= 2;
        se.radius *= 2;
        se.particleOffsetY = -2.0f;
        se.yOffset = -2.0f;
        se.particle = Particle.CAMPFIRE_COSY_SMOKE;
        se.start();
    }

    public void arcEffect(Player player, int seconds) {
        BiFunction<Player, Integer, Location> xForwardFromPlayer = (p, x) -> player.getLocation().add(0,2,0).add(player.getLocation().getDirection().multiply(x));
        ArcEffect effect = new ArcEffect(manager);
        effect.setDynamicOrigin(new DynamicLocation(xForwardFromPlayer.apply(player, 3)));
        effect.setDynamicTarget(new DynamicLocation(xForwardFromPlayer.apply(player, 9)));
        effect.particles = 50;
        effect.particle = Particle.SOUL_FIRE_FLAME;;
        effect.start();
        SchedulerHelper.scheduleTask(null, effect::cancel, seconds);
    }
}