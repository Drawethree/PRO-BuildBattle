package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.utils.ItemCreator;
import me.drawe.buildbattle.utils.ParticleEffect;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.inventory.ItemStack;

public enum BBParticle {

    HEART(ParticleEffect.HEART, "buildbattlepro.particle.heart", ItemCreator.create(CompMaterial.APPLE, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.heart.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.heart.lore")), null, null), 10),
    HAPPY(ParticleEffect.VILLAGER_HAPPY, "buildbattlepro.particle.happy", ItemCreator.create(CompMaterial.EMERALD, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.happy.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.happy.lore")), null, null), 11),
    ANGRY(ParticleEffect.VILLAGER_ANGRY, "buildbattlepro.particle.angry", ItemCreator.create(CompMaterial.BLAZE_POWDER, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.angry.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.angry.lore")), null, null), 12),
    DRIP_LAVA(ParticleEffect.DRIP_LAVA, "buildbattlepro.particle.lava", ItemCreator.create(CompMaterial.LAVA_BUCKET, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.drip_lava.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.drip_lava.lore")), null, null), 13),
    DRIP_WATER(ParticleEffect.DRIP_WATER, "buildbattlepro.particle.water", ItemCreator.create(CompMaterial.WATER_BUCKET, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.drip_water.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.drip_water.lore")), null, null), 14),
    NOTE(ParticleEffect.NOTE, "buildbattlepro.particle.note", ItemCreator.create(CompMaterial.JUKEBOX, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.note.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.note.lore")), null, null), 15),
    GLYPH(ParticleEffect.ENCHANTMENT_TABLE, "buildbattlepro.particle.glyph", ItemCreator.create(CompMaterial.ENCHANTING_TABLE, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.glyph.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.glyph.lore")), null, null), 16),
    SLIME(ParticleEffect.SLIME, "buildbattlepro.particle.slime", ItemCreator.create(CompMaterial.SLIME_BALL, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.slime.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.slime.lore")), null, null), 19),
    MOB_SPAWNER(ParticleEffect.FLAME, "buildbattlepro.particle.mobspawner", ItemCreator.create(CompMaterial.SPAWNER, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.mob_spawner.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.mob_spawner.lore")), null, null), 20),
    EXPLOSION(ParticleEffect.EXPLOSION_LARGE, "buildbattlepro.particle.explosion", ItemCreator.create(CompMaterial.TNT, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.explosion.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.explosion.lore")), null, null), 21),
    MAGIC_CRIT(ParticleEffect.CRIT_MAGIC, "buildbattlepro.particle.crit", ItemCreator.create(CompMaterial.DIAMOND_SWORD, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.magic_crit.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.magic_crit.lore")), null, null), 22),
    WITCH(ParticleEffect.SPELL_WITCH, "buildbattlepro.particle.witch", ItemCreator.create(CompMaterial.GLASS_BOTTLE, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.witch.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.witch.lore")), null, null), 23),
    SPLASH(ParticleEffect.WATER_SPLASH, "buildbattlepro.particle.splash", ItemCreator.create(CompMaterial.OAK_BOAT, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.splash.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.splash.lore")), null, null), 24),
    CLOUD(ParticleEffect.CLOUD, "buildbattlepro.particle.cloud", ItemCreator.create(CompMaterial.ARROW, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.cloud.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.cloud.lore")), null, null), 25),
    PORTAL(ParticleEffect.PORTAL, "buildbattlepro.particle.portal", ItemCreator.create(CompMaterial.NETHER_BRICK, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.portal.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.portal.lore")), null, null), 29),
    FIREWORK_SPARK(ParticleEffect.FIREWORKS_SPARK, "buildbattlepro.particle.firework", ItemCreator.create(CompMaterial.FIREWORK_ROCKET, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.firework_spark.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.firework_spark.lore")), null, null), 30),
    REDSTONE(ParticleEffect.REDSTONE, "buildbattlepro.particle.redstone", ItemCreator.create(CompMaterial.REDSTONE, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.redstone.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.redstone.lore")), null, null), 31),
    SNOWBALL_POOF(ParticleEffect.SNOWBALL, "buildbattlepro.particle.snowball", ItemCreator.create(CompMaterial.SNOWBALL, 1, BuildBattle.getFileManager().getConfig("messages.yml").get().getString("particles.snowball_poof.displayname"), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("particles.snowball_poof.lore")), null, null), 32);

    private final ParticleEffect effect;
    private final ItemStack itemStack;
    private final int slot;
    private String requiredPermission;

    BBParticle(ParticleEffect effect, String requiredPermission, ItemStack itemStack, int slot) {
        this.effect = effect;
        this.requiredPermission = requiredPermission;
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static BBParticle getBBParticle(ItemStack item) {
        for (BBParticle p : values()) {
            if (p.getItemStack().isSimilar(item)) {
                return p;
            }
        }
        return null;
    }

    public int getSlot() {
        return slot;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }
}
