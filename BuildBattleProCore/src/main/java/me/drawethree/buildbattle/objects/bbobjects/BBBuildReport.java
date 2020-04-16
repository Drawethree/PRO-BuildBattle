package me.drawethree.buildbattle.objects.bbobjects;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.managers.ReportManager;
import me.drawethree.buildbattle.utils.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public boolean saveReport(ReportManager manager) {
        switch (BuildBattle.getInstance().getSettings().getStatsType()) {
            case FLATFILE:
                return manager.saveReportIntoConfig(this);
            case MYSQL:
                return manager.saveReportIntoDB(this);
        }
        return false;
    }

    public BBReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(BBReportStatus reportStatus) {
        this.reportStatus = reportStatus;
        reportInventoryItem = ItemUtil.createReportItem(this);
        switch (BuildBattle.getInstance().getSettings().getStatsType()) {
            case FLATFILE:
                BuildBattle.getInstance().getFileManager().getConfig("reports.yml").set(getReportID() + ".status", getReportStatus().name().toUpperCase()).save();
                break;
            case MYSQL:
                BuildBattle.getInstance().getMySQLDatabase().execute("UPDATE BuildBattlePro_ReportedBuilds SET Status=? WHERE ID=?", reportStatus.name().toUpperCase(), getReportIDRaw());
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
        for (UUID uuid : reportedPlayers) {
            s = s + (uuid.toString() + ",");
        }
        return s.substring(0, s.length() - 1);
    }

    public List<String> getReportedPlayersStringList() {
        List<String> returnList = new ArrayList<>();
        reportedPlayers.forEach(p -> returnList.add(p.toString()));
        return returnList;
    }

    public ItemStack getReportInventoryItem() {
        return reportInventoryItem;
    }

    public void pasteSchematic(Player p) {

        Clipboard clipboard;

        ClipboardFormat format = ClipboardFormats.findByFile(this.getSchematicFile());
        try (ClipboardReader reader = format.getReader(new FileInputStream(this.getSchematicFile()))) {
            clipboard = reader.read();
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(p.getWorld()), -1, BukkitAdapter.adapt(p))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()))
                        .copyBiomes(true)
                        .copyEntities(true)
                        .build();
                Operations.complete(operation);
                p.sendMessage(BuildBattle.getInstance().getSettings().getPrefix() + "§aSchematic for Report §e" + this.reportID + " §aparsed!");
            } catch (WorldEditException e) {
                p.sendMessage(BuildBattle.getInstance().getSettings().getPrefix() + "§cThere are some problem with parsing schematic for report §e" + this.reportID + " !");
                e.printStackTrace();
            }
        } catch (IOException e) {
            p.sendMessage(BuildBattle.getInstance().getSettings().getPrefix() + "§cThere are some problem with loading schematic file for report §e" + this.reportID + " !");
            e.printStackTrace();
        }

        /*com.sk89q.worldedit.util.io.Closer closer = com.sk89q.worldedit.util.io.Closer.create();
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
        }*/
    }

    public boolean delete(ReportManager manager) {
        if (schematic.exists())
            schematic.delete();
        switch (BuildBattle.getInstance().getSettings().getStatsType()) {
            case FLATFILE:
                return manager.deleteReportFromConfig(this);
            case MYSQL:
                return manager.deleteReportFromDatabase(this);
        }
        return false;
    }

    public int getReportIDRaw() {
        return Integer.parseInt(this.reportID.replace("report", ""));
    }
}
