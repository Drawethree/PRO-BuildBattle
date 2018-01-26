package me.drawe.buildbattle.particles;

import me.drawe.buildbattle.utils.ItemCreator;
import me.drawe.buildbattle.utils.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum BBParticle {

    HEARTH(ParticleEffect.HEART, ItemCreator.create(Material.APPLE,1,(byte) 0, "&aHearth particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 10),
    HAPPY(ParticleEffect.VILLAGER_HAPPY, ItemCreator.create(Material.EMERALD,1,(byte) 0, "&aHappy particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 11),
    ANGRY(ParticleEffect.VILLAGER_ANGRY, ItemCreator.create(Material.BLAZE_POWDER,1,(byte) 0, "&aAngry particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 12),
    DRIP_LAVA(ParticleEffect.DRIP_LAVA, ItemCreator.create(Material.LAVA_BUCKET,1,(byte) 0, "&aDrip Lava particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 13),
    DRIP_WATER(ParticleEffect.DRIP_WATER, ItemCreator.create(Material.WATER_BUCKET,1,(byte) 0, "&aDrip Water particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 14),
    NOTE(ParticleEffect.NOTE, ItemCreator.create(Material.JUKEBOX,1,(byte) 0, "&aNote particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 15),
    GLYPH(ParticleEffect.ENCHANTMENT_TABLE, ItemCreator.create(Material.ENCHANTMENT_TABLE,1,(byte) 0, "&aGlyph particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),16),
    SLIME(ParticleEffect.SLIME, ItemCreator.create(Material.SLIME_BALL,1,(byte) 0, "&aSlime particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),19),
    MOB_SPAWNER(ParticleEffect.FLAME, ItemCreator.create(Material.MOB_SPAWNER,1,(byte) 0, "&aMobspawner particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),20),
    EXPLOSION(ParticleEffect.EXPLOSION_LARGE, ItemCreator.create(Material.TNT,1,(byte) 0, "&aExplosion particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),21),
    MAGIC_CRIT(ParticleEffect.CRIT_MAGIC, ItemCreator.create(Material.DIAMOND_SWORD,1,(byte) 0, "&aMagic Crit particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),22),
    WITCH(ParticleEffect.SPELL_WITCH, ItemCreator.create(Material.GLASS_BOTTLE,1,(byte) 0, "&aWitch particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),23),
    SPLASH(ParticleEffect.WATER_SPLASH, ItemCreator.create(Material.BOAT,1,(byte) 0, "&aSplash particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),24),
    CLOUD(ParticleEffect.CLOUD, ItemCreator.create(Material.ARROW,1,(byte) 0, "&aCloud particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),25),
    PORTAL(ParticleEffect.PORTAL, ItemCreator.create(Material.NETHER_BRICK_ITEM,1,(byte) 0, "&aPortal particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),29),
    FIREWORK_SPARK(ParticleEffect.FIREWORKS_SPARK, ItemCreator.create(Material.FIREWORK,1,(byte) 0, "&aFirework Spark particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),30),
    REDSTONE(ParticleEffect.REDSTONE, ItemCreator.create(Material.REDSTONE,1,(byte) 0, "&aRedstone particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),31),
    SNOWBALL_POOF(ParticleEffect.SNOWBALL, ItemCreator.create(Material.SNOW_BALL,1,(byte) 0, "&aSnowball Poof particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),32);

    private final ParticleEffect effect;
    private final ItemStack itemStack;
    private final int slot;

    BBParticle(ParticleEffect effect, ItemStack itemStack, int slot) {
        this.effect = effect;
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
        for(BBParticle p : values()) {
            if(p.getItemStack().isSimilar(item)) {
                return p;
            }
        }
        return null;
    }

    public int getSlot() {
        return slot;
    }
}
