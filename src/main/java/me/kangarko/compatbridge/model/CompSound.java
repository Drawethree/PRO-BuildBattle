package me.kangarko.compatbridge.model;

import me.kangarko.compatbridge.utils.VersionResolver;
import org.bukkit.Sound;

/**
 * Wrapper for *SOME* sound names. You can expand this at your need.
 */
public enum CompSound {

    BLOCK_NOTE_HAT("NOTE_BASS", "BLOCK_NOTE_BLOCK_HAT"),
    BLOCK_NOTE_HARP("NOTE_SNARE_DRUM", "BLOCK_ANVIL_BREAK" /* intended */),
    BLOCK_NOTE_BASS("NOTE_BASS", "BLOCK_NOTE_BLOCK_BASS"),
    BLOCK_ANVIL_FALL("ANVIL_LAND", "BLOCK_ANVIL_FALL"),

    BLOCK_DISPENSER_LAUNCH("ITEM_PICKUP"),
    BLOCK_FIRE_EXTINGUISH("FIRE_IGNITE"),
    BLOCK_GLASS_BREAK("GLASS"),
    BLOCK_CHEST_CLOSE("CHEST_CLOSE"),
    BLOCK_ANVIL_HIT("ANVIL_LAND"),

    ENTITY_ARROW_HIT_PLAYER("SUCCESSFUL_HIT"),
    ENTITY_EXPERIENCE_ORB_PICKUP("ORB_PICKUP"),
    ENTITY_CHICKEN_EGG("CHICKEN_EGG_POP"),

    ENTITY_ENDERDRAGON_FLAP("ENDERDRAGON_WINGS", "ENTITY_ENDER_DRAGON_FLAP"),
    ENTITY_ENDERDRAGON_DEATH("ENDERDRAGON_DEATH", "ENTITY_ENDER_DRAGON_DEATH"),

    ENTITY_ITEMFRAME_BREAK("FIREWORK_BLAST", "ENTITY_ITEM_FRAME_BREAK"),
    ENTITY_ITEM_PICKUP("ITEM_PICKUP"),

    ENTITY_FIREWORK_BLAST("FIREWORK_BLAST", "ENTITY_FIREWORK_ROCKET_BLAST"),
    ENTITY_FIREWORK_LAUNCH("FIREWORK_LAUNCH", "ENTITY_FIREWORK_ROCKET_LAUNCH"),
    ENTITY_FIREWORK_TWINKLE_FAR("FIREWORK_LAUNCH", "ENTITY_FIREWORK_ROCKET_TWINKLE_FAR"),
    ENTITY_FIREWORK_LARGE_BLAST_FAR("FIREWORK_LARGE_BLAST", "ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR"),

    ENTITY_PLAYER_HURT("HURT_FLESH"),
    ENTITY_PLAYER_LEVELUP("LEVEL_UP"),
    ENTITY_PLAYER_BURP("BURP");

    /**
     * The correctly translated sound for the installed MC version.
     */
    private final Sound sound;

    /**
     * The first name is for Minecraft older than 1.9, the second for Minecraft 1.13 and newer.
     */
    private final String legacyName, modernName;

    /**
     * Make a new CompSound with the given legacy name for MC < 1.9.
     *
     * @param legacyName
     */
    private CompSound(String legacyName) {
        this(legacyName, null);
    }

    /**
     * Make a new CompSound with the given legacy name for MC < 1.9 and a different name for MC >= 1.13.
     *
     * @param legacyName
     */
    private CompSound(String legacyName, String modernName) {
        this.legacyName = legacyName;
        this.modernName = modernName;

        sound = parseSound(modernName, name(), legacyName);
    }

    private static Sound parseSound(String v1_13, String v1_9, String legacy) {
        if (VersionResolver.isAtLeast1_13())
            return Sound.valueOf(v1_13 != null ? v1_13 : v1_9);

        if (VersionResolver.isAtLeast1_9())
            return Sound.valueOf(v1_9);

        return Sound.valueOf(legacy);
    }

    /**
     * Parses a sound name into the right sound name for installed MC version.
     *
     * @param original
     * @return
     */
    public static final Sound convert(String original) {

        if (VersionResolver.isAtLeast1_13()) {
            for (final CompSound compSound : CompSound.values())
                if (original.equalsIgnoreCase(compSound.name()) || original.equalsIgnoreCase(compSound.legacyName) || compSound.modernName != null && original.equalsIgnoreCase(compSound.modernName))
                    return Sound.valueOf(compSound.modernName != null ? compSound.modernName : compSound.name());
        }

        if (VersionResolver.isAtLeast1_9()) {
            for (final CompSound s : CompSound.values())
                if (original.equalsIgnoreCase(s.legacyName))
                    return Sound.valueOf(s.toString());
        } else
            for (final CompSound s : CompSound.values())
                if (original.equalsIgnoreCase(s.toString()))
                    try {
                        return Sound.valueOf(s.legacyName);
                    } catch (final IllegalArgumentException ex) {
                        if (!VersionResolver.isAtLeast1_8()) // 1.7.10 and older
                            return Sound.valueOf("LEVEL_UP");

                        throw new RuntimeException(ex);
                    }

        try {
            return Sound.valueOf(original);
        } catch (final IllegalArgumentException ex) {
            if (!VersionResolver.isAtLeast1_8()) // 1.7.10 and older
                return Sound.valueOf("LEVEL_UP");

            throw new RuntimeException(ex);
        }
    }

    public Sound getSound() {
        return sound;
    }
}