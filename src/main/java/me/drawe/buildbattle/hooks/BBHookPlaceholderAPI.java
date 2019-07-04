package me.drawe.buildbattle.hooks;

import me.drawe.buildbattle.hooks.papi.BuildBattleProPlaceholders;

public class BBHookPlaceholderAPI extends BBHook {

    public BBHookPlaceholderAPI() {
        super("PlaceholderAPI");
    }

    @Override
    protected void runHookAction() {
        new BuildBattleProPlaceholders().register();
    }
}
