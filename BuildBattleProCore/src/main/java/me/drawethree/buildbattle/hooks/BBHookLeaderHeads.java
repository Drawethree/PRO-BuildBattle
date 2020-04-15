package me.drawethree.buildbattle.hooks;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.hooks.leaderheads.*;

public class BBHookLeaderHeads extends BBHook {

    public BBHookLeaderHeads() {
        super("LeaderHeads");
    }

    @Override
    protected void runHookAction(BuildBattle plugin) {
        new BuildBattleWins();
        new BuildBattlePlayed();
        new BuildBattleBlocksPlaced();
        new BuildBattleMostPoints();
        new BuildBattleSuperVotes();
        new BuildBattleParticlesPlaced();
    }
}
