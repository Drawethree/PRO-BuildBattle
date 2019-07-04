package me.drawe.buildbattle.hooks;

import me.drawe.buildbattle.hooks.leaderheads.*;

public class BBHookLeaderHeads extends BBHook {

    public BBHookLeaderHeads() {
        super("LeaderHeads");
    }

    @Override
    protected void runHookAction() {
        new BuildBattleWins();
        new BuildBattlePlayed();
        new BuildBattleBlocksPlaced();
        new BuildBattleMostPoints();
        new BuildBattleSuperVotes();
        new BuildBattleParticlesPlaced();
    }
}
