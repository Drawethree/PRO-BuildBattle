package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.utils.ItemCreator;
import me.drawe.buildbattle.utils.ParticleEffect;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.inventory.ItemStack;

public enum BBParticle {

    HEART(ParticleEffect.HEART, "buildbattlepro.particle.heart", ItemCreator.create(CompMaterial.APPLE,1, "&aHeart particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 10),
    HAPPY(ParticleEffect.VILLAGER_HAPPY, "buildbattlepro.particle.happy", ItemCreator.create(CompMaterial.EMERALD,1, "&aHappy particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 11),
    ANGRY(ParticleEffect.VILLAGER_ANGRY, "buildbattlepro.particle.angry", ItemCreator.create(CompMaterial.BLAZE_POWDER,1, "&aAngry particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 12),
    DRIP_LAVA(ParticleEffect.DRIP_LAVA, "buildbattlepro.particle.lava", ItemCreator.create(CompMaterial.LAVA_BUCKET,1, "&aDrip Lava particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 13),
    DRIP_WATER(ParticleEffect.DRIP_WATER, "buildbattlepro.particle.water", ItemCreator.create(CompMaterial.WATER_BUCKET,1, "&aDrip Water particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 14),
    NOTE(ParticleEffect.NOTE, "buildbattlepro.particle.note", ItemCreator.create(CompMaterial.JUKEBOX,1, "&aNote particle", ItemCreator.makeLore("&7Right-Click to place."), null,null), 15),
    GLYPH(ParticleEffect.ENCHANTMENT_TABLE, "buildbattlepro.particle.glyph", ItemCreator.create(CompMaterial.ENCHANTING_TABLE,1, "&aGlyph particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),16),
    SLIME(ParticleEffect.SLIME, "buildbattlepro.particle.slime", ItemCreator.create(CompMaterial.SLIME_BALL,1, "&aSlime particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),19),
    MOB_SPAWNER(ParticleEffect.FLAME, "buildbattlepro.particle.mobspawner", ItemCreator.create(CompMaterial.SPAWNER,1, "&aMobspawner particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),20),
    EXPLOSION(ParticleEffect.EXPLOSION_LARGE, "buildbattlepro.particle.explosion", ItemCreator.create(CompMaterial.TNT,1, "&aExplosion particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),21),
    MAGIC_CRIT(ParticleEffect.CRIT_MAGIC, "buildbattlepro.particle.crit", ItemCreator.create(CompMaterial.DIAMOND_SWORD,1, "&aMagic Crit particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),22),
    WITCH(ParticleEffect.SPELL_WITCH, "buildbattlepro.particle.witch", ItemCreator.create(CompMaterial.GLASS_BOTTLE,1, "&aWitch particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),23),
    SPLASH(ParticleEffect.WATER_SPLASH, "buildbattlepro.particle.splash", ItemCreator.create(CompMaterial.OAK_BOAT,1, "&aSplash particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),24),
    CLOUD(ParticleEffect.CLOUD, "buildbattlepro.particle.cloud", ItemCreator.create(CompMaterial.ARROW,1, "&aCloud particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),25),
    PORTAL(ParticleEffect.PORTAL, "buildbattlepro.particle.portal", ItemCreator.create(CompMaterial.NETHER_BRICK,1, "&aPortal particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),29),
    FIREWORK_SPARK(ParticleEffect.FIREWORKS_SPARK, "buildbattlepro.particle.firework", ItemCreator.create(CompMaterial.FIREWORK_ROCKET,1, "&aFirework Spark particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),30),
    REDSTONE(ParticleEffect.REDSTONE, "buildbattlepro.particle.redstone", ItemCreator.create(CompMaterial.REDSTONE,1, "&aRedstone particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),31),
    SNOWBALL_POOF(ParticleEffect.SNOWBALL, "buildbattlepro.particle.snowball", ItemCreator.create(CompMaterial.SNOWBALL,1, "&aSnowball Poof particle", ItemCreator.makeLore("&7Right-Click to place."), null,null),32);

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

    public String getRequiredPermission() {
        return requiredPermission;
    }
}
