package me.drawethree.buildbattle.objects.bbobjects.arena.editor.options;

import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamSizeOption extends BBArenaEditIntOption {

	public TeamSizeOption(int initialValue) {
		super(ItemUtil.create(CompMaterial.PAPER, 1, "Team Size: §e" + initialValue, ItemUtil.makeLore("", "&8(Click to adjust)", "&7< &c-1   &a+1 &7>")), initialValue, 1);
	}

	@Override
	protected void updateItem() {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Team Size: §e" + value);
		item.setItemMeta(meta);
	}
}
