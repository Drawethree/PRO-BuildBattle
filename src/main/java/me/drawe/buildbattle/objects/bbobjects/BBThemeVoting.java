package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BBThemeVoting {

    private Inventory voteInventory;
    private List<BBTheme> themesVoted;
    private HashMap<Player,BBTheme> votedPlayers;
    private BBTheme winner;
    private BBArena arena;

    public BBThemeVoting(BBArena a) {
        this.arena = a;
        this.votedPlayers = new HashMap<>();
        this.themesVoted = getRandomThemesToVote();
        voteInventory = Bukkit.createInventory(null, GameManager.getThemesToVote()*9, Message.GUI_THEME_VOTING_TITLE.getMessage());
        this.winner = null;
        for(BBTheme theme : getThemesVoted()) {
            voteInventory.setItem(theme.getSlotInInventory(), ItemCreator.create(Material.SIGN, 1, (byte) 0, Message.GUI_THEME_VOTING_INVENTORY_THEMES_DISPLAYNAME.getMessage(), ItemCreator.convertThemeLore(theme, GameManager.getThemeVotingLore()) , null,null));
            voteInventory.setItem(theme.getSlotInInventory()+1, ItemCreator.create(Material.IRON_FENCE, 1,(byte) 0, "&a", ItemCreator.makeLore(""),null,null));
        }
    }

    public void setThemesVoted(List<BBTheme> themes) {
        this.themesVoted = themes;
    }

    public void resetInventory() {
        for(BBTheme theme : getThemesVoted()) {
            voteInventory.setItem(theme.getSlotInInventory(), ItemCreator.create(Material.SIGN, 1, (byte) 0, Message.GUI_THEME_VOTING_INVENTORY_THEMES_DISPLAYNAME.getMessage(), ItemCreator.convertThemeLore(theme, GameManager.getThemeVotingLore()) , null,null));
            voteInventory.setItem(theme.getSlotInInventory()+1, ItemCreator.create(Material.IRON_FENCE, 1,(byte) 0, "&a", ItemCreator.makeLore(""),null,null));
        }
    }

    public List<BBTheme> getRandomThemesToVote() {
        List<BBTheme> returnList = new ArrayList<>();
        List<String> themes = new ArrayList<>(GameManager.getThemes());
        Random ran = new Random();
        int slot = 0;
        while(returnList.size() != GameManager.getThemesToVote()) {
            String theme = themes.get(ran.nextInt(themes.size()));
            BBTheme bbTheme = new BBTheme(theme,0,slot);
            returnList.add(bbTheme);
            themes.remove(theme);
            slot = slot + 9;
        }
        return returnList;
    }

    public List<BBTheme> getThemesVoted() {
        return themesVoted;
    }

    public Inventory getVoteInventory() {
        return voteInventory;
    }

    public void updateVoting() {
        for(BBTheme theme : getThemesVoted()) {
            if(getVotedPlayers().size() == 0) {
                theme.setPercentage(0);
            } else {
                double divided = (double) theme.getVotes() / getVotedPlayers().size();
                int percentage = (int) (divided*100);
                theme.setPercentage(percentage);
            }

            ItemStack themeItem = voteInventory.getItem(theme.getSlotInInventory());
            ItemMeta meta = themeItem.getItemMeta();
            meta.setLore(ItemCreator.convertThemeLore(theme, GameManager.getThemeVotingLore()));
            themeItem.setItemMeta(meta);

            int numberOfGreens = 0;
            int percentage = theme.getPercentage();
            int onePart = 100 / 7;
            while(percentage - onePart >= 0) {
                numberOfGreens = numberOfGreens + 1;
                percentage = percentage - onePart;
            }
            for(int i = theme.getSlotInInventory() + 2;i < theme.getSlotInInventory() + 9;i++) {
                if(numberOfGreens > 0) {
                    voteInventory.setItem(i, ItemCreator.create(Material.STAINED_GLASS_PANE, 1, (byte) 5,theme.getPercentage() + "%", ItemCreator.makeLore(""), null,null));
                    numberOfGreens = numberOfGreens - 1;
                } else {
                    voteInventory.setItem(i, ItemCreator.create(Material.STAINED_GLASS_PANE, 1, (byte) 14, theme.getPercentage() + "%", ItemCreator.makeLore(""), null,null));
                }
            }
        }
    }

    public HashMap<Player,BBTheme> getVotedPlayers() {
        return votedPlayers;
    }
    public void setVotedPlayers(HashMap<Player,BBTheme> players) {
        this.votedPlayers = players;
    }

    public BBTheme getWinner() {
        return winner;
    }

    public void setWinner() {
        BBTheme voteWinner = null;
        for(BBTheme theme : getThemesVoted()) {
            if(voteWinner == null) {
                voteWinner = theme;
            } else {
                if (theme.getPercentage() > voteWinner.getPercentage()) {
                    voteWinner = theme;
                }
            }
        }
        winner = voteWinner;
    }

    public BBTheme getThemeBySlot(int slot) {
        for(BBTheme theme : getThemesVoted()) {
            if(theme.getSlotInInventory() == slot) {
                return theme;
            }
        }
        return null;
    }

    public BBArena getArena() {
        return arena;
    }
}
