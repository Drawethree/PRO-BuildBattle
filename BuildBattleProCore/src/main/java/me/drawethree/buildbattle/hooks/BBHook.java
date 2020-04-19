package me.drawethree.buildbattle.hooks;

import me.drawethree.buildbattle.BuildBattle;
import org.bukkit.Bukkit;

import java.util.HashMap;

public abstract class BBHook {

    private static HashMap<String, BBHook> hooks = new HashMap<>();

    static {
        hooks.put("Vault", new BBVaultHook());
        hooks.put("Citizens", new BBHookCitizens());
        hooks.put("HolographicDisplays", new BBHookHolographicDisplays());
        hooks.put("LeaderHeads", new BBHookLeaderHeads());
        hooks.put("MVdWPlaceholderAPI", new BBHookMVDWPAPI());
        hooks.put("WorldEdit", new BBHookWorldEdit());
        hooks.put("PlaceholderAPI", new BBHookPlaceholderAPI());
    }

    public static void attemptHooks(BuildBattle buildBattle) {
        for(BBHook hook : hooks.values()) {
            hook.hook(buildBattle);
        }
    }

    public static boolean getHook(String pluginName) {
        if(hooks.get(pluginName) == null) {
            return false;
        }

        return hooks.get(pluginName).isEnabled();
    }

    public static BBHook getHookInstance(String pluginName) {
        return hooks.get(pluginName);
    }

    protected String pluginName;
    protected boolean enabled;

    public BBHook(String pluginName) {
        this.pluginName = pluginName;
    }

    public void hook(BuildBattle buildBattle) {
        if(Bukkit.getPluginManager().isPluginEnabled(this.pluginName)) {
            String version = Bukkit.getPluginManager().getPlugin(this.pluginName).getDescription().getVersion();
            BuildBattle.getInstance().info("§aSuccessfully hooked into §e" + this.pluginName + " §aversion §e" + version);
            this.enabled = true;
            this.runHookAction(buildBattle);
        }
    }

    protected abstract void runHookAction(BuildBattle buildBattle);

    public boolean isEnabled() {
        return enabled;
    }
}
