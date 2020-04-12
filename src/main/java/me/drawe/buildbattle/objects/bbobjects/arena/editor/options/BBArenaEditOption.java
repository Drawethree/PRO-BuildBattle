package me.drawe.buildbattle.objects.bbobjects.arena.editor.options;

import lombok.Getter;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class BBArenaEditOption<T> {

	protected ItemStack item;
	protected T value;

	public BBArenaEditOption(ItemStack item, T initialValue) {
		this.item = item;
		this.value = initialValue;
	}

	public abstract boolean handleClick(ClickType click);

	protected abstract void updateItem();
}
