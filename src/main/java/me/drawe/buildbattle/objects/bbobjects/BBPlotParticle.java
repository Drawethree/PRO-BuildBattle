package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.GameManager;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BBPlotParticle {

    private BBPlot plot;
    private BBParticle particle;
    private Location location;
    private BukkitTask showParticleTask;

    public BBPlotParticle(BBPlot plot, BBParticle particle, Location loc){
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
                //particle.getEffect().displa
                //getLocation().getWorld().spawnParticle(particle.getEffect(),location,GameManager.getAmountParticleToSpawn(),GameManager.getParticleOffset(),GameManager.getParticleOffset(),GameManager.getParticleOffset());
                getParticle().getEffect().display((float) GameManager.getParticleOffset(),(float) GameManager.getParticleOffset(),(float) GameManager.getParticleOffset(),1F,GameManager.getAmountParticleToSpawn(), getLocation(),getPlot().getArena().getPlayers());
            }
        }.runTaskTimer(BuildBattle.getInstance(), 0L, (long) GameManager.getParticleRefreshTime()*20L);
    }

    public void stop() {
        showParticleTask.cancel();
    }
}
