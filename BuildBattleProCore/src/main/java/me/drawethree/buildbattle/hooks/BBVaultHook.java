package me.drawethree.buildbattle.hooks;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class BBVaultHook extends BBHook {

    @Getter
    private Economy econ;

    public BBVaultHook() {
        super("Vault");
    }

    @Override
    protected void runHookAction(BuildBattle plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        try {
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            this.econ = rsp.getProvider();
        } catch (Exception e) {
            plugin.warning("No economy implementation for Vault provided!");
        }
    }
}
