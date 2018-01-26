package me.drawe.buildbattle.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public class FireworkUtil {

    public static void spawnRandomFirework(Location location) {
        Firework f = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(randomBoolean()).trail(randomBoolean()).with(randomType())
                .withColor(randomColor()).withFade(randomColor()).build());
        fm.setPower(1);
        f.setFireworkMeta(fm);
    }

    public static boolean randomBoolean() {
        Random rBoolean = new Random();
        boolean b = rBoolean.nextBoolean();
        return b;
    }

    public static Type randomType() {
        Random rType = new Random();
        int type = rType.nextInt(4);
        if (type == 0) {
            return Type.BALL;
        } else if (type == 1) {
            return Type.BALL_LARGE;
        } else if (type == 2) {
            return Type.BURST;
        } else if (type == 3) {
            return Type.CREEPER;
        } else if (type == 4) {
            return Type.STAR;
        }
        return null;
    }

    public static Color randomColor() {
        Random rColor = new Random();
        int color = rColor.nextInt(8);
        if (color == 0) {
            return Color.AQUA;
        } else if (color == 1) {
            return Color.FUCHSIA;
        } else if (color == 2) {
            return Color.GREEN;
        } else if (color == 3) {
            return Color.ORANGE;
        } else if (color == 4) {
            return Color.LIME;
        } else if (color == 5) {
            return Color.YELLOW;
        } else if (color == 6) {
            return Color.RED;
        } else if (color == 7) {
            return Color.PURPLE;
        }
        return null;
    }
}
