package me.drawe.buildbattle.objects.bbobjects.rewards;

import lombok.Getter;
import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;

import java.util.HashMap;


public abstract class BBReward<T> {

    @Getter
    private BuildBattle parent;
    @Getter
    protected String rewardType;
    @Getter
    private boolean enabled;

    protected Class<T> typeOfReward;
    protected HashMap<Integer, T> rewardsForPlacements;


    public BBReward(BuildBattle parent, String rewardType, Class<T> typeOfReward) {
        this.typeOfReward = typeOfReward;
        this.parent = parent;
        this.rewardType = rewardType;
        this.rewardsForPlacements = new HashMap<>();
        this.enabled = this.parent.getFileManager().getConfig("config.yml").get().getBoolean("rewards." + rewardType + ".enabled");
        this.loadFromConfig();
    }

    public void addReward(int placement, T reward) {
        this.parent.debug("Adding " + this.rewardType + " reward for placement " + placement + ": " + reward);
        this.rewardsForPlacements.put(placement, reward);
    }

    private void loadFromConfig() {
        this.parent.debug("Loading " + this.rewardType + " rewards from config.yml");

        if (!this.enabled) {
            return;
        }

        int place;
        for (String key : this.parent.getFileManager().getConfig("config.yml").get().getConfigurationSection("rewards." + rewardType + ".placement").getKeys(false)) {

            try {
                place = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                this.parent.warning("Placement '" + key + "' is not a valid placement for " + this.rewardType + " reward! It should be a number.");
                continue;
            }

            this.addReward(place, (T) this.parent.getFileManager().getConfig("config.yml").get().get("rewards." + rewardType + ".placement." + place, typeOfReward));
        }
    }

    public void giveReward(BBTeam team, int placement) {
        if(!enabled || team == null || !rewardsForPlacements.containsKey(placement)) {
            return;
        }
    }
}
