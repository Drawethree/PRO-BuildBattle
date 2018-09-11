package me.drawe.buildbattle.objects.bbobjects;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum BBReportStatus {

    PENDING(Material.YELLOW_TERRACOTTA, ChatColor.YELLOW),
    SOLVED(Material.LIME_TERRACOTTA, ChatColor.GREEN);

    private ChatColor statusColor;
    private Material statusMaterial;
    BBReportStatus(Material i, ChatColor c) {
        this.statusMaterial = i;
        this.statusColor = c;
    }

    public ChatColor getStatusColor() {
        return statusColor;
    }

    public Material getStatusMaterial() {
        return statusMaterial;
    }
}
