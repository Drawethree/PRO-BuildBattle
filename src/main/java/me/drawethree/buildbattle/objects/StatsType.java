package me.drawethree.buildbattle.objects;

public enum StatsType {

    FLATFILE("Stats will be saved and loaded from stats.yml"),
    MYSQL("Stats will be saved and loaded from MySQL database");

    private String info;

    StatsType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
