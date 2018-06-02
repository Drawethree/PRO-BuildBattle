package me.drawe.buildbattle.objects.bbobjects;

import org.bukkit.ChatColor;

public enum BBReportStatus {

    PENDING((byte)4, ChatColor.YELLOW),
    SOLVED((byte)5, ChatColor.GREEN);

    private byte data;
    private ChatColor statusColor;
    BBReportStatus(byte i, ChatColor c) {
        this.data = i;
        this.statusColor = c;
    }

    public byte getData() {
        return data;
    }

    public ChatColor getStatusColor() {
        return statusColor;
    }
}
