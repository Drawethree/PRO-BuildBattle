package me.drawe.buildbattle.objects.bbobjects;

import me.kangarko.compatbridge.model.CompDye;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.inventory.ItemStack;


public enum BBDyeColor {

    BLACK(0, CompDye.BLACK, CompMaterial.INK_SAC.toItem()),
    BLUE(1, CompDye.BLUE,  CompMaterial.LAPIS_LAZULI.toItem()),
    BROWN(2, CompDye.BROWN,  CompMaterial.COCOA_BEANS.toItem()),
    CYAN(3, CompDye.CYAN,  CompMaterial.CYAN_DYE.toItem()),
    GRAY(4, CompDye.GRAY,  CompMaterial.GRAY_DYE.toItem()),
    GREEN(5, CompDye.GREEN,  CompMaterial.CACTUS_GREEN.toItem()),
    LIGHT_BLUE(6, CompDye.LIGHT_BLUE,  CompMaterial.LIGHT_BLUE_DYE.toItem()),
    LIGHT_GRAY(7, CompDye.LIGHT_GRAY,  CompMaterial.LIGHT_GRAY_DYE.toItem()),
    LIME(8, CompDye.LIME,  CompMaterial.LIME_DYE.toItem()),
    MAGENTA(9, CompDye.MAGENTA,  CompMaterial.MAGENTA_DYE.toItem()),
    ORANGE(10, CompDye.ORANGE,  CompMaterial.ORANGE_DYE.toItem()),
    PINK(11, CompDye.PINK,  CompMaterial.PINK_DYE.toItem()),
    PURPLE(12, CompDye.PURPLE,  CompMaterial.PURPLE_DYE.toItem()),
    RED(13, CompDye.RED,  CompMaterial.ROSE_RED.toItem()),
    WHITE(14, CompDye.WHITE,  CompMaterial.BONE_MEAL.toItem()),
    YELLOW(15, CompDye.YELLOW,  CompMaterial.DANDELION_YELLOW.toItem());

    private byte data;
    private ItemStack item;
    private CompDye color;

    BBDyeColor(int data, CompDye color, ItemStack item) {
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

    public CompDye getColor() {
        return color;
    }

    public static BBDyeColor getByItem(ItemStack type) {
        for(BBDyeColor d : values()) {
            if(d.getItem().isSimilar(type)) {
                return d;
            }
        }
        return null;
    }
}
