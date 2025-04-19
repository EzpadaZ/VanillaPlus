package dev.ezpadaz.vanillaPlus.Utils;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.*;
import de.slikey.effectlib.util.DynamicLocation;
import dev.ezpadaz.vanillaPlus.Utils.Effects.CustomExplodeEffect;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
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

    public void strikeLightning(Location location) {
        location.getWorld().strikeLightningEffect(location);
    }

    public void smokeEffect(Player jugador, int seconds) {
        CylinderEffect ce = new CylinderEffect(manager);
        ce.setEntity(jugador); // makes it follow the player

        ce.iterations = seconds * 20;
        ce.period = 1;

        ce.radius = 0.4f;       // tight around body
        ce.height = 1.6f;       // slightly under head height
        ce.particles = 15;
        ce.particle = Particle.CAMPFIRE_COSY_SMOKE;
        ce.particleOffsetY = 0.5f;

        ce.start();
    }

    public void smokeExplosionEffect(Player jugador) {
        SphereEffect se = new SphereEffect(manager) {
            int tick = 0;
            final int maxTicks = 10;

            @Override
            public void onRun() {
                radius = 0.5f + (tick * 0.30f); // expand radius each tick
                particle = Particle.CAMPFIRE_COSY_SMOKE;
                tick++;
                if (tick >= maxTicks) cancel();
                super.onRun();
            }
        };

        se.setLocation(jugador.getLocation().clone().add(0, 1, 0)); // center at chest
        se.iterations = 10; // run for 10 ticks
        se.period = 1;      // run every tick
        se.particles = 20;
        se.particleOffsetY = 0.0f;

        se.start();
    }

    public void smokeExplosionEffect(Location location) {
        SphereEffect se = new SphereEffect(manager) {
            int tick = 0;
            final int maxTicks = 10;

            @Override
            public void onRun() {
                radius = 0.5f + (tick * 0.30f); // expand radius each tick
                particle = Particle.CAMPFIRE_COSY_SMOKE;
                tick++;
                if (tick >= maxTicks) cancel();
                super.onRun();
            }
        };

        se.setLocation(location.clone().add(0, 1, 0)); // center at chest
        se.iterations = 10; // run for 10 ticks
        se.period = 1;      // run every tick
        se.particles = 20;
        se.particleOffsetY = 0.0f;

        se.start();
    }

    public void arcEffect(Player player, int seconds) {
        ArcEffect effect = new ArcEffect(manager);
        effect.setDynamicOrigin(new DynamicLocation(forwardFromPlayer(player, 3)));
        effect.setDynamicTarget(new DynamicLocation(forwardFromPlayer(player, 9)));
        effect.particles = 50;
        effect.particle = Particle.SOUL_FIRE_FLAME;
        ;
        effect.start();
        SchedulerHelper.scheduleTask(null, effect::cancel, seconds);
    }

    public void dnaEffect(Player player, int seconds) {
        DnaEffect effect = new DnaEffect(manager);
        effect.setDynamicOrigin(new DynamicLocation(forwardFromPlayer(player, 3)));
        effect.particleHelix = Particle.SOUL_FIRE_FLAME;
        effect.start();
        SchedulerHelper.scheduleTask(null, effect::cancel, seconds);
    }

    public void explodeEffect(Player player) {
        CustomExplodeEffect effect = new CustomExplodeEffect(manager);
        effect.setDynamicOrigin(new DynamicLocation(forwardFromPlayer(player, 3)));
        effect.start();
        SchedulerHelper.scheduleTask(null, () -> {
            if (effect.isDone()) return;
            Bukkit.getScheduler().runTask(VanillaPlus.getInstance(), () -> effect.cancel());
        }, 2);
    }

    public static Location forwardFromPlayer(Player player, int blocks) {
        return player.getLocation().add(0, 2, 0).add(player.getLocation().getDirection().multiply(blocks));
    }
}