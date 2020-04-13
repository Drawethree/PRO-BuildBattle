package me.drawethree.buildbattle.objects.bbobjects;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.compatbridge.model.CompDye;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class BBBannerCreator {

    private Player player;
    private ItemStack createdBanner;
    private CompDye selectedColor;

    public BBBannerCreator(Player p) {
        this.player = p;
        this.createdBanner = ItemUtil.create(CompMaterial.WHITE_BANNER,1, Message.FINAL_BANNER_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getInstance().getSettings().getFinalBannerLore()), null,null);
        this.selectedColor = CompDye.WHITE;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getCreatedBanner() {
        return createdBanner;
    }

    public void selectColor(CompDye color) {
        this.selectedColor = color;
        BuildBattle.getInstance().getOptionsManager().openPatternsInventory(this);
    }

    public void addPattern(PatternType type) {
        BannerMeta meta = (BannerMeta) createdBanner.getItemMeta();
        meta.addPattern(new Pattern(selectedColor.getDye(), type));
        createdBanner.setItemMeta(meta);
        BuildBattle.getInstance().getOptionsManager().openColorsInventory(this);
    }

    public void giveItem() {
        player.getInventory().addItem(createdBanner);
        BuildBattle.getInstance().getBannerCreatorManager().removeBannerCreator(this);
    }
}
