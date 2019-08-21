package me.drawe.buildbattle.objects.bbobjects;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.MySQLManager;
import me.drawe.buildbattle.managers.ReportManager;
import me.drawe.buildbattle.mysql.MySQL;
import me.drawe.buildbattle.utils.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.swing.plaf.basic.BasicButtonUI;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BBBuildReport {

    private String reportID;
    private BBReportStatus reportStatus;
    private List<UUID> reportedPlayers;
    private UUID reportedBy;
    private File schematic;
    private Date reportDate;
    private ItemStack reportInventoryItem;

    public BBBuildReport(String reportID, List<UUID> reportedPlayers, UUID reportedBy, File schematic, Date reportDate, BBReportStatus status) {
        this.reportID = reportID;
        this.reportedPlayers = reportedPlayers;
        this.reportedBy = reportedBy;
        this.schematic = schematic;
        this.reportDate = reportDate;
        this.reportStatus = status;
        this.reportInventoryItem = ItemUtil.createReportItem(this);
    }


    public String getReportID() {
        return reportID;
    }

    public boolean saveReport() {
        switch(BuildBattle.getInstance().getSettings().getStatsType()) {
            case FLATFILE:
                return BuildBattle.getInstance().getReportManager().saveReportIntoConfig(this);
            case MYSQL:
                return BuildBattle.getInstance().getMySQLManager().saveReport(this);
        }
        return false;
    }

    public BBReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(BBReportStatus reportStatus) {
        this.reportStatus = reportStatus;
        reportInventoryItem = ItemUtil.createReportItem(this);
        switch(BuildBattle.getInstance().getSettings().getStatsType()) {
            case FLATFILE:
                BuildBattle.getInstance().getFileManager().getConfig("reports.yml").set(getReportID() + ".status", getReportStatus().name().toUpperCase()).save();
                break;
            case MYSQL:
                MySQL.update("UPDATE BuildBattlePro_ReportedBuilds SET Status=" + reportStatus.name().toUpperCase() + " WHERE ID=" + getReportID() + "");
                break;
        }
    }

    public List<UUID> getReportedPlayers() {
        return reportedPlayers;
    }

    public UUID getReportedBy() {
        return reportedBy;
    }

    public File getSchematicFile() {
        return schematic;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public String getReportedPlayersInCommaSeparatedString() {
        String s = "";
        for(UUID uuid : reportedPlayers) {
            s = s + (uuid.toString() + ",");
        }
        return s.substring(0, s.length()-1);
    }

    public List<String> getReportedPlayersStringList() {
        List<String> returnList = new ArrayList<>();
        reportedPlayers.forEach(p-> returnList.add(p.toString()));
        return returnList;
    }

    public ItemStack getReportInventoryItem() {
        return reportInventoryItem;
    }

    public boolean selectSchematic(Player p) {
        com.sk89q.worldedit.util.io.Closer closer = com.sk89q.worldedit.util.io.Closer.create();
        try {
            FileInputStream fis = closer.register(new FileInputStream(schematic));
            BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
            ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(bis);
            LocalSession session = BuildBattle.getInstance().getWorldEdit().getSession(p);
            WorldData worldData = BuildBattle.getInstance().getWorldEdit().wrapPlayer(p).getWorld().getWorldData();
            Clipboard clipboard = reader.read(BuildBattle.getInstance().getWorldEdit().wrapPlayer(p).getWorld().getWorldData());
            session.setClipboard(new ClipboardHolder(clipboard, worldData));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closer.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    public boolean delete() {
        if(schematic.exists()) schematic.delete();
        switch(BuildBattle.getInstance().getSettings().getStatsType()) {
            case FLATFILE:
                return BuildBattle.getInstance().getReportManager().deleteReportFromConfig(this);
            case MYSQL:
                return BuildBattle.getInstance().getMySQLManager().deleteReport(this);
        }
        return false;
    }
}
