package me.drawethree.buildbattle.managers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.Getter;
import me.drawethree.api.events.misc.BBReportEvent;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.GUIItem;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.StatsType;
import me.drawethree.buildbattle.objects.bbobjects.BBBuildReport;
import me.drawethree.buildbattle.objects.bbobjects.BBReportStatus;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ReportManager {

    public static final DateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    public static final String REPORTS_DIRECTORY_NAME = "reports_schematics";
    private static int maxReportsPerInv = 45;

    private BuildBattle plugin;
    private HashMap<String, BBBuildReport> buildReports;

    public ReportManager(BuildBattle plugin) {
        this.plugin = plugin;
    }

    public void loadAllReports() {
        this.buildReports = new HashMap<>();
        if (this.plugin.getSettings().getStatsType() == StatsType.FLATFILE) {
            this.loadAllReportsFromConfig();
        } else {
            this.plugin.getMySQLManager().loadAllReports(this);
        }
    }

    private void loadAllReportsFromConfig() {
        for (String s : this.plugin.getFileManager().getConfig("reports.yml").get().getKeys(false)) {
            final List<UUID> reportedPlayers = new ArrayList<>();
            this.plugin.getFileManager().getConfig("reports.yml").get().getStringList(s + ".reported_players").forEach(uuid -> reportedPlayers.add(UUID.fromString(uuid)));

            final UUID reportedBy = UUID.fromString(this.plugin.getFileManager().getConfig("reports.yml").get().getString(s + ".reported_by"));

            Date date = null;
            try {
                date = REPORT_DATE_FORMAT.parse(this.plugin.getFileManager().getConfig("reports.yml").get().getString(s + ".date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final File schematic = new File(new File(this.plugin.getDataFolder(), REPORTS_DIRECTORY_NAME), this.plugin.getFileManager().getConfig("reports.yml").get().getString(s + ".schematic_name"));
            final BBReportStatus status = BBReportStatus.valueOf(this.plugin.getFileManager().getConfig("reports.yml").get().getString(s + ".status").toUpperCase());
            final BBBuildReport report = new BBBuildReport(s, reportedPlayers, reportedBy, schematic, date, status);
            this.addReport(report);
        }
    }

    public BBBuildReport getReport(ItemStack item) {
        for (BBBuildReport report : buildReports.values()) {
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
        if (report.saveReport(this)) {
            whoReported.sendMessage(Message.REPORT_SUCCESS.getChatMessage());
            reportedPlot.setReportedBy(whoReported.getUniqueId());
            buildReports.put(report.getReportID(), report);
            Bukkit.getPluginManager().callEvent(new BBReportEvent(whoReported, reportedPlot.getTeam().getPlayers(), reportedPlot, reportID));
            return true;
        } else {
            whoReported.sendMessage(Message.REPORT_FAILED.getChatMessage());
        }
        return false;
    }

    private File createSchematic(Player player, String reportID, BBPlot plot) {
        try {
            File dir = new File(this.plugin.getDataFolder(), REPORTS_DIRECTORY_NAME);
            File schematicFile = new File(dir, reportID + ".schematic");
            if (!dir.exists())
                dir.mkdirs();

            /*LocalPlayer localPlayer = wep.wrapPlayer(player);
            LocalSession localSession = we.getSession(localPlayer);
            EditSession editSession = localSession.createEditSession(localPlayer);*/

            /*Vector min = new Vector(plot.getMinPoint().getBlockX(), plot.getMinPoint().getBlockY(), plot.getMinPoint().getBlockZ());
            Vector max = new Vector(plot.getMaxPoint().getBlockX(), plot.getMaxPoint().getBlockY(), plot.getMaxPoint().getBlockZ());

            editSession.enableQueue();
            CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
            clipboard.copy(editSession);
            SchematicFormat.MCEDIT.save(clipboard, schematic);
            editSession.flushQueue();*/

            BlockVector3 min = BlockVector3.at(plot.getMinPoint().getBlockX(), plot.getMinPoint().getBlockY(), plot.getMinPoint().getBlockZ());
            BlockVector3 max = BlockVector3.at(plot.getMaxPoint().getBlockX(), plot.getMaxPoint().getBlockY(), plot.getMaxPoint().getBlockZ());

            CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(player.getWorld()), min, max);
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(player.getWorld()), -1)) {
                ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                        editSession, region, clipboard, region.getMinimumPoint()
                );

                forwardExtentCopy.setCopyingBiomes(true);
                forwardExtentCopy.setCopyingEntities(true);

                Operations.complete(forwardExtentCopy);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }

            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematicFile))) {
                writer.write(clipboard);
            }
            return schematicFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsReport(int id) {
        return this.buildReports.containsKey("report" + id);
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
            this.plugin.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".reported_players", report.getReportedPlayersStringList());
            this.plugin.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".reported_by", report.getReportedBy().toString());
            this.plugin.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".date", REPORT_DATE_FORMAT.format(report.getReportDate()));
            this.plugin.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".schematic_name", report.getSchematicFile().getName());
            this.plugin.getFileManager().getConfig("reports.yml").set(report.getReportID() + ".status", report.getReportStatus().name().toUpperCase());
            this.plugin.getFileManager().getConfig("reports.yml").save();
            return true;
        } catch (Exception e) {
            this.plugin.severe("An exception occurred while saving report " + report.getReportID() + " into reports.yml !");
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveReportIntoDB(BBBuildReport report) {
        this.plugin.getMySQLManager().saveReport(report);
        return true;
    }


    public int getNextPage(InventoryView inv) {
        try {
            return Integer.parseInt(inv.getTitle().replace(this.plugin.getOptionsManager().getReportsInventoryTitle(), "")) + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCurrentPage(InventoryView inv) {
        try {
            return Integer.parseInt(inv.getTitle().replace(this.plugin.getOptionsManager().getReportsInventoryTitle(), ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getPrevPage(InventoryView inv) {
        try {
            return Integer.parseInt(inv.getTitle().replace(this.plugin.getOptionsManager().getReportsInventoryTitle(), "")) - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public void openReports(Player p, int page) {
        if (page > 0) {
            if (buildReports.values().size() >= (page * maxReportsPerInv) - 45) {
                List<ItemStack> reportItems = buildReports.values().stream().map(BBBuildReport::getReportInventoryItem).collect(Collectors.toList());
                Inventory inv = Bukkit.createInventory(null, 54, this.plugin.getOptionsManager().getReportsInventoryTitle() + page);
                for (int i = page * maxReportsPerInv - 45; i < page * maxReportsPerInv; i++) {
                    try {
                        inv.addItem(reportItems.get(i));
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
            this.plugin.getFileManager().getConfig("reports.yml").set(report.getReportID(), null).save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReport(BBBuildReport clickedReport) {
        if (clickedReport.delete(this)) {
            buildReports.remove(clickedReport.getReportID());
            return true;
        }
        return false;
    }

    public boolean deleteReportFromDatabase(BBBuildReport bbBuildReport) {
        this.plugin.getMySQLManager().deleteReport(bbBuildReport);
        return true;
    }

    public void addReport(BBBuildReport report) {
        this.buildReports.put(report.getReportID(), report);
    }
}
