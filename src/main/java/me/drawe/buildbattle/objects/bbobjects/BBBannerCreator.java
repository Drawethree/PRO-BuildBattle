package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.BannerCreatorManager;
import me.drawe.buildbattle.managers.OptionsManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.ItemUtil;
import me.kangarko.compatbridge.model.CompDye;
import me.kangarko.compatbridge.model.CompMaterial;
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
        this.createdBanner = ItemUtil.create(CompMaterial.WHITE_BANNER,1, Message.FINAL_BANNER_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BBSettings.getFinalBannerLore()), null,null);
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
        OptionsManager.getInstance().openPatternsInventory(this);
    }
    public void addPattern(PatternType type) {
        BannerMeta meta = (BannerMeta) createdBanner.getItemMeta();
        meta.addPattern(new Pattern(selectedColor.getDye(), type));
        createdBanner.setItemMeta(meta);
        OptionsManager.getInstance().openColorsInventory(this);
    }

    public void giveItem() {
        player.getInventory().addItem(createdBanner);
        BannerCreatorManager.getInstance().removeBannerCreator(this);
    }
}
