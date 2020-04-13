package me.drawethree.buildbattle.objects.bbobjects.sign;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class BBArenaSpectateSign extends BBArenaSign {

	public BBArenaSpectateSign(BuildBattle parent, Location loc) {
		super(parent, BBSignType.SPECTATE, loc);
	}

	public BBArenaSpectateSign(BuildBattle plugin, BBArena arena, Location location) {
		super(plugin, BBSignType.SPECTATE, location, arena);
	}

	@Override
	public void handleClick(Player whoClicked, Action clickType) {
		if (clickType == Action.RIGHT_CLICK_BLOCK)
			this.getParent().getSpectatorManager().spectate(whoClicked, this.arena);
	}

	@Override
	public void update() {
		if (Bukkit.getPluginManager().isPluginEnabled(BuildBattle.getInstance())) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), () -> {
				sign.setLine(0, Message.SIGN_SPECTATE_FIRST_LINE.getMessage().replace("%arena%", this.arena.getName()));
				sign.setLine(1, Message.SIGN_SPECTATE_SECOND_LINE.getMessage().replace("%arena%", this.arena.getName()));
				sign.setLine(2, Message.SIGN_SPECTATE_THIRD_LINE.getMessage().replace("%arena%", this.arena.getName()));
				sign.setLine(3, Message.SIGN_SPECTATE_FOURTH_LINE.getMessage().replace("%arena%", this.arena.getName()));
				sign.update(true);
			}, 20L);
		}
	}
}
