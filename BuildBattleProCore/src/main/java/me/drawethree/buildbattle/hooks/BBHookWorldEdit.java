package me.drawethree.buildbattle.hooks;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.managers.ReportManager;
import org.bukkit.Bukkit;

public class BBHookWorldEdit extends BBHook {

    @Getter
    private ReportManager reportManager;

    public BBHookWorldEdit() {
        super("WorldEdit");
    }

    @Override
    protected void runHookAction(BuildBattle buildBattle) {
        this.reportManager = new ReportManager(buildBattle);
        this.reportManager.loadAllReports();
    }

    @Override
    public void hook(BuildBattle buildBattle) {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            String version = Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion();
            String fullName = Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getFullName();

            boolean compatible = false;
            String minVersion = "7";

            if (fullName.contains("FastAsyncWorldEdit") && (version.startsWith("1.15") || version.startsWith("1.16"))) {
                compatible = true;
                minVersion = "1.15";
            } else if (version.startsWith("7"))
                compatible = true;

            if (compatible) {
                BuildBattle.getInstance().info("§aSuccessfully hooked into §e" + this.pluginName + " §aversion §e" + version);
                this.enabled = true;
                this.runHookAction(buildBattle);
            } else {
                BuildBattle.getInstance().warning("§cThis version of " + fullName + " is not supported!(§e" + version + "§c). Please use version §e" + minVersion + " §cand above.");
            }
        }
    }
}
