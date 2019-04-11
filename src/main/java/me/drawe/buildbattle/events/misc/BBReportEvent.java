package me.drawe.buildbattle.events.misc;

import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class BBReportEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final Player reporter;
    private final List<Player> reportedPlayers;
    private final BBPlot reportedPlot;
    private final String schematicName;


    /**
     * Called when build report was successfully submitted
     *
     * @param reporter        Player who reported
     * @param reportedPlayers Players that were reported
     * @param reportedPlot    Plot that was reported
     * @param schematicName   Schematic file name that was created
     */
    public BBReportEvent(Player reporter, List<Player> reportedPlayers, BBPlot reportedPlot, String schematicName) {
        this.reporter = reporter;
        this.reportedPlayers = reportedPlayers;
        this.reportedPlot = reportedPlot;
        this.schematicName = schematicName;
    }


    public HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public Player getReporter() {
        return reporter;
    }

    public List<Player> getReportedPlayers() {
        return reportedPlayers;
    }

    public BBPlot getReportedPlot() {
        return reportedPlot;
    }

    public String getSchematicName() {
        return schematicName;
    }
}
