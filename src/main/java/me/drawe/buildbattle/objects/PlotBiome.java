package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.utils.ItemCreator;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

public enum PlotBiome {

    PLAINS("Plains", Biome.PLAINS, ItemCreator.create(Material.GRASS, 1, (byte) 0, Message.GUI_BIOMES_PLAINS_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.plains.lore")), null, null), 0),
    MESA("Mesa", Biome.BADLANDS, ItemCreator.create(Material.LEGACY_STAINED_CLAY, 1, (byte) 1, Message.GUI_BIOMES_MESA_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.mesa.lore")), null, null), 1),
    OCEAN("Ocean", Biome.OCEAN, ItemCreator.create(Material.WATER_BUCKET, 1, (byte) 0, Message.GUI_BIOMES_OCEAN_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.ocean.lore")), null, null), 2),
    DESERT("Desert", Biome.DESERT, ItemCreator.create(Material.SAND, 1, (byte) 0, Message.GUI_BIOMES_DESERT_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.desert.lore")), null, null), 3),
    FOREST("Forest", Biome.FOREST, ItemCreator.create(Material.LEGACY_LOG, 1, (byte) 0, Message.GUI_BIOMES_FOREST_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.forest.lore")), null, null), 4),
    JUNGLE("Jungle", Biome.JUNGLE, ItemCreator.create(Material.VINE, 1, (byte) 0, Message.GUI_BIOMES_JUNGLE_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.jungle.lore")), null, null), 5),
    SWAMP("Swamp", Biome.SWAMP, ItemCreator.create(Material.LEGACY_WATER_LILY, 1, (byte) 0, Message.GUI_BIOMES_SWAMP_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.swamp.lore")), null, null), 6),
    SAVANNA("Savanna", Biome.SAVANNA, ItemCreator.create(Material.LEGACY_LOG_2, 1, (byte) 0, Message.GUI_BIOMES_SAVANNA_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.biomes.items.savanna.lore")), null, null), 7);

    private String name;
    private Biome biome;
    private int slot;
    private ItemStack item;

    PlotBiome(String name, Biome biome, ItemStack itemStack, int slot) {
        this.name = name;
        this.slot = slot;
        this.item = itemStack;
        this.biome = biome;
    }

    public Biome getBiome() {
        return this.biome;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public static PlotBiome getBiomeFromItemStack(ItemStack item, int slot) {
        for (PlotBiome biome : values()) {
            if ((biome.getItem().isSimilar(item)) && (biome.getSlot() == slot)) {
                return biome;
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }
}
