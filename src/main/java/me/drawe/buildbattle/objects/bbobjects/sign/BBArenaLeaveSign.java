package me.drawe.buildbattle.objects.bbobjects.sign;

import me.drawe.buildbattle.BuildBattle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class BBArenaLeaveSign extends BBArenaSign {

    public BBArenaLeaveSign(BuildBattle plugin, Location location) {
        super(plugin, BBSignType.LEAVE, location);
    }

    @Override
    public void handleClick(Player whoClicked, Action clickType) {
        if(clickType == Action.RIGHT_CLICK_BLOCK) {
            arena.removePlayer(whoClicked);
        }
    }

    @Override
    public void update() {

    }
}
