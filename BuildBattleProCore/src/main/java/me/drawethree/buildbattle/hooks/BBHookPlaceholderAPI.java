package me.drawethree.buildbattle.hooks;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.hooks.papi.BuildBattleProExpansion;

public class BBHookPlaceholderAPI extends BBHook {

    public BBHookPlaceholderAPI() {
        super("PlaceholderAPI");
    }

    @Override
    protected void runHookAction(BuildBattle plugin) {
        new BuildBattleProExpansion(plugin).register();
    }
}
