package me.drawethree.buildbattle.objects.bbobjects.gui;

import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawethree.buildbattle.utils.compatbridge.model.CompSound;
import org.bukkit.entity.Player;

public class ClearPlotGUI extends ConfirmationGUI {

    private BBPlot plot;

    public ClearPlotGUI(BBPlot plot) {
        super("Are you sure?", plot.getArena().getPlugin().getOptionsManager().getClearPlotItem());
        this.plot = plot;
    }

    @Override
    protected void onYesClick(Player player) {
        this.plot.resetPlotFromGame();
        player.closeInventory();
        player.playSound(player.getLocation(), CompSound.EXPLODE.getSound(), 1.0f, 1.0f);
    }

    @Override
    protected void onNoClick(Player player) {
        this.plot.getArena().getPlugin().getOptionsManager().openOptionsInventory(player, this.plot);
        player.playSound(player.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);

    }
}
