package me.drawethree.spectateapi;

import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public interface Spectatable<E extends Player> {

    ItemStack QUIT_SPECTATE_ITEM = ItemUtil.create(CompMaterial.BARRIER, 1, "&cQuit Spectate", Collections.singletonList("§7Click to quit spectating"), null, null);
    ItemStack SPECTATE_ITEM = ItemUtil.create(CompMaterial.COMPASS, 1, "&eSpectate", Collections.singletonList("§7Click to open up spectate inventory"), null, null);

    Inventory getSpectateInventory();

    List<E> getPlayersToSpectate();

    default void refreshSpectateInventory() {
        this.getSpectateInventory().clear();
        for (Player p : this.getPlayersToSpectate()) {
            this.getSpectateInventory().addItem(ItemUtil.createPlayerHead(p));
        }
    }

    void spectate(E e);

    void unspectate(E e);
}
