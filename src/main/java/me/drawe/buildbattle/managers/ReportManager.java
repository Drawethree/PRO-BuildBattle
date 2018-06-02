package me.drawe.buildbattle.managers;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.GuiItem;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.BBBuildReport;
import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import me.drawe.buildbattle.objects.bbobjects.BBReportStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class ReportManager {
    private static ReportManager ourInstance = new ReportManager();

    public static ReportManager getInstance() {
        return ourInstance;
    }

    private ReportManager() {
    }

    public static List<BBBuildReport> buildReports;
    public static final DateFormat reportDateformat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    public static final String reportsDirectoryName = "reports_schematics";
    private static int maxReportsPerInv = 45;
    public static String reportsInventoryTitle = "Build Reports Page: ";

    public static List<BBBuildReport> getBuildReports() {
        return buildReports;
    }

    public void loadAllReports() {
        switch (GameManager.getStatsType()) {
            case MYSQL:
                MySQLManager.getInstance().loadAllReports();
                break;
            case FLATFILE:
                loadAllReportsFromConfig();
                break;
        }

    }

    public void loadAllReportsFromConfig() {
        buildReports = new ArrayList<>();
        for (String s : BuildBattle.getFileManager().getConfig("reports.yml").get().getKeys(false)) {
            String reportId = s;
            List<UUID> reportedPlayers = new ArrayList<>();
            BuildBattle.getFileManager().getConfig("reports.yml").get().getStringList(s + ".reported_players").forEach(uuid -> reportedPlayers.add(UUID.fromString(uuid)));
            UUID reportedBy = UUID.fromString(BuildBattle.getFileManager().getConfig("reports.yml").get().getString(s + ".reported_by"));
            Date date = Date.from(Instant.parse(BuildBattle.getFileManager().getConfig("reports.yml").get().getString(s + ".date")));
            File schematic = new File(new File(BuildBattle.getInstance().getDataFolder(), reportsDirectoryName), BuildBattle.getFileManager().getConfig("reports.yml").get().getString(s + ".schematic_name"));
            BBReportStatus status = BBReportStatus.valueOf(BuildBattle.getFileManager().getConfig("reports.yml").get().getString(s + ".status").toUpperCase());
            BBBuildReport report = new BBBuildReport(reportId, reportedPlayers, reportedBy, schematic, date, status);
            ReportManager.getBuildReports().add(report);
        }
    }

    public BBBuildReport getReport(ItemStack item) {
        for(BBBuildReport report : buildReports) {
            if(report.getReportInventoryItem().equals(item)) {
                return report;
            }
        }
        return null;
    }


    public boolean attemptReport(BBPlot reportedPlot, Player whoReported) {
        if(reportedPlot.getTeam().getPlayers().contains(whoReported)) {
            whoReported.sendMessage(Message.CANNOT_REPORT_YOURSELF.getChatMessage());
            return false;
        } else if(reportedPlot.getReportedBy() != null) {
            whoReported.sendMessage(Message.ALREADY_REPOTED.getChatMessage());
            return false;
        } else {
            return createReport(reportedPlot,whoReported);
        }
    }

    public boolean createReport(BBPlot reportedPlot, Player whoReported) {
        String reportID = generateReportID();
        List<UUID> reportedPlayers = new ArrayList<>();
        reportedPlot.getTeam().getPlayers().forEach(p-> reportedPlayers.add(p.getUniqueId()));
        UUID reportedBy = whoReported.getUniqueId();
        BBReportStatus status = BBReportStatus.PENDING;
        File schem = createSchematic(whoReported,reportID,reportedPlot);
        BBBuildReport report = new BBBuildReport(reportID,reportedPlayers,reportedBy,schem,new Date(),status);
        if (report.saveReport()) {
            whoReported.sendMessage(Message.REPORT_SUCCESS.getChatMessage());
            reportedPlot.setReportedBy(whoReported.getUniqueId());
            buildReports.add(report);
            return true;
        } else {
            whoReported.sendMessage(Message.REPORT_FAILED.getChatMessage());
        }
        return false;
    }

    public static File createSchematic(Player player, String reportID, BBPlot plot) {
        try {
            File dir = new File(BuildBattle.getInstance().getDataFolder(), reportsDirectoryName);
            File schematic = new File(dir, reportID + ".schematic");
            if (!dir.exists())
                dir.mkdirs();

            WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            WorldEdit we = wep.getWorldEdit();

            LocalPlayer localPlayer = wep.wrapPlayer(player);
            LocalSession localSession = we.getSession(localPlayer);
            EditSession editSession = localSession.createEditSession(localPlayer);

            Vector min = new Vector(plot.getMinPoint().getBlockX(), plot.getMinPoint().getBlockY(), plot.getMinPoint().getBlockZ());
            Vector max = new Vector(plot.getMaxPoint().getBlockX(), plot.getMaxPoint().getBlockY(), plot.getMaxPoint().getBlockZ());

            editSession.enableQueue();
            CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
            clipboard.copy(editSession);
            SchematicFormat.MCEDIT.save(clipboard, schematic);
            editSession.flushQueue();
            return schematic;
        } catch (IOException | DataException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean existsReport(int id) {
        for(BBBuildReport report : buildReports) {
            if(Integer.parseInt(report.getReportID().replaceAll("report", "")) == id) {
                return true;
            }
        }
        return false;
    }

    public String generateReportID() {
        int id = 1;
        while(existsReport(id)) {
            id += 1;
        }
        return "report" + id;
    }

    public boolean saveReportIntoConfig(BBBuildReport report) {
        try {
            BuildBattle.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".reported_players", report.getReportedPlayersStringList());
            BuildBattle.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".reported_by", report.getReportedBy().toString());
            BuildBattle.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".date", reportDateformat.format(report.getReportDate()));
            BuildBattle.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".schematic_name", report.getSchematicFile().getName());
            BuildBattle.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".status", report.getReportStatus().name().toUpperCase());
            BuildBattle.getFileManager().getConfig("reports.yml").save();
            return true;
        } catch (Exception e) {
            BuildBattle.severe("An exception occurred while saving report " + report.getReportID() + " into reports.yml !");
            e.printStackTrace();
        }
        return false;
    }


    public int getNextPage(Inventory inv) {
        try {
            return Integer.parseInt(inv.getTitle().replaceAll(reportsInventoryTitle, "")) + 1;
        } catch (Exception e) {
            return 0;
        }
    }
    public int getCurrentPage(Inventory inv) {
        try {
            return Integer.parseInt(inv.getTitle().replaceAll(reportsInventoryTitle, ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getPrevPage(Inventory inv) {
        try {
            return Integer.parseInt(inv.getTitle().replaceAll(reportsInventoryTitle, "")) - 1;
        } catch (Exception e) {
            return 0;
        }
    }
    public void openReports(Player p, int page) {
        if(page > 0) {
            if(getBuildReports().size() >= (page*maxReportsPerInv)-45) {
                Inventory inv = Bukkit.createInventory(null, 54, reportsInventoryTitle + page);
                for (int i = page * maxReportsPerInv - 45; i < page * maxReportsPerInv; i++) {
                    try {
                        inv.addItem(getBuildReports().get(i).getReportInventoryItem());
                    } catch (Exception e) {
                        break;
                    }
                }
                inv.setItem(45, GuiItem.FILL_ITEM.getItemStack());
                inv.setItem(46, GuiItem.FILL_ITEM.getItemStack());
                inv.setItem(47, GuiItem.FILL_ITEM.getItemStack());
                inv.setItem(48, GuiItem.PREV_PAGE.getItemStack());
                inv.setItem(49, GuiItem.FILL_ITEM.getItemStack());
                inv.setItem(50, GuiItem.NEXT_PAGE.getItemStack());
                inv.setItem(51, GuiItem.FILL_ITEM.getItemStack());
                inv.setItem(52, GuiItem.FILL_ITEM.getItemStack());
                inv.setItem(53, GuiItem.CLOSE_GUI.getItemStack());
                p.openInventory(inv);
            }
        }
    }

    public boolean deleteReportFromConfig(BBBuildReport report) {
        try {
            BuildBattle.getFileManager().getConfig("reports.yml").set(report.getReportID(), null).save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReport(BBBuildReport clickedReport) {
        if(clickedReport.delete()) {
            buildReports.remove(clickedReport);
            clickedReport = null;
            return true;
        }
        return false;
    }
}
