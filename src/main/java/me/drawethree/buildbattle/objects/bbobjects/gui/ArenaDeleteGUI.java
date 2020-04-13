package me.drawethree.buildbattle.objects.bbobjects.gui;

import me.drawethree.buildbattle.objects.bbobjects.arena.editor.BBArenaEdit;
import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import me.drawethree.buildbattle.utils.compatbridge.model.CompSound;
import org.bukkit.entity.Player;

public class ArenaDeleteGUI extends ConfirmationGUI {

    private BBArenaEdit currentEdit;

    public ArenaDeleteGUI(BBArenaEdit currentEdit) {
        super("Are you Sure?", ItemUtil.create(CompMaterial.WRITABLE_BOOK, 1, "&aDelete Arena: &e" + currentEdit.getArena().getName(), ItemUtil.makeLore("", "&4&lWARNING!", "&cThis action cannot be undone !")));
        this.currentEdit = currentEdit;
    }

    @Override
    protected void onYesClick(Player player) {
        this.currentEdit.getArena().getPlugin().getArenaManager().removeArena(player, currentEdit.getArena());
        player.openInventory(this.currentEdit.getArena().getPlugin().getArenaManager().getEditArenasInventory());
        player.playSound(player.getLocation(), CompSound.EXPLODE.getSound(), 1.0F, 1.0F);
    }

    @Override
    protected void onNoClick(Player player) {
        player.closeInventory();
        player.openInventory(this.currentEdit.getEditInventory());
        player.playSound(player.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
    }
}
