package me.drawe.buildbattle.objects.bbobjects;

import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.ChatColor;

public enum BBReportStatus {

    PENDING(CompMaterial.YELLOW_TERRACOTTA, ChatColor.YELLOW),
    SOLVED(CompMaterial.LIME_TERRACOTTA, ChatColor.GREEN);

    private ChatColor statusColor;
    private CompMaterial statusMaterial;

    BBReportStatus(CompMaterial i, ChatColor c) {
        this.statusMaterial = i;
        this.statusColor = c;
    }

    public ChatColor getStatusColor() {
        return statusColor;
    }

    public CompMaterial getStatusMaterial() {
        return statusMaterial;
    }
}
