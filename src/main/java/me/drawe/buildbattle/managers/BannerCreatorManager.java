package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.objects.bbobjects.BBBannerCreator;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BannerCreatorManager {
    private static BannerCreatorManager ourInstance = new BannerCreatorManager();
    private static List<BBBannerCreator> activeBannerCreators = new ArrayList<>();
    public static BannerCreatorManager getInstance() {
        return ourInstance;
    }

    private BannerCreatorManager() {
    }

    public static List<BBBannerCreator> getActiveBannerCreators() {
        return activeBannerCreators;
    }

    public static void setActiveBannerCreators(List<BBBannerCreator> activeBannerCreators) {
        BannerCreatorManager.activeBannerCreators = activeBannerCreators;
    }

    public BBBannerCreator addBannerCreator(Player p) {
        BBBannerCreator bannerCreator = new BBBannerCreator(p);
        OptionsManager.getInstance().openColorsInventory(bannerCreator);
        activeBannerCreators.add(bannerCreator);
        return bannerCreator;
    }

    public void removeBannerCreator(BBBannerCreator creator) {
        activeBannerCreators.remove(creator);
    }

    public BBBannerCreator getBannerCreator(Player p) {
        for(BBBannerCreator creator : activeBannerCreators) {
            if(creator.getPlayer().equals(p)) {
                return creator;
            }
        }
        return null;
    }
}
