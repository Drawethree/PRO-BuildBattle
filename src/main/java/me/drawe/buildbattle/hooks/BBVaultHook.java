package me.drawe.buildbattle.hooks;

import lombok.Getter;
import me.drawe.buildbattle.BuildBattle;
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
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        this.econ = rsp.getProvider();
    }
}
