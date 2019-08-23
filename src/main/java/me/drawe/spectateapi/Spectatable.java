package me.drawe.spectateapi;

import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public interface Spectatable<E extends Player> {

    ItemStack QUIT_SPECTATE_ITEM = ItemUtil.create(CompMaterial.BARRIER, 1, "&cQuit Spectate", Arrays.asList(new String[]{"§7Click to quit spectating"}), null, null);
    ItemStack SPECTATE_ITEM = ItemUtil.create(CompMaterial.COMPASS, 1, "&eSpectate", Arrays.asList(new String[]{"§7Click to open up spectate inventory"}), null, null);

    Inventory getSpectateInventory();
    List<E> getPlayersToSpectate();

    void refreshSpectateInventory();
    void spectate(E e);
    void unspectate(E e);
}
