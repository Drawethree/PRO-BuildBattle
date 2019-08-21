package me.drawe.buildbattle.objects.bbobjects.plot;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.bbobjects.BBParticle;
import me.drawe.buildbattle.utils.ParticleEffect;
import me.drawe.buildbattle.utils.compatbridge.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BBPlotParticle {

    private BBPlot plot;
    private BBParticle particle;
    private Location location;
    private BukkitTask showParticleTask;

    public BBPlotParticle(BBPlot plot, BBParticle particle, Location loc) {
        this.particle = particle;
        this.plot = plot;
        this.location = loc;
    }

    public BBPlot getPlot() {
        return plot;
    }

    public BBParticle getParticle() {
        return particle;
    }

    public Location getLocation() {
        return location;
    }

    public void start() {
        showParticleTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_13)) {
                    if (particle.getEffect() == ParticleEffect.REDSTONE) {
                        location.getWorld().spawnParticle(Particle.valueOf(particle.getEffect().name()), location, BuildBattle.getInstance().getSettings().getAmountParticleToSpawn(), BuildBattle.getInstance().getSettings().getParticleOffset(), BuildBattle.getInstance().getSettings().getParticleOffset(), BuildBattle.getInstance().getSettings().getParticleOffset(), new Particle.DustOptions(Color.RED,1));
                    } else {
                        location.getWorld().spawnParticle(Particle.valueOf(particle.getEffect().name()), location, BuildBattle.getInstance().getSettings().getAmountParticleToSpawn(), BuildBattle.getInstance().getSettings().getParticleOffset(), BuildBattle.getInstance().getSettings().getParticleOffset(), BuildBattle.getInstance().getSettings().getParticleOffset());
                    }
                } else {
                    particle.getEffect().display((float) BuildBattle.getInstance().getSettings().getParticleOffset(), (float) BuildBattle.getInstance().getSettings().getParticleOffset(), (float) BuildBattle.getInstance().getSettings().getParticleOffset(), 1F, BuildBattle.getInstance().getSettings().getAmountParticleToSpawn(), location, Bukkit.getOnlinePlayers());
                }
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, (long) BuildBattle.getInstance().getSettings().getParticleRefreshTime() * 20L);
    }

    public void stop() {
        showParticleTask.cancel();
    }
}
