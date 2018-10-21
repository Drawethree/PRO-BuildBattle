package me.drawe.buildbattle.objects.bbobjects;

import me.kangarko.compatbridge.model.CompDye;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public enum BBDyeColor {

    BLACK(0, DyeColor.BLACK, CompMaterial.INK_SAC.toItem()),
    BLUE(1,DyeColor.BLUE,  CompMaterial.LAPIS_LAZULI.toItem()),
    BROWN(2,DyeColor.BROWN,  CompMaterial.COCOA_BEANS.toItem()),
    CYAN(3,DyeColor.CYAN,  CompMaterial.CYAN_DYE.toItem()),
    GRAY(4,DyeColor.GRAY,  CompMaterial.GRAY_DYE.toItem()),
    GREEN(5,DyeColor.GREEN,  CompMaterial.CACTUS_GREEN.toItem()),
    LIGHT_BLUE(6,DyeColor.LIGHT_BLUE,  CompMaterial.LIGHT_BLUE_DYE.toItem()),
    LIGHT_GRAY(7,DyeColor.LIGHT_GRAY,  CompMaterial.LIGHT_GRAY_DYE.toItem()),
    LIME(8,DyeColor.LIME,  CompMaterial.LIME_DYE.toItem()),
    MAGENTA(9,DyeColor.MAGENTA,  CompMaterial.MAGENTA_DYE.toItem()),
    ORANGE(10,DyeColor.ORANGE,  CompMaterial.ORANGE_DYE.toItem()),
    PINK(11,DyeColor.PINK,  CompMaterial.PINK_DYE.toItem()),
    PURPLE(12,DyeColor.PURPLE,  CompMaterial.PURPLE_DYE.toItem()),
    RED(13,DyeColor.RED,  CompMaterial.ROSE_RED.toItem()),
    WHITE(14,DyeColor.WHITE,  CompMaterial.BONE_MEAL.toItem()),
    YELLOW(15,DyeColor.YELLOW,  CompMaterial.DANDELION_YELLOW.toItem());

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

    public static BBDyeColor getByMaterial(CompMaterial type) {
        for(BBDyeColor d : values()) {
            if(d.getItem().getType() == type.getMaterial()) {
                return d;
            }
        }
        return null;
    }

    public DyeColor getColor() {
        return color;
    }
}
