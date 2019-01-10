package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.Bukkit;
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
        voteInventory = Bukkit.createInventory(null, BBSettings.getThemesToVote()*9, Message.GUI_THEME_VOTING_TITLE.getMessage());
        this.winner = null;
        for(BBTheme theme : themesVoted) {
            voteInventory.setItem(theme.getSlotInInventory(), ItemUtil.create(CompMaterial.SIGN, 1, Message.GUI_THEME_VOTING_INVENTORY_THEMES_DISPLAYNAME.getMessage(), ItemUtil.convertThemeLore(theme, BBSettings.getThemeVotingLore(), (int) BBSettings.getThemeVotingTime()) , null,null));
            voteInventory.setItem(theme.getSlotInInventory()+1, ItemUtil.create(CompMaterial.IRON_BARS, 1, "&a", ItemUtil.makeLore(""),null,null));
        }
    }

    private void resetInventory() {
        for(BBTheme theme : themesVoted) {
            voteInventory.setItem(theme.getSlotInInventory(), ItemUtil.create(CompMaterial.SIGN, 1, Message.GUI_THEME_VOTING_INVENTORY_THEMES_DISPLAYNAME.getMessage(), ItemUtil.convertThemeLore(theme, BBSettings.getThemeVotingLore(), (int) BBSettings.getThemeVotingTime()) , null,null));
            voteInventory.setItem(theme.getSlotInInventory()+1, ItemUtil.create(CompMaterial.IRON_BARS, 1, "&a", ItemUtil.makeLore(""),null,null));
        }
    }

    private List<BBTheme> getRandomThemesToVote() {
        List<BBTheme> returnList = new ArrayList<>();
        List<String> themes = null;
        switch (arena.getGameType()) {
            case SOLO:
                themes = new ArrayList<>(BBSettings.getSoloThemes());
                break;
            case TEAM:
                themes = new ArrayList<>(BBSettings.getTeamThemes());
                break;
        }
        int slot = 0;
        Random ran = new Random();
        while(returnList.size() != BBSettings.getThemesToVote()) {
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

    public void openThemeVoting(Player p) {
        int superVotesAmount = 0;

        BBPlayerStats pStats = PlayerManager.getInstance().getPlayerStats(p);
        if(pStats != null) {
            superVotesAmount = pStats.getSuperVotes();
        }

        Inventory openInv = Bukkit.createInventory(null, voteInventory.getSize(), voteInventory.getTitle());

        openInv.setContents(voteInventory.getContents());

        for(BBTheme theme : themesVoted) {
            openInv.setItem(theme.getSlotInInventory() + 8, ItemUtil.getSuperVoteItem(superVotesAmount,theme));
        }

        p.openInventory(openInv);
    }

    public void updateVoting(int timeLeft) {
        for(BBTheme theme : themesVoted) {
            if(votedPlayers.size() == 0) {
                theme.setPercentage(0);
            } else {
                double divided = (double) theme.getVotes() / getVotedPlayers().size();
                int percentage = (int) (divided*100);
                theme.setPercentage(percentage);
            }

            ItemStack themeItem = voteInventory.getItem(theme.getSlotInInventory());
            ItemMeta meta = themeItem.getItemMeta();
            meta.setLore(ItemUtil.convertThemeLore(theme, BBSettings.getThemeVotingLore(), timeLeft));
            themeItem.setItemMeta(meta);

            int numberOfGreens = 0;
            int percentage = theme.getPercentage();
            int onePart = 100 / 6;

            while(percentage - onePart >= 0) {
                numberOfGreens = numberOfGreens + 1;
                percentage = percentage - onePart;
            }

            for(int i = theme.getSlotInInventory() + 2;i < theme.getSlotInInventory() + 8;i++) {
                if(numberOfGreens > 0) {
                    voteInventory.setItem(i, ItemUtil.create(CompMaterial.LIME_STAINED_GLASS_PANE, 1,theme.getPercentage() + "%", ItemUtil.makeLore(""), null,null));
                    numberOfGreens = numberOfGreens - 1;
                } else {
                    voteInventory.setItem(i, ItemUtil.create(CompMaterial.RED_STAINED_GLASS_PANE, 1, theme.getPercentage() + "%", ItemUtil.makeLore(""), null,null));
                }
            }
        }
        arena.getPlayers().forEach(player -> openThemeVoting(player));
    }

    public HashMap<Player,BBTheme> getVotedPlayers() {
        return votedPlayers;
    }

    public BBTheme getWinner() {
        return winner;
    }

    public void setWinner() {
        BBTheme voteWinner = null;
        for(BBTheme theme : themesVoted) {
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
        for(BBTheme theme : themesVoted) {
            if(theme.getSlotInInventory() == slot || theme.getSlotInInventory() + 8 == slot) {
                return theme;
            }
        }
        return null;
    }

    public BBArena getArena() {
        return arena;
    }

    public void superVote(Player who, BBTheme selectedTheme) {
        winner = selectedTheme;
        arena.startGame(selectedTheme.getName(), false);
        PlayerManager.getInstance().broadcastToAllPlayersInArena(arena, Message.THEME_WAS_SUPER_VOTED.getChatMessage().replaceAll("%theme%", selectedTheme.getName()).replaceAll("%player%", who.getName()));
    }

    public void reset() {
        this.votedPlayers = new HashMap<>();
        this.themesVoted = getRandomThemesToVote();
        this.resetInventory();
    }
}
