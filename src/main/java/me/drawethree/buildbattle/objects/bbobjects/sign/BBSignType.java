package me.drawethree.buildbattle.objects.bbobjects.sign;

import lombok.Getter;

public enum BBSignType {
    JOIN("join"),
    LEAVE("leave"),
    AUTO_JOIN("auto-join"),
    SPECTATE("spectate");

    @Getter
    private String configPath;

    BBSignType(String configPath) {
        this.configPath = configPath;
    }
}
