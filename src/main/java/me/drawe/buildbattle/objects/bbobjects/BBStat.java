package me.drawe.buildbattle.objects.bbobjects;

public enum BBStat {

    WINS("wins","Wins"),
    PLAYED("played", "Played"),
    MOST_POINTS("most_points","MostPoints"),
    PARTICLES_PLACED("blocks_placed", "BlocksPlaced"),
    BLOCKS_PLACED("particles_placed", "ParticlesPlaced"),
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

    public String getSQLKey() {
        return sqlKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}
