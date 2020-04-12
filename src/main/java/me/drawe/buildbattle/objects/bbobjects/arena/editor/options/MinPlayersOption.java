package me.drawe.buildbattle.objects.bbobjects.arena.editor.options;

import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.inventory.meta.ItemMeta;

public class MinPlayersOption extends BBArenaEditIntOption {

	public MinPlayersOption(int initialValue) {
		super(ItemUtil.create(CompMaterial.PAPER, 1, "Min Players: §e" + initialValue, ItemUtil.makeLore("", "&8(Click to adjust)", "&7< &c-1   &a+1 &7>")), initialValue, 1);
	}


	@Override
	protected void updateItem() {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Min Players: §e" + value);
		item.setItemMeta(meta);
	}


}
