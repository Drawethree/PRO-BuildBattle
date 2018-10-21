package me.kangarko.compatbridge.model;

import org.bukkit.block.Biome;

public enum CompBiome {

    BADLANDS("MESA"),
    BADLANDS_PLATEAU("MESA_PLATEAU"),
    BEACH("BEACH"),
    BIRCH_FOREST("BIRCH_FOREST"),
    BIRCH_FOREST_HILLS("BIRCH_FOREST_HILLS"),
    COLD_OCEAN,
    DARK_FOREST,
    DARK_FOREST_HILLS,
    DEEP_COLD_OCEAN,
    DEEP_FROZEN_OCEAN,
    DEEP_LUKEWARM_OCEAN,
    DEEP_OCEAN("DEEP_OCEAN"),
    DEEP_WARM_OCEAN,
    DESERT("DESERT"),
    DESERT_HILLS("DESERT_HILLS"),
    DESERT_LAKES,
    END_BARRENS,
    END_HIGHLANDS,
    END_MIDLANDS,
    ERODED_BADLANDS,
    FLOWER_FOREST("FLOWER_FOREST"),
    FOREST("FOREST"),
    FROZEN_OCEAN("FROZEN_OCEAN"),
    FROZEN_RIVER("FROZEN_RIVER"),
    GIANT_SPRUCE_TAIGA("MEGA_SPRUCE_TAIGA"),
    GIANT_SPRUCE_TAIGA_HILLS("MEGA_SPRUCE_TAIGA_HILLS"),
    GIANT_TREE_TAIGA,
    GIANT_TREE_TAIGA_HILLS,
    GRAVELLY_MOUNTAINS,
    ICE_SPIKES("ICE_PLAINS_SPIKES"),
    JUNGLE("JUNGLE"),
    JUNGLE_EDGE("JUNGLE_EDGE"),
    JUNGLE_HILLS("JUNGLE_HILLS"),
    LUKEWARM_OCEAN(),
    MODIFIED_BADLANDS_PLATEAU,
    MODIFIED_GRAVELLY_MOUNTAINS,
    MODIFIED_JUNGLE,
    MODIFIED_JUNGLE_EDGE,
    MODIFIED_WOODED_BADLANDS_PLATEAU,
    MOUNTAIN_EDGE,
    MOUNTAINS("SMALL_MOUNTAINS"),
    MUSHROOM_FIELD_SHORE,
    MUSHROOM_FIELDS("MUSHROOM_SHORE"),
    NETHER("HELL"),
    OCEAN("OCEAN"),
    PLAINS("PLAINS"),
    RIVER("RIVER"),
    SAVANNA("SAVANNA"),
    SAVANNA_PLATEAU("SAVANNA_PLATEAU"),
    SHATTERED_SAVANNA,
    SHATTERED_SAVANNA_PLATEAU,
    SMALL_END_ISLANDS,
    SNOWY_BEACH,
    SNOWY_MOUNTAINS,
    SNOWY_TAIGA,
    SNOWY_TAIGA_HILLS,
    SNOWY_TAIGA_MOUNTAINS,
    SNOWY_TUNDRA,
    STONE_SHORE,
    SUNFLOWER_PLAINS,
    SWAMP("SWAMPLAND"),
    SWAMP_HILLS("SWAMPLAND_MOUNTAINS"),
    TAIGA("TAIGA"),
    TAIGA_HILLS("TAIGA_HILLS"),
    TAIGA_MOUNTAINS("TAIGA_MOUNTAINS"),
    TALL_BIRCH_FOREST,
    TALL_BIRCH_HILLS,
    THE_END,
    THE_VOID,
    WARM_OCEAN,
    WOODED_BADLANDS_PLATEAU,
    WOODED_HILLS,
    WOODED_MOUNTAINS;

    private final String legacyName;
    private final Biome biome;

    private CompBiome() {
        this(null);
    }

    private CompBiome(String name) {
        this.legacyName = name;

        Biome biome;

        try {
            biome = Biome.valueOf(name());

        } catch (final IllegalArgumentException ex) {
            if (name == null) throw new RuntimeException("Missing legacy name for Biome." + name());

            biome = Biome.valueOf(name);
        }

        if (biome == null) throw new RuntimeException("Failed to resolve Biome." + name());
        this.biome = biome;
    }

    public Biome getBiome() {
        return biome;
    }


    public static final CompBiome fromName(String name) {
        for (final CompBiome b : values())
            if (b.toString().equalsIgnoreCase(name) || b.legacyName.equalsIgnoreCase(name))
                return b;

        throw new RuntimeException("Could not get CompBiome from name: " + name);
    }

}
