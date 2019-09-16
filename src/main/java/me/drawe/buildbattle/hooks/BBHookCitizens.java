package me.drawe.buildbattle.hooks;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.listeners.NPCListener;
import org.bukkit.Bukkit;

public class BBHookCitizens extends BBHook {

    public BBHookCitizens() {
        super("Citizens");
    }

    @Override
    protected void runHookAction(BuildBattle plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(plugin), plugin);
    }
}
