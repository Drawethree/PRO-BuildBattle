package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.utils.LocationUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import me.drawe.buildbattle.utils.compatbridge.model.CompatBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class BBSign {

    private BBArena arena;
    private Sign sign;
    private Block blockBehind;

    public BBSign(BBArena arena, Location loc) {
        this.arena = arena;
        try {
            this.sign = (Sign) loc.getBlock().getState();
            this.blockBehind = LocationUtil.getAttachedBlock(sign.getBlock());
            this.blockBehind.setType(CompMaterial.WHITE_TERRACOTTA.getMaterial());
            this.update();
        } catch (Exception e) {
            BuildBattle.warning("§cThere is no sign for arena §e" + arena.getName() + "§c in location §e" + LocationUtil.getStringFromLocationXYZ(loc) + " §c!");
            BuildBattle.getFileManager().getConfig("signs.yml").get().set(arena.getName() + "." + LocationUtil.getStringFromLocationXYZ(loc), null);
            BuildBattle.getFileManager().getConfig("signs.yml").save();
        }
    }

    public BBArena getArena() {
        return arena;
    }

    public Location getLocation() {
        return sign.getLocation();
    }

    public void update() {
        if (Bukkit.getPluginManager().isPluginEnabled(BuildBattle.getInstance())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), () -> {
                sign.setLine(0, Message.SIGN_JOIN_FIRST_LINE.getMessage().replaceAll("%teamsize%", String.valueOf(arena.getTeamSize())).replaceAll("%arena%", arena.getName()).replaceAll("%gamestate%", arena.getBBArenaState().getPrefix()).replaceAll("%players%", arena.getTotalPlayers()).replaceAll("%mode%", arena.getGameType().getName()));
                sign.setLine(1, Message.SIGN_JOIN_SECOND_LINE.getMessage().replaceAll("%teamsize%", String.valueOf(arena.getTeamSize())).replaceAll("%arena%", arena.getName()).replaceAll("%gamestate%", arena.getBBArenaState().getPrefix()).replaceAll("%players%", arena.getTotalPlayers()));
                sign.setLine(2, Message.SIGN_JOIN_THIRD_LINE.getMessage().replaceAll("%teamsize%", String.valueOf(arena.getTeamSize())).replaceAll("%arena%", arena.getName()).replaceAll("%gamestate%", arena.getBBArenaState().getPrefix()).replaceAll("%players%", arena.getTotalPlayers()));
                sign.setLine(3, Message.SIGN_JOIN_FOURTH_LINE.getMessage().replaceAll("%teamsize%", String.valueOf(arena.getTeamSize())).replaceAll("%arena%", arena.getName()).replaceAll("%gamestate%", arena.getBBArenaState().getPrefix()).replaceAll("%players%", arena.getTotalPlayers()));
                sign.update(true);
                if (BBSettings.isReplaceBlockBehindSigns()) {
                    CompatBridge.setTypeAndData(blockBehind, arena.getBBArenaState().getBlockMaterial(), (byte) arena.getBBArenaState().getBlockMaterial().getData());
                    //getBlockBehind().setType(getArena().getBBArenaState().getBlockMaterial().getMaterial());
                }
            }, 20L);
        }
    }

    public Sign getSign() {
        return sign;
    }

    public void removeSign(Player p) {
        BuildBattle.getFileManager().getConfig("signs.yml").get().set(arena.getName() + "." + LocationUtil.getStringFromLocationXYZ(getLocation()), null);
        BuildBattle.getFileManager().getConfig("signs.yml").save();
        arena.getArenaSigns().remove(this);
        blockBehind.setType(CompMaterial.AIR.getMaterial());
        if (p != null) {
            p.sendMessage(BBSettings.getPrefix() + "§aSign for arena §e" + getArena().getName() + "§a successfully removed!");
        }
    }

    public Block getBlockBehind() {
        return blockBehind;
    }
}
