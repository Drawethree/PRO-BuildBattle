package me.drawe.buildbattle.hooks;

import me.drawe.buildbattle.BuildBattle;

public class BBHookHolographicDisplays extends BBHook {

    public BBHookHolographicDisplays() {
        super("HolographicDisplays");
    }

    @Override
    protected void runHookAction() {
        BuildBattle.getInstance().getLeaderboardManager().loadAllLeaderboards();
    }
}
