package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompBiome;
import me.drawe.buildbattle.utils.compatbridge.model.XMaterial;
import org.bukkit.inventory.ItemStack;

public enum PlotBiome {

    PLAINS("Plains", CompBiome.PLAINS, ItemUtil.create(XMaterial.GRASS, 1, Message.GUI_BIOMES_PLAINS_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.plains.lore")), null, null)),
    MESA("Mesa", CompBiome.BADLANDS, ItemUtil.create(XMaterial.ORANGE_TERRACOTTA, 1, Message.GUI_BIOMES_MESA_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.mesa.lore")), null, null)),
    OCEAN("Ocean", CompBiome.OCEAN, ItemUtil.create(XMaterial.WATER_BUCKET, 1, Message.GUI_BIOMES_OCEAN_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.ocean.lore")), null, null)),
    DESERT("Desert", CompBiome.DESERT, ItemUtil.create(XMaterial.SAND, 1, Message.GUI_BIOMES_DESERT_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.desert.lore")), null, null)),
    FOREST("Forest", CompBiome.FOREST, ItemUtil.create(XMaterial.OAK_LOG, 1, Message.GUI_BIOMES_FOREST_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.forest.lore")), null, null)),
    JUNGLE("Jungle", CompBiome.JUNGLE, ItemUtil.create(XMaterial.VINE, 1, Message.GUI_BIOMES_JUNGLE_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.jungle.lore")), null, null)),
    SWAMP("Swamp", CompBiome.SWAMP, ItemUtil.create(XMaterial.LILY_PAD, 1, Message.GUI_BIOMES_SWAMP_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.swamp.lore")), null, null)),
    SAVANNA("Savanna", CompBiome.SAVANNA, ItemUtil.create(XMaterial.ACACIA_LOG, 1, Message.GUI_BIOMES_SAVANNA_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.savanna.lore")), null, null)),
    BEACH("Beach", CompBiome.BEACH, ItemUtil.create(XMaterial.SAND, 1, Message.GUI_BIOMES_BEACH_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.beach.lore")), null, null)),
    ICE_SPIKES("Ice Spikes", CompBiome.ICE_SPIKES, ItemUtil.create(XMaterial.PACKED_ICE, 1, Message.GUI_BIOMES_ICE_SPIKES_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.ice_spikes.lore")), null, null)),
    NETHER("Nether", CompBiome.NETHER, ItemUtil.create(XMaterial.NETHER_BRICKS, 1, Message.GUI_BIOMES_NETHER_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.nether.lore")), null, null)),
    THE_END("The End", CompBiome.THE_END, ItemUtil.create(XMaterial.DRAGON_EGG, 1, Message.GUI_BIOMES_THE_END_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.the_end.lore")), null, null)),
    MOUNTAINS("Mountains", CompBiome.MOUNTAINS, ItemUtil.create(XMaterial.STONE, 1, Message.GUI_BIOMES_MOUNTAINS_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.mountains.lore")), null, null)),
    TAIGA("Taiga", CompBiome.TAIGA, ItemUtil.create(XMaterial.SPRUCE_LOG, 1, Message.GUI_BIOMES_TAIGA_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.taiga.lore")), null, null)),
    RIVER("River", CompBiome.RIVER, ItemUtil.create(XMaterial.WATER_BUCKET, 1, Message.GUI_BIOMES_RIVER_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.river.lore")), null, null)),
    MUSHROOM("Mushroom Fields", CompBiome.MUSHROOM_FIELDS, ItemUtil.create(XMaterial.RED_MUSHROOM_BLOCK, 1, Message.GUI_BIOMES_MUSHROOM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.biomes.items.mushroom.lore")), null, null));

    private String name;
    private CompBiome biome;
    private ItemStack item;

    PlotBiome(String name, CompBiome biome, ItemStack itemStack) {
        this.name = name;
        this.item = itemStack;
        this.biome = biome;
    }

    public CompBiome getBiome() {
        return this.biome;
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
