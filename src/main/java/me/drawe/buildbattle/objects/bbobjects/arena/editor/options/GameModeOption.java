package me.drawe.buildbattle.objects.bbobjects.arena.editor.options;

import me.drawe.buildbattle.objects.bbobjects.BBGameMode;
import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;

public class GameModeOption extends BBArenaEditOption<BBGameMode> {

	public GameModeOption(BBGameMode gameMode) {
		super(ItemUtil.create(CompMaterial.PLAYER_HEAD, 1, "Game Mode: §e" + gameMode.name(), ItemUtil.makeLore("", "&8(Click to change)")), gameMode);
	}

	@Override
	public boolean handleClick(ClickType click) {
		if (value == BBGameMode.SOLO) {
			value = BBGameMode.TEAM;
		} else {
			value = BBGameMode.SOLO;
		}
		this.updateItem();
		return true;
	}

	@Override
	protected void updateItem() {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Game Mode: §e" + value.name());
		item.setItemMeta(meta);
	}
}
