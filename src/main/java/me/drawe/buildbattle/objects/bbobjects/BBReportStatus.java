package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.utils.compatbridge.model.XMaterial;
import org.bukkit.ChatColor;

public enum BBReportStatus {

    PENDING(XMaterial.YELLOW_TERRACOTTA, ChatColor.YELLOW),
    SOLVED(XMaterial.LIME_TERRACOTTA, ChatColor.GREEN);

    private ChatColor statusColor;
    private XMaterial statusMaterial;

    BBReportStatus(XMaterial i, ChatColor c) {
        this.statusMaterial = i;
        this.statusColor = c;
    }

    public ChatColor getStatusColor() {
        return statusColor;
    }

    public XMaterial getStatusMaterial() {
        return statusMaterial;
    }
}
