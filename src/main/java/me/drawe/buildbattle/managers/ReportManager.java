package me.drawe.buildbattle.managers;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.events.misc.BBReportEvent;
import me.drawe.buildbattle.objects.GUIItem;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.StatsType;
import me.drawe.buildbattle.objects.bbobjects.BBBuildReport;
import me.drawe.buildbattle.objects.bbobjects.BBReportStatus;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReportManager {
    private static ReportManager ourInstance = new ReportManager();
    public static List<BBBuildReport> buildReports;
    public static final DateFormat reportDateformat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    public static final String reportsDirectoryName = "reports_schematics";
    private static int maxReportsPerInv = 45;

    private ReportManager() {

    }

    public static ReportManager getInstance() {
        return ourInstance;
    }

    public static List<BBBuildReport> getBuildReports() {
        return buildReports;
    }

    public void loadAllReports() {
        if (BBSettings.getStatsType() == StatsType.FLATFILE) {
            loadAllReportsFromConfig();
        }
    }

    private void loadAllReportsFromConfig() {
        buildReports = new ArrayList<>();
        for (String s : BuildBattle.getFileManager().getConfig("reports.yml").get().getKeys(false)) {
            final List<UUID> reportedPlayers = new ArrayList<>();
            BuildBattle.getFileManager().getConfig("reports.yml").get().getStringList(s + ".reported_players").forEach(uuid -> reportedPlayers.add(UUID.fromString(uuid)));

            final UUID reportedBy = UUID.fromString(BuildBattle.getFileManager().getConfig("reports.yml").get().getString(s + ".reported_by"));

            Date date = null;
            try {
                date = reportDateformat.parse(BuildBattle.getFileManager().getConfig("reports.yml").get().getString(s + ".date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final File schematic = new File(new File(BuildBattle.getInstance().getDataFolder(), reportsDirectoryName), BuildBattle.getFileManager().getConfig("reports.yml").get().getString(s + ".schematic_name"));
            final BBReportStatus status = BBReportStatus.valueOf(BuildBattle.getFileManager().getConfig("reports.yml").get().getString(s + ".status").toUpperCase());
            final BBBuildReport report = new BBBuildReport(s, reportedPlayers, reportedBy, schematic, date, status);
            buildReports.add(report);
        }
    }

    public BBBuildReport getReport(ItemStack item) {
        for (BBBuildReport report : buildReports) {
            if (report.getReportInventoryItem().equals(item)) {
                return report;
            }
        }
        return null;
    }


    public boolean attemptReport(BBPlot reportedPlot, Player whoReported) {
        if (reportedPlot.getTeam().getPlayers().contains(whoReported)) {
            whoReported.sendMessage(Message.CANNOT_REPORT_YOURSELF.getChatMessage());
            return false;
        } else if (reportedPlot.getReportedBy() != null) {
            whoReported.sendMessage(Message.ALREADY_REPOTED.getChatMessage());
            return false;
        } else {
            return createReport(reportedPlot, whoReported);
        }
    }

    private boolean createReport(BBPlot reportedPlot, Player whoReported) {
        String reportID = generateReportID();
        List<UUID> reportedPlayers = new ArrayList<>();
        reportedPlot.getTeam().getPlayers().forEach(p -> reportedPlayers.add(p.getUniqueId()));
        UUID reportedBy = whoReported.getUniqueId();
        BBReportStatus status = BBReportStatus.PENDING;
        File schem = createSchematic(whoReported, reportID, reportedPlot);
        BBBuildReport report = new BBBuildReport(reportID, reportedPlayers, reportedBy, schem, new Date(), status);
        if (report.saveReport()) {
            whoReported.sendMessage(Message.REPORT_SUCCESS.getChatMessage());
            reportedPlot.setReportedBy(whoReported.getUniqueId());
            buildReports.add(report);
            Bukkit.getPluginManager().callEvent(new BBReportEvent(whoReported, reportedPlot.getTeam().getPlayers(), reportedPlot, reportID));
            return true;
        } else {
            whoReported.sendMessage(Message.REPORT_FAILED.getChatMessage());
        }
        return false;
    }

    private File createSchematic(Player player, String reportID, BBPlot plot) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsReport(int id) {
        for (BBBuildReport report : buildReports) {
            if (Integer.parseInt(report.getReportID().replaceAll("report", "")) == id) {
                return true;
            }
        }
        return false;
    }

    public String generateReportID() {
        int id = 1;
        while (existsReport(id)) {
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
            return Integer.parseInt(inv.getTitle().replaceAll(OptionsManager.getReportsInventoryTitle(), "")) + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCurrentPage(Inventory inv) {
        try {
            return Integer.parseInt(inv.getTitle().replaceAll(OptionsManager.getReportsInventoryTitle(), ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getPrevPage(Inventory inv) {
        try {
            return Integer.parseInt(inv.getTitle().replaceAll(OptionsManager.getReportsInventoryTitle(), "")) - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public void openReports(Player p, int page) {
        if (page > 0) {
            if (getBuildReports().size() >= (page * maxReportsPerInv) - 45) {
                Inventory inv = Bukkit.createInventory(null, 54, OptionsManager.getReportsInventoryTitle() + page);
                for (int i = page * maxReportsPerInv - 45; i < page * maxReportsPerInv; i++) {
                    try {
                        inv.addItem(getBuildReports().get(i).getReportInventoryItem());
                    } catch (Exception e) {
                        break;
                    }
                }
                inv.setItem(45, GUIItem.FILL_ITEM.getItemStack());
                inv.setItem(46, GUIItem.FILL_ITEM.getItemStack());
                inv.setItem(47, GUIItem.FILL_ITEM.getItemStack());
                inv.setItem(48, GUIItem.PREV_PAGE.getItemStack());
                inv.setItem(49, GUIItem.FILL_ITEM.getItemStack());
                inv.setItem(50, GUIItem.NEXT_PAGE.getItemStack());
                inv.setItem(51, GUIItem.FILL_ITEM.getItemStack());
                inv.setItem(52, GUIItem.FILL_ITEM.getItemStack());
                inv.setItem(53, GUIItem.CLOSE_GUI.getItemStack());
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
        if (clickedReport.delete()) {
            buildReports.remove(clickedReport);
            return true;
        }
        return false;
    }
}
