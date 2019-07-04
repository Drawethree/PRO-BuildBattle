package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.utils.compatbridge.model.CompDye;
import me.drawe.buildbattle.utils.compatbridge.model.XMaterial;
import org.bukkit.inventory.ItemStack;


public enum BBDyeColor {

    BLACK(0, CompDye.BLACK, XMaterial.INK_SAC.parseItem()),
    BLUE(1, CompDye.BLUE,  XMaterial.LAPIS_LAZULI.parseItem()),
    BROWN(2, CompDye.BROWN,  XMaterial.COCOA_BEANS.parseItem()),
    CYAN(3, CompDye.CYAN,  XMaterial.CYAN_DYE.parseItem()),
    GRAY(4, CompDye.GRAY,  XMaterial.GRAY_DYE.parseItem()),
    GREEN(5, CompDye.GREEN,  XMaterial.GREEN_DYE.parseItem()),
    LIGHT_BLUE(6, CompDye.LIGHT_BLUE,  XMaterial.LIGHT_BLUE_DYE.parseItem()),
    LIGHT_GRAY(7, CompDye.LIGHT_GRAY,  XMaterial.LIGHT_GRAY_DYE.parseItem()),
    LIME(8, CompDye.LIME,  XMaterial.LIME_DYE.parseItem()),
    MAGENTA(9, CompDye.MAGENTA,  XMaterial.MAGENTA_DYE.parseItem()),
    ORANGE(10, CompDye.ORANGE,  XMaterial.ORANGE_DYE.parseItem()),
    PINK(11, CompDye.PINK,  XMaterial.PINK_DYE.parseItem()),
    PURPLE(12, CompDye.PURPLE,  XMaterial.PURPLE_DYE.parseItem()),
    RED(13, CompDye.RED,  XMaterial.POPPY.parseItem()),
    WHITE(14, CompDye.WHITE,  XMaterial.BONE_MEAL.parseItem()),
    YELLOW(15, CompDye.YELLOW,  XMaterial.DANDELION.parseItem());

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
