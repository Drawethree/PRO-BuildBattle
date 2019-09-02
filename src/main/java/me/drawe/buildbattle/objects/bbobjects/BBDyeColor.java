package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.utils.compatbridge.model.CompDye;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;


public enum BBDyeColor {

    BLACK(CompDye.BLACK, CompMaterial.INK_SAC.toItem()),
    BLUE(CompDye.BLUE, CompMaterial.LAPIS_LAZULI.toItem()),
    BROWN(CompDye.BROWN,  CompMaterial.COCOA_BEANS.toItem()),
    CYAN(CompDye.CYAN,  CompMaterial.CYAN_DYE.toItem()),
    GRAY(CompDye.GRAY,  CompMaterial.GRAY_DYE.toItem()),
    GREEN(CompDye.GREEN,  CompMaterial.GREEN_DYE.toItem()),
    LIGHT_BLUE(CompDye.LIGHT_BLUE,  CompMaterial.LIGHT_BLUE_DYE.toItem()),
    LIGHT_GRAY(CompDye.LIGHT_GRAY,  CompMaterial.LIGHT_GRAY_DYE.toItem()),
    LIME(CompDye.LIME,  CompMaterial.LIME_DYE.toItem()),
    MAGENTA(CompDye.MAGENTA,  CompMaterial.MAGENTA_DYE.toItem()),
    ORANGE(CompDye.ORANGE,  CompMaterial.ORANGE_DYE.toItem()),
    PINK(CompDye.PINK,  CompMaterial.PINK_DYE.toItem()),
    PURPLE(CompDye.PURPLE,  CompMaterial.PURPLE_DYE.toItem()),
    RED(CompDye.RED,  CompMaterial.POPPY.toItem()),
    WHITE(CompDye.WHITE,  CompMaterial.BONE_MEAL.toItem()),
    YELLOW(CompDye.YELLOW,  CompMaterial.YELLOW_DYE.toItem());

    private ItemStack item;
    private CompDye color;

    BBDyeColor(CompDye color, ItemStack item) {
        this.color = color;
        this.item = item;
    }

    public static BBDyeColor getByColor(DyeColor color) {
        for(BBDyeColor color1 : values()) {
            if(color1.getColor().getDye().equals(color)) {
                return color1;
            }
        }
        return null;
    }

    public ItemStack getItem() {
        return item;
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
