package me.drawe.buildbattle.objects.bbobjects;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public enum BBDyeColor {

    BLACK(0, DyeColor.BLACK, new ItemStack(Material.INK_SAC,1)),
    BLUE(1,DyeColor.BLUE,  new ItemStack(Material.LAPIS_LAZULI,1)),
    BROWN(2,DyeColor.BROWN,  new ItemStack(Material.COCOA_BEANS, 1)),
    CYAN(3,DyeColor.CYAN,  new ItemStack(Material.CYAN_DYE, 1)),
    GRAY(4,DyeColor.GRAY,  new ItemStack(Material.GRAY_DYE, 1)),
    GREEN(5,DyeColor.GREEN,  new ItemStack(Material.CACTUS_GREEN, 1)),
    LIGHT_BLUE(6,DyeColor.LIGHT_BLUE,  new ItemStack(Material.LIGHT_BLUE_DYE, 1)),
    LIGHT_GRAY(7,DyeColor.LIGHT_GRAY,  new ItemStack(Material.LIGHT_GRAY_DYE, 1)),
    LIME(8,DyeColor.LIME,  new ItemStack(Material.LIME_DYE, 1)),
    MAGENTA(9,DyeColor.MAGENTA,  new ItemStack(Material.MAGENTA_DYE, 1)),
    ORANGE(10,DyeColor.ORANGE,  new ItemStack(Material.ORANGE_DYE, 1)),
    PINK(11,DyeColor.PINK,  new ItemStack(Material.PINK_DYE, 1)),
    PURPLE(12,DyeColor.PURPLE,  new ItemStack(Material.PURPLE_DYE, 1)),
    RED(13,DyeColor.RED,  new ItemStack(Material.ROSE_RED, 1)),
    WHITE(14,DyeColor.WHITE,  new ItemStack(Material.BONE_MEAL, 1)),
    YELLOW(15,DyeColor.YELLOW,  new ItemStack(Material.DANDELION_YELLOW, 1));

    private byte data;
    private ItemStack item;
    private DyeColor color;

    BBDyeColor(int data, DyeColor color, ItemStack item) {
        this.data = (byte) data;
        this.color = color;
        this.item = item;
    }

    public byte getData() {
        return data;
    }

    public ItemStack getItem() {
        return item;
    }

    public static BBDyeColor getById(int i) {
        for(BBDyeColor d : values()) {
            if(d.getData() == i) {
                return d;
            }
        }
        return null;
    }

    public static BBDyeColor getByMaterial(Material type) {
        for(BBDyeColor d : values()) {
            if(d.getItem().getType() == type) {
                return d;
            }
        }
        return null;
    }

    public DyeColor getColor() {
        return color;
    }
}
