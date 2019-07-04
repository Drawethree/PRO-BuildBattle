package me.drawe.buildbattle.hooks;

import me.drawe.buildbattle.managers.LeaderboardManager;

public class BBHookHolographicDisplays extends BBHook {

    public BBHookHolographicDisplays() {
        super("HolographicDisplays");
    }

    @Override
    protected void runHookAction() {
        LeaderboardManager.getInstance().loadAllLeaderboards();
    }
}
