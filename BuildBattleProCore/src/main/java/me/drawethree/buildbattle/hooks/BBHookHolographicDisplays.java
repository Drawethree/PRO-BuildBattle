package me.drawethree.buildbattle.hooks;

import me.drawethree.buildbattle.BuildBattle;

public class BBHookHolographicDisplays extends BBHook {

    public BBHookHolographicDisplays() {
        super("HolographicDisplays");
    }

    @Override
    protected void runHookAction(BuildBattle plugin) {
        plugin.getLeaderboardManager().loadAllLeaderboards();
    }
}
