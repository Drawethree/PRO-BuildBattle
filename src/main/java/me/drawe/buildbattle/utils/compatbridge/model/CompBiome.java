package me.drawe.buildbattle.utils.compatbridge.model;

import me.drawe.buildbattle.utils.compatbridge.MinecraftVersion;
import org.bukkit.block.Biome;

public enum CompBiome {

    BADLANDS("MESA", "MESA", "MESA", "BADLANDS"),
    BADLANDS_PLATEAU("MESA_PLATEAU", "MESA_ROCK", "MESA_ROCK", "BADLANDS_PLATEAU"),
    BEACH("BEACH", "BEACHES", "BEACHES", "BEACH"),
    BIRCH_FOREST("BIRCH_FOREST", "BIRCH_FOREST", "BIRCH_FOREST", "BIRCH_FOREST"),
    BIRCH_FOREST_HILLS("BIRCH_FOREST_HILLS", "BIRCH_FOREST_HILLS", "BIRCH_FOREST_HILLS", "BIRCH_FOREST_HILLS"),
    COLD_OCEAN(null, null, null, "COLD_OCEAN"),
    DARK_FOREST(null, "MUTATED_FOREST", "MUTATED_FOREST", "DARK_FOREST"),
    DARK_FOREST_HILLS(null, "MUTATED_ROOFED_FOREST", "MUTATED_ROOFED_FOREST", "DARK_FOREST_HILLS"),
    DEEP_COLD_OCEAN(null, null, null, "DEEP_COLD_OCEAN"),
    DEEP_FROZEN_OCEAN(null, null, null, "DEEP_FROZEN_OCEAN"),
    DEEP_LUKEWARM_OCEAN(null, null, null, "DEEP_LUKEWARM_OCEAN"),
    DEEP_OCEAN("DEEP_OCEAN", "DEEP_OCEAN", "DEEP_OCEAN", "DEEP_OCEAN"),
    DEEP_WARM_OCEAN(null, null, null, "DEEP_WARM_OCEAN"),
    DESERT("DESERT", "DESERT", "DESERT", "DESERT"),
    DESERT_HILLS("DESERT_HILLS", "DESERT_HILLS", "DESERT_HILLS", "DESERT_HILLS"),
    DESERT_LAKES(null, null, null, "DESERT_LAKES"),
    END_BARRENS(null, null, null, "END_BARRENS"),
    END_HIGHLANDS(null, null, null, "END_HIGHLANDS"),
    END_MIDLANDS(null, null, null, "END_MIDLANDS"),
    ERODED_BADLANDS(null, null, null, "ERODED_BADLANDS"),
    FLOWER_FOREST("FLOWER_FOREST", null, null, "FLOWER_FOREST"),
    FOREST("FOREST", "FOREST", "FOREST", "FOREST"),
    FROZEN_OCEAN("FROZEN_OCEAN", "FROZEN_OCEAN", "FROZEN_OCEAN", "FROZEN_OCEAN"),
    FROZEN_RIVER("FROZEN_RIVER", "FROZEN_RIVER", "FROZEN_RIVER", "FROZEN_RIVER"),
    GIANT_SPRUCE_TAIGA("MEGA_SPRUCE_TAIGA", "REDWOOD_TAIGA", "REDWOOD_TAIGA", "GIANT_SPRUCE_TAIGA"),
    GIANT_SPRUCE_TAIGA_HILLS("MEGA_SPRUCE_TAIGA_HILLS", "REDWOOD_TAIGA_HILLS", "REDWOOD_TAIGA_HILLS", "GIANT_SPRUCE_TAIGA_HILLS"),
    GIANT_TREE_TAIGA(null, null, null, "GIANT_TREE_TAIGA"),
    GIANT_TREE_TAIGA_HILLS(null, null, null, "GIANT_TREE_TAIGA_HILLS"),
    GRAVELLY_MOUNTAINS(null, null, null, "GRAVELLY_MOUNTAINS"),
    ICE_SPIKES("ICE_PLAINS_SPIKES", null, null, "ICE_SPIKES"),
    JUNGLE("JUNGLE", "JUNGLE", "JUNGLE", "JUNGLE"),
    JUNGLE_EDGE("JUNGLE_EDGE", "JUNGLE_EDGE", "JUNGLE_EDGE", "JUNGLE_EDGE"),
    JUNGLE_HILLS("JUNGLE_HILLS", "JUNGLE_HILLS", "JUNGLE_HILLS", "JUNGLE_HILLS"),
    LUKEWARM_OCEAN(null, null, null, "LUKEWARM_OCEAN"),
    MODIFIED_BADLANDS_PLATEAU(null, null, null, "MODIFIED_BADLANDS_PLATEAU"),
    MODIFIED_GRAVELLY_MOUNTAINS(null, null, null, "MODIFIED_GRAVELLY_MOUNTAINS"),
    MODIFIED_JUNGLE(null, null, null, "MODIFIED_JUNGLE"),
    MODIFIED_JUNGLE_EDGE(null, null, null, "MODIFIED_JUNGLE_EDGE"),
    MODIFIED_WOODED_BADLANDS_PLATEAU(null, null, null, "MODIFIED_WOODED_BADLANDS_PLATEAU"),
    MOUNTAIN_EDGE(null, null, null, "MOUNTAIN_EDGE"),
    MOUNTAINS("SMALL_MOUNTAINS", null, null, "MOUNTAINS"),
    MUSHROOM_FIELD_SHORE(null, "MUSHROOM_ISLAND", "MUSHROOM_ISLAND", "MUSHROOM_FIELD_SHORE"),
    MUSHROOM_FIELDS("MUSHROOM_SHORE", "MUSHROOM_ISLAND_SHORE", "MUSHROOM_ISLAND_SHORE", "MUSHROOM_FIELDS"),
    NETHER("HELL", "HELL", "HELL", "NETHER"),
    OCEAN("OCEAN", "OCEAN", "OCEAN", "OCEAN"),
    PLAINS("PLAINS", "PLAINS", "PLAINS", "PLAINS"),
    RIVER("RIVER", "RIVER", "RIVER", "RIVER"),
    SAVANNA("SAVANNA", "SAVANNA", "SAVANNA", "SAVANNA"),
    SAVANNA_PLATEAU("SAVANNA_PLATEAU", "SAVANNA_ROCK", "SAVANNA_ROCK", "SAVANNA_PLATEAU"),
    SHATTERED_SAVANNA(null, null, null, "SHATTERED_SAVANNA"),
    SHATTERED_SAVANNA_PLATEAU(null, null, null, "SHATTERED_SAVANNA_PLATEAU"),
    SMALL_END_ISLANDS(null, null, null, "SMALL_END_ISLANDS"),
    SNOWY_BEACH(null, null, null, "SNOWY_BEACH"),
    SNOWY_MOUNTAINS(null, null, null, "SNOWY_MOUNTAINS"),
    SNOWY_TAIGA(null, "TAIGA_COLD", "TAIGA_COLD", "SNOWY_TAIGA"),
    SNOWY_TAIGA_HILLS(null, "TAIGA_COLD_HILLS", "TAIGA_COLD_HILLS", "SNOWY_TAIGA_HILLS"),
    SNOWY_TAIGA_MOUNTAINS(null, null, null, "SNOWY_TAIGA_MOUNTAINS"),
    SNOWY_TUNDRA(null, null, null, "SNOWY_TUNDRA"),
    STONE_SHORE(null, "STONE_BEACH", "STONE_BEACH", "STONE_SHORE"),
    SUNFLOWER_PLAINS(null, null, null, "SUNFLOWER_PLAINS"),
    SWAMP("SWAMPLAND", "SWAMPLAND", "SWAMPLAND", "SWAMP"),
    SWAMP_HILLS("SWAMPLAND_MOUNTAINS", null, null, "SWAMP_HILLS"),
    TAIGA("TAIGA", "TAIGA", "TAIGA", "TAIGA"),
    TAIGA_HILLS("TAIGA_HILLS", "TAIGA_HILLS", "TAIGA_HILLS", "TAIGA_HILLS"),
    TAIGA_MOUNTAINS("TAIGA_MOUNTAINS", "TAIGA_HILLS", "TAIGA_HILLS", "TAIGA_MOUNTAINS"),
    TALL_BIRCH_FOREST(null, null, null, "TALL_BIRCH_FOREST"),
    TALL_BIRCH_HILLS(null, null, null, "TALL_BIRCH_HILLS"),
    THE_END(null, null, null, "THE_END"),
    THE_VOID(null, "VOID", "VOID", "THE_VOID"),
    WARM_OCEAN(null, null, null, "WARM_OCEAN"),
    WOODED_BADLANDS_PLATEAU(null, null, null, "WOODED_BADLANDS_PLATEAU"),
    WOODED_HILLS(null, null, null, "WOODED_HILLS"),
    WOODED_MOUNTAINS(null, null, null, "WOODED_MOUNTAINS");

    private final String v1_8_8, v1_9, v1_12, v1_13;
    private final Biome biome;

    CompBiome(String v1_8_8, String v1_9, String v1_12, String v1_13) {
        this.v1_8_8 = v1_8_8;
        this.v1_9 = v1_9;
        this.v1_12 = v1_12;
        this.v1_13 = v1_13;
        this.biome = parseBiome(this);
    }

    private static Biome parseBiome(CompBiome b) {
        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_13)) {
            return Biome.valueOf(b.v1_13);
        } else if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_12) && b.v1_12 != null) {
            return Biome.valueOf(b.v1_12);
        } else if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_9) && b.v1_9 != null) {
            return Biome.valueOf(b.v1_9);
        } else {
            if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_8) && b.v1_8_8 != null) {
                try {
                    return Biome.valueOf(b.v1_8_8);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }

    public Biome getBiome() {
        return biome;
    }
}
