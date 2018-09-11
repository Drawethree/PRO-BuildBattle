package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.utils.ItemCreator;
import me.drawe.buildbattle.utils.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public enum Votes {

    NONE(Message.VOTING_NONE.getMessage(), 0, null, Sounds.CAT_MEOW.getSound(), 0.0F),
    VERY_BAD(Message.VOTING_VERY_BAD.getMessage(), 0, ItemCreator.create(Material.LEGACY_STAINED_CLAY, 1, (byte) 14, Message.VOTING_VERY_BAD.getMessage(), ItemCreator.makeLore(""), null, null), Sounds.CAT_MEOW.getSound(), 0.1F),
    BAD(Message.VOTING_BAD.getMessage(), 1, ItemCreator.create(Material.LEGACY_STAINED_CLAY, 1, (byte) 1, Message.VOTING_BAD.getMessage(), ItemCreator.makeLore(""), null, null),Sounds.CAT_MEOW.getSound(), 0.5F),
    OK(Message.VOTING_OK.getMessage(), 2, ItemCreator.create(Material.LEGACY_STAINED_CLAY, 1, (byte) 5, Message.VOTING_OK.getMessage(), ItemCreator.makeLore(""), null, null),Sounds.CAT_MEOW.getSound(), 1.0F),
    NICE(Message.VOTING_NICE.getMessage(), 3, ItemCreator.create(Material.LEGACY_STAINED_CLAY, 1, (byte) 13, Message.VOTING_NICE.getMessage(), ItemCreator.makeLore(""), null, null),Sounds.CAT_MEOW.getSound(),1.25F),
    EPIC(Message.VOTING_EPIC.getMessage(), 4, ItemCreator.create(Material.LEGACY_STAINED_CLAY, 1, (byte) 11, Message.VOTING_EPIC.getMessage(), ItemCreator.makeLore(""), null, null),Sounds.CAT_MEOW.getSound(), 1.5F),
    LEGENDARY(Message.VOTING_LEGENDARY.getMessage(), 5, ItemCreator.create(Material.LEGACY_STAINED_CLAY, 1, (byte) 4, Message.VOTING_LEGENDARY.getMessage(), ItemCreator.makeLore(""), null, null),Sounds.CAT_MEOW.getSound(), 2.0F);

    private String prefix;
    private int weight;
    private ItemStack item;
    private Sound sound;
    private float pitch;

    Votes(String prefix, int weight, ItemStack itemStack, Sound sound, float pitch) {
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
        for(Votes item : values()) {
            if((item.getWeight() == integer) && (item != Votes.NONE)) {
                return item;
            }
        }
        return null;
    }

    public static Votes getVoteByItemStack(ItemStack item) {
        for(Votes vote : values()) {
            if(vote.getItem() != null && vote.getItem().isSimilar(item)) {
                return vote;
            }
        }
        return null;
    }

    public Sound getSound() {
        return sound;
    }

    public float getPitch() {
        return pitch;
    }
}
