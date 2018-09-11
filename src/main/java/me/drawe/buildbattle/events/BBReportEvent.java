package me.drawe.buildbattle.events;

import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class BBReportEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private Player reporter;
    private List<Player> reportedPlayers;
    private BBPlot reportedPlot;
    private String schematicName;


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
