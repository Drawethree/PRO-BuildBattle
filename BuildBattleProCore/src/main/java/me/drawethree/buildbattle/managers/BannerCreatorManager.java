package me.drawethree.buildbattle.managers;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBBannerCreator;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BannerCreatorManager {

    private HashMap<Player, BBBannerCreator> activeBannerCreators;
    private BuildBattle plugin;

    public BannerCreatorManager(BuildBattle plugin) {
        this.plugin = plugin;
        this.activeBannerCreators = new HashMap<>();
    }


    public BBBannerCreator addBannerCreator(Player p) {
        BBBannerCreator bannerCreator = new BBBannerCreator(p);
        this.plugin.getOptionsManager().openColorsInventory(bannerCreator);
        this.activeBannerCreators.put(p, bannerCreator);
        return bannerCreator;
    }

    public void removeBannerCreator(BBBannerCreator creator) {
        this.activeBannerCreators.remove(creator.getPlayer());
    }

    public BBBannerCreator getBannerCreator(Player p) {
        return this.activeBannerCreators.get(p);
    }
}
