package me.drawethree.buildbattle.objects.bbobjects.arena.editor.options;

import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.inventory.meta.ItemMeta;

public class GameTimeOption extends BBArenaEditIntOption {

	public GameTimeOption(int initialValue) {
		super(ItemUtil.create(CompMaterial.PAPER, 1, "Game Time: §e" + initialValue + "s", ItemUtil.makeLore("", "&8(Click to adjust)", "&7< &c-10s   &a+10s &7>")), initialValue, 10);
	}

	@Override
	protected void updateItem() {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Game Time: §e" + value + "s");
		item.setItemMeta(meta);
	}
}
