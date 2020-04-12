package me.drawe.buildbattle.objects.bbobjects.arena.editor.options;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class BBArenaEditIntOption extends BBArenaEditOption<Integer> {

	private int amplifier;

	public BBArenaEditIntOption(ItemStack item, Integer initialValue, int amplifier) {
		super(item, initialValue);
		this.amplifier = amplifier;
	}

	@Override
	public boolean handleClick(ClickType click) {
		if (click == ClickType.RIGHT) {
			value += amplifier;
		} else if (click == ClickType.LEFT || click == ClickType.DOUBLE_CLICK) {
			if (value - amplifier != 0) {
				value -= amplifier;
			} else {
				return false;
			}
		} else {
			return false;
		}
		this.updateItem();
		return true;
	}
}
