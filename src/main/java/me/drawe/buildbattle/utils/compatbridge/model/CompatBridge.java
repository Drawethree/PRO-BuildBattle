package me.drawe.buildbattle.utils.compatbridge.model;

import me.drawe.buildbattle.utils.compatbridge.VersionResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;

/**
 * A mostly reflection-based class for version-specific various API methods.
 *
 * Methods work for 1.12 + 1.13.
 */
public final class CompatBridge {

    /**
     * Sets a data of a block in the world.
     *
     * @param block
     * @param data
     */
    public static void setData(Block block, int data) {
        try {
            Block.class.getMethod("setData", byte.class).invoke(block, (byte) data);
        } catch (final NoSuchMethodException ex) {
            block.setBlockData(Bukkit.getUnsafe().fromLegacy(block.getType(), (byte) data), true);

        } catch (final ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Spawns a falling block.
     *
     * @param w
     * @param l
     * @param mat
     * @param data
     * @return
     */
    public static FallingBlock spawnFallingBlock(World w, Location l, Material mat, byte data) {
        if (VersionResolver.isAtLeast1_13())
            return w.spawnFallingBlock(l, Bukkit.getUnsafe().fromLegacy(mat, data));
        else {
            try {
                return (FallingBlock) w.getClass().getMethod("spawnFallingBlock", Location.class, int.class, byte.class).invoke(w, l, mat.getId(), data);
            } catch (final ReflectiveOperationException ex) {
                ex.printStackTrace();

                return null;
            }
        }
    }

    /**
     * Sets a block type and its data, applying physics.
     *
     * @param block
     * @param mat
     * @param data
     */
    public static void setTypeAndData(Block block, CompMaterial mat, byte data) {
        setTypeAndData(block, mat.getMaterial(), data);
    }

    /**
     * Sets a block type and its data, applying physics.
     *
     * @param block
     * @param mat
     * @param data
     */
    public static void setTypeAndData(Block block, Material mat, byte data) {
        setTypeAndData(block, mat, data, true);
    }

    /**
     * Sets a block type and its data.
     *
     * @param block
     * @param mat
     * @param data
     */
    public static void setTypeAndData(Block block, Material mat, byte data, boolean physics) {
        if (VersionResolver.isAtLeast1_13()) {
            block.setType(mat);
            block.setBlockData( Bukkit.getUnsafe().fromLegacy(mat, data), physics );

        } else {
            try {
                block.getClass().getMethod("setTypeIdAndData", int.class, byte.class, boolean.class).invoke(block, mat.getId(), data, physics);
            } catch (final ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }
    }
}