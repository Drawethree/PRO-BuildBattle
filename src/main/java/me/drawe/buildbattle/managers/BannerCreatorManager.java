package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.objects.bbobjects.BBBannerCreator;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BannerCreatorManager {

    private static BannerCreatorManager ourInstance = new BannerCreatorManager();
    private static HashMap<Player, BBBannerCreator> activeBannerCreators = new HashMap<>();

    public static BannerCreatorManager getInstance() {
        return ourInstance;
    }

    private BannerCreatorManager() {
    }


    public BBBannerCreator addBannerCreator(Player p) {
        BBBannerCreator bannerCreator = new BBBannerCreator(p);
        OptionsManager.getInstance().openColorsInventory(bannerCreator);
        activeBannerCreators.put(p, bannerCreator);
        return bannerCreator;
    }

    public void removeBannerCreator(BBBannerCreator creator) {
        activeBannerCreators.remove(creator.getPlayer());
    }

    public BBBannerCreator getBannerCreator(Player p) {
        return activeBannerCreators.get(p);
    }
}
