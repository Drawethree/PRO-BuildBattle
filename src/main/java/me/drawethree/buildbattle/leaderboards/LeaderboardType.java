package me.drawethree.buildbattle.leaderboards;

import lombok.Getter;
import me.drawethree.buildbattle.objects.Message;

@Getter
public enum LeaderboardType {

    WINS("%wins%", Message.LEADERBOARDS_WINS_TITLE.getMessage(), Message.LEADERBOARDS_WINS_FORMAT.getMessage()),
    PLAYED("%played%", Message.LEADERBOARDS_PLAYED_TITLE.getMessage(), Message.LEADERBOARDS_PLAYED_FORMAT.getMessage()),
    BLOCKS_PLACED("%blocks_placed%", Message.LEADERBOARDS_BLOCKS_PLACED_TITLE.getMessage(), Message.LEADERBOARDS_BLOCKS_PLACED_FORMAT.getMessage()),
    PARTICLES_PLACED("%particles_placed%", Message.LEADERBOARDS_PARTICLES_PLACED_TITLE.getMessage(), Message.LEADERBOARDS_PARTICLES_PLACED_FORMAT.getMessage());

    String placeholder;
    String title;
    String lineFormat;

    LeaderboardType(String s, String title, String lineFormat) {
        this.placeholder = s;
        this.title = title;
        this.lineFormat = lineFormat;
    }

}
