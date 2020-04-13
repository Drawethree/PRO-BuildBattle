package me.drawethree.buildbattle.hooks;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.listeners.NPCListener;
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
