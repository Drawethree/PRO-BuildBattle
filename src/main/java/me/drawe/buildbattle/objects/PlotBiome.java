package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.utils.ItemCreator;
import me.kangarko.compatbridge.model.CompBiome;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.inventory.ItemStack;

public enum PlotBiome {

    PLAINS("Plains", CompBiome.PLAINS, ItemCreator.create(CompMaterial.GRASS, 1, Message.GUI_BIOMES_PLAINS_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.plains.lore")), null, null)),
    MESA("Mesa", CompBiome.BADLANDS, ItemCreator.create(CompMaterial.ORANGE_TERRACOTTA, 1, Message.GUI_BIOMES_MESA_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.mesa.lore")), null, null)),
    OCEAN("Ocean", CompBiome.OCEAN, ItemCreator.create(CompMaterial.WATER_BUCKET, 1, Message.GUI_BIOMES_OCEAN_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.ocean.lore")), null, null)),
    DESERT("Desert", CompBiome.DESERT, ItemCreator.create(CompMaterial.SAND, 1, Message.GUI_BIOMES_DESERT_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.desert.lore")), null, null)),
    FOREST("Forest", CompBiome.FOREST, ItemCreator.create(CompMaterial.OAK_LOG, 1, Message.GUI_BIOMES_FOREST_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.forest.lore")), null, null)),
    JUNGLE("Jungle", CompBiome.JUNGLE, ItemCreator.create(CompMaterial.VINE, 1, Message.GUI_BIOMES_JUNGLE_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.jungle.lore")), null, null)),
    SWAMP("Swamp", CompBiome.SWAMP, ItemCreator.create(CompMaterial.LILY_PAD, 1, Message.GUI_BIOMES_SWAMP_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.swamp.lore")), null, null)),
    SAVANNA("Savanna", CompBiome.SAVANNA, ItemCreator.create(CompMaterial.ACACIA_LOG, 1, Message.GUI_BIOMES_SAVANNA_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.savanna.lore")), null, null)),
    BEACH("Beach", CompBiome.BEACH, ItemCreator.create(CompMaterial.SAND, 1, Message.GUI_BIOMES_BEACH_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.beach.lore")), null, null)),
    ICE_SPIKES("Ice Spikes", CompBiome.ICE_SPIKES, ItemCreator.create(CompMaterial.PACKED_ICE, 1, Message.GUI_BIOMES_ICE_SPIKES_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.ice_spikes.lore")), null, null)),
    NETHER("Nether", CompBiome.NETHER, ItemCreator.create(CompMaterial.NETHER_BRICKS, 1, Message.GUI_BIOMES_NETHER_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.nether.lore")), null, null)),
    THE_END("The End", CompBiome.THE_END, ItemCreator.create(CompMaterial.DRAGON_EGG, 1, Message.GUI_BIOMES_THE_END_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.the_end.lore")), null, null)),
    MOUNTAINS("Mountains", CompBiome.MOUNTAINS, ItemCreator.create(CompMaterial.STONE, 1, Message.GUI_BIOMES_MOUNTAINS_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.mountains.lore")), null, null)),
    TAIGA("Taiga", CompBiome.TAIGA, ItemCreator.create(CompMaterial.SPRUCE_LOG, 1, Message.GUI_BIOMES_TAIGA_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.taiga.lore")), null, null)),
    RIVER("River", CompBiome.RIVER, ItemCreator.create(CompMaterial.WATER_BUCKET, 1, Message.GUI_BIOMES_RIVER_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.river.lore")), null, null)),
    MUSHROOM("Mushroom Fields", CompBiome.MUSHROOM_FIELDS, ItemCreator.create(CompMaterial.RED_MUSHROOM, 1, Message.GUI_BIOMES_MUSHROOM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.mushroom.lore")), null, null));

    private String name;
    private CompBiome biome;
    private int slot;
    private ItemStack item;

    PlotBiome(String name, CompBiome biome, ItemStack itemStack) {
        this.name = name;
        this.slot = slot;
        this.item = itemStack;
        this.biome = biome;
    }

    public CompBiome getBiome() {
        return this.biome;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public static PlotBiome getBiomeFromItemStack(ItemStack item) {
        for (PlotBiome biome : values()) {
            if ((biome.getItem().isSimilar(item))) {
                return biome;
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }
}
