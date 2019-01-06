package me.drawe.buildbattle.objects.bbrewards;

import me.drawe.buildbattle.objects.bbobjects.BBTeam;

public interface BBReward {

    void giveReward(BBTeam team, int placement);
}
