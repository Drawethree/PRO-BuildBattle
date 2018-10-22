package me.kangarko.compatbridge.model;

import me.kangarko.compatbridge.utils.VersionResolver;
import org.bukkit.block.Biome;

public enum CompBiome {

    BADLANDS("MESA", "BADLANDS"),
    BADLANDS_PLATEAU("MESA_PLATEAU", "BADLANDS_PLATEAU"),
    BEACH("BEACH", "BEACH"),
    BIRCH_FOREST("BIRCH_FOREST", "BIRCH_FOREST"),
    BIRCH_FOREST_HILLS("BIRCH_FOREST_HILLS", "BIRCH_FOREST_HILLS"),
    COLD_OCEAN(null,"COLD_OCEAN"),
    DARK_FOREST(null, "DARK_FOREST"),
    DARK_FOREST_HILLS(null,"DARK_FOREST_HILLS"),
    DEEP_COLD_OCEAN(null,"DEEP_COLD_OCEAN"),
    DEEP_FROZEN_OCEAN(null, "DEEP_FROZEN_OCEAN"),
    DEEP_LUKEWARM_OCEAN(null, "DEEP_LUKEWARM_OCEAN"),
    DEEP_OCEAN("DEEP_OCEAN", "DEEP_OCEAN"),
    DEEP_WARM_OCEAN(null, "DEEP_WARM_OCEAN"),
    DESERT("DESERT", "DESERT"),
    DESERT_HILLS("DESERT_HILLS", "DESERT_HILLS"),
    DESERT_LAKES(null, "DESERT_LAKES"),
    END_BARRENS(null, "END_BARRENS"),
    END_HIGHLANDS(null, "END_HIGHLANDS"),
    END_MIDLANDS(null, "END_MIDLANDS"),
    ERODED_BADLANDS(null, "ERODED_BADLANDS"),
    FLOWER_FOREST("FLOWER_FOREST", "FLOWER_FOREST"),
    FOREST("FOREST","FOREST"),
    FROZEN_OCEAN("FROZEN_OCEAN", "FROZEN_OCEAN"),
    FROZEN_RIVER("FROZEN_RIVER", "FROZEN_RIVER"),
    GIANT_SPRUCE_TAIGA("MEGA_SPRUCE_TAIGA", "GIANT_SPRUCE_TAIGA"),
    GIANT_SPRUCE_TAIGA_HILLS("MEGA_SPRUCE_TAIGA_HILLS", "GIANT_SPRUCE_TAIGA_HILLS"),
    GIANT_TREE_TAIGA(null, "GIANT_TREE_TAIGA"),
    GIANT_TREE_TAIGA_HILLS(null, "GIANT_TREE_TAIGA_HILLS"),
    GRAVELLY_MOUNTAINS(null, "GRAVELLY_MOUNTAINS"),
    ICE_SPIKES("ICE_PLAINS_SPIKES", "ICE_SPIKES"),
    JUNGLE("JUNGLE", "JUNGLE"),
    JUNGLE_EDGE("JUNGLE_EDGE", "JUNGLE_EDGE"),
    JUNGLE_HILLS("JUNGLE_HILLS", "JUNGLE_HILLS"),
    LUKEWARM_OCEAN(null, "LUKEWARM_OCEAN"),
    MODIFIED_BADLANDS_PLATEAU(null, "MODIFIED_BADLANDS_PLATEAU"),
    MODIFIED_GRAVELLY_MOUNTAINS(null, "MODIFIED_GRAVELLY_MOUNTAINS"),
    MODIFIED_JUNGLE(null, "MODIFIED_JUNGLE"),
    MODIFIED_JUNGLE_EDGE(null, "MODIFIED_JUNGLE_EDGE"),
    MODIFIED_WOODED_BADLANDS_PLATEAU(null, "MODIFIED_WOODED_BADLANDS_PLATEAU"),
    MOUNTAIN_EDGE(null, "MOUNTAIN_EDGE"),
    MOUNTAINS("SMALL_MOUNTAINS", "MOUNTAINS"),
    MUSHROOM_FIELD_SHORE(null, "MUSHROOM_FIELD_SHORE"),
    MUSHROOM_FIELDS("MUSHROOM_SHORE", "MUSHROOM_FIELDS"),
    NETHER("HELL", "NETHER"),
    OCEAN("OCEAN", "OCEAN"),
    PLAINS("PLAINS", "PLAINS"),
    RIVER("RIVER", "RIVER"),
    SAVANNA("SAVANNA", "SAVANNA"),
    SAVANNA_PLATEAU("SAVANNA_PLATEAU", "SAVANNA_PLATEAU"),
    SHATTERED_SAVANNA(null, "SHATTERED_SAVANNA"),
    SHATTERED_SAVANNA_PLATEAU(null, "SHATTERED_SAVANNA_PLATEAU"),
    SMALL_END_ISLANDS(null, "SMALL_END_ISLANDS"),
    SNOWY_BEACH(null, "SNOWY_BEACH"),
    SNOWY_MOUNTAINS(null, "SNOWY_MOUNTAINS"),
    SNOWY_TAIGA(null, "SNOWY_TAIGA"),
    SNOWY_TAIGA_HILLS(null, "SNOWY_TAIGA_HILLS"),
    SNOWY_TAIGA_MOUNTAINS(null, "SNOWY_TAIGA_MOUNTAINS"),
    SNOWY_TUNDRA(null, "SNOWY_TUNDRA"),
    STONE_SHORE(null, "STONE_SHORE"),
    SUNFLOWER_PLAINS(null, "SUNFLOWER_PLAINS"),
    SWAMP("SWAMPLAND", "SWAMP"),
    SWAMP_HILLS("SWAMPLAND_MOUNTAINS", "SWAMP_HILLS"),
    TAIGA("TAIGA", "TAIGA"),
    TAIGA_HILLS("TAIGA_HILLS", "TAIGA_HILLS"),
    TAIGA_MOUNTAINS("TAIGA_MOUNTAINS", "TAIGA_MOUNTAINS"),
    TALL_BIRCH_FOREST(null, "TALL_BIRCH_FOREST"),
    TALL_BIRCH_HILLS(null , "TALL_BIRCH_HILLS"),
    THE_END(null, "THE_END"),
    THE_VOID(null, "THE_VOID"),
    WARM_OCEAN(null, "WARM_OCEAN"),
    WOODED_BADLANDS_PLATEAU(null, "WOODED_BADLANDS_PLATEAU"),
    WOODED_HILLS(null, "WOODED_HILLS"),
    WOODED_MOUNTAINS(null, "WOODED_MOUNTAINS");

    private final String legacyName, modernName;
    private final Biome biome;

    CompBiome(String legacyName, String modernName) {
        this.legacyName = legacyName;
        this.modernName = modernName;
        this.biome = parseBiome(name(), legacyName);
    }

    private static Biome parseBiome(String modern , String legacy) {
        //If we are running 1.13, then its definitely modern biome
        if (VersionResolver.isAtLeast1_13()) {
            return Biome.valueOf(modern);
        }
        //If legacy name exists, lets use it
        if(legacy != null) {
            return Biome.valueOf(legacy);
        }
        //If not, then name for this version doesn't exist's (biome is new)
        return null;
    }

    public Biome getBiome() {
        return biome;
    }
}
