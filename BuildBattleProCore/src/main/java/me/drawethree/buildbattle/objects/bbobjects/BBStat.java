package me.drawethree.buildbattle.objects.bbobjects;

import me.drawethree.buildbattle.leaderboards.LeaderboardType;

public enum BBStat {

    WINS("wins","Wins"),
    PLAYED("played", "Played"),
    MOST_POINTS("most_points","MostPoints"),
    PARTICLES_PLACED("blocks_placed", "ParticlesPlaced"),
    BLOCKS_PLACED("particles_placed", "BlocksPlaced"),
    SUPER_VOTES("super_votes","SuperVotes");

    private String configKey;
    private String sqlKey;

    BBStat(String configKey, String sqlKey) {
        this.configKey = configKey;
        this.sqlKey = sqlKey;
    }

    public static BBStat getStat(String params) {
        for(BBStat stat : BBStat.values()) {
            if(stat.configKey.equals(params)) {
                return stat;
            }
        }
        return null;
    }

    public static BBStat map(LeaderboardType leaderboardType) {
        for (BBStat stat : values()) {
            if (stat.name().equalsIgnoreCase(leaderboardType.name())) {
                return stat;
            }
        }
        return null;
    }

    public String getSQLKey() {
        return sqlKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}
