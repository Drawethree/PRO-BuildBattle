package me.kangarko.compatbridge.utils;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

/**
 * Utility class helps to determine which MC version is installed.
 */
public class VersionResolver {

    /**
     * Detects if we run at least Minecraft 1.9.
     */
    private static boolean atLeast1_9 = true;

    /**
     * Detects if we run at least Minecraft 1.8.
     */
    private static boolean atLeast1_8 = true;

    /**
     * Detects if we run at least Minecraft 1.12.
     */
    private static boolean atLeast1_12 = true;

    /**
     * Detects if we run at least Minecraft 1.13 or newer with new material names.
     */
    private static boolean atLeast1_13 = true;

    static {
        // MC 1.8
        try {
            Material.valueOf("PRISMARINE");

        } catch (final IllegalArgumentException ex) {
            atLeast1_8 = false;
        }

        // MC 1.9
        try {
            Sound.valueOf("BLOCK_END_GATEWAY_SPAWN").ordinal();

        } catch (final Throwable t) {
            atLeast1_9 = false;
        }

        // MC 1.12
        try {
            EntityType.valueOf("ILLUSIONER");

        } catch (final Throwable t) {
            atLeast1_12 = false;
        }

        // MC 1.13
        try {
            Material.valueOf("TRIDENT");

        } catch (final IllegalArgumentException ex) {
            atLeast1_13 = false;
        }
    }

    public static boolean isAtLeast1_9() {
        return atLeast1_9;
    }

    public static boolean isAtLeast1_8() {
        return atLeast1_8;
    }

    public static boolean isAtLeast1_12() {
        return atLeast1_12;
    }

    public static boolean isAtLeast1_13() {
        return atLeast1_13;
    }
}
