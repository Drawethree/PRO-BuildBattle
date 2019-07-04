package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompSound;
import me.drawe.buildbattle.utils.compatbridge.model.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum Votes {

    NONE(Message.VOTING_NONE.getMessage(), 0, null, CompSound.CAT_MEOW, 0.0F),
    VERY_BAD(Message.VOTING_VERY_BAD.getMessage(), 0, ItemUtil.create(XMaterial.RED_TERRACOTTA, 1, Message.VOTING_VERY_BAD.getMessage(), ItemUtil.makeLore(""), null, null), CompSound.CAT_MEOW, 0.1F),
    BAD(Message.VOTING_BAD.getMessage(), 1, ItemUtil.create(XMaterial.ORANGE_TERRACOTTA, 1, Message.VOTING_BAD.getMessage(), ItemUtil.makeLore(""), null, null), CompSound.CAT_MEOW, 0.5F),
    OK(Message.VOTING_OK.getMessage(), 2, ItemUtil.create(XMaterial.LIME_TERRACOTTA, 1, Message.VOTING_OK.getMessage(), ItemUtil.makeLore(""), null, null), CompSound.CAT_MEOW, 1.0F),
    NICE(Message.VOTING_NICE.getMessage(), 3, ItemUtil.create(XMaterial.GREEN_TERRACOTTA, 1, Message.VOTING_NICE.getMessage(), ItemUtil.makeLore(""), null, null), CompSound.CAT_MEOW, 1.25F),
    EPIC(Message.VOTING_EPIC.getMessage(), 4, ItemUtil.create(XMaterial.PURPLE_TERRACOTTA, 1, Message.VOTING_EPIC.getMessage(), ItemUtil.makeLore(""), null, null), CompSound.CAT_MEOW, 1.5F),
    LEGENDARY(Message.VOTING_LEGENDARY.getMessage(), 5, ItemUtil.create(XMaterial.YELLOW_TERRACOTTA, 1, Message.VOTING_LEGENDARY.getMessage(), ItemUtil.makeLore(""), null, null), CompSound.CAT_MEOW, 2.0F);

    private String prefix;
    private int weight;
    private ItemStack item;
    private CompSound sound;
    private float pitch;

    Votes(String prefix, int weight, ItemStack itemStack, CompSound sound, float pitch) {
        this.weight = weight;
        this.item = itemStack;
        this.prefix = prefix;
        this.sound = sound;
        this.pitch = pitch;
    }

    public int getWeight() {
        return weight;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public static Votes getVoteItemByPoints(Integer integer) {
        for (Votes item : values()) {
            if ((item.getWeight() == integer) && (item != Votes.NONE)) {
                return item;
            }
        }
        return null;
    }

    public static Votes getVoteByItemStack(ItemStack item) {
        for (Votes vote : values()) {
            if (vote.getItem() != null && vote.getItem().isSimilar(item)) {
                return vote;
            }
        }
        return null;
    }

    public CompSound getSound() {
        return sound;
    }

    public float getPitch() {
        return pitch;
    }
}
