package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.utils.ItemCreator;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public enum BBDyeColor {

    BLACK(0, DyeColor.BLACK, ItemCreator.create(Material.INK_SAC,1, "§0Black")),
    BLUE(1,DyeColor.BLUE,  ItemCreator.create(Material.LAPIS_LAZULI,1, "§1Blue")),
    BROWN(2,DyeColor.BROWN,  ItemCreator.create(Material.COCOA_BEANS, 1, "§6Brown")),
    CYAN(3,DyeColor.CYAN,  ItemCreator.create(Material.CYAN_DYE, 1, "§cCyan")),
    GRAY(4,DyeColor.GRAY,  ItemCreator.create(Material.GRAY_DYE, 1, "§8Gray")),
    GREEN(5,DyeColor.GREEN,  ItemCreator.create(Material.CACTUS_GREEN, 1, "§2Green")),
    LIGHT_BLUE(6,DyeColor.LIGHT_BLUE,  ItemCreator.create(Material.LIGHT_BLUE_DYE, 1, "§bLight Blue")),
    LIGHT_GRAY(7,DyeColor.LIGHT_GRAY,  ItemCreator.create(Material.LIGHT_GRAY_DYE, 1, "§7Light Gray")),
    LIME(8,DyeColor.LIME,  ItemCreator.create(Material.LIME_DYE, 1, "§aLime")),
    MAGENTA(9,DyeColor.MAGENTA,  ItemCreator.create(Material.MAGENTA_DYE, 1, "§dMagenta")),
    ORANGE(10,DyeColor.ORANGE,  ItemCreator.create(Material.ORANGE_DYE, 1,"§6Orange")),
    PINK(11,DyeColor.PINK,  ItemCreator.create(Material.PINK_DYE, 1, "§dPink")),
    PURPLE(12,DyeColor.PURPLE,  ItemCreator.create(Material.PURPLE_DYE, 1, "§5Purple")),
    RED(13,DyeColor.RED,  ItemCreator.create(Material.ROSE_RED, 1, "§cRed")),
    WHITE(14,DyeColor.WHITE,  ItemCreator.create(Material.BONE_MEAL, 1, "§fWhite")),
    YELLOW(15,DyeColor.YELLOW,  ItemCreator.create(Material.DANDELION_YELLOW, 1, "§eYellow"));

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
