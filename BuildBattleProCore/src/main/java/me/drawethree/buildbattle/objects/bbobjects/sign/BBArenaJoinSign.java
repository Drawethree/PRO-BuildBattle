package me.drawethree.buildbattle.objects.bbobjects.sign;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.utils.compatbridge.model.CompatBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class BBArenaJoinSign extends BBArenaSign {

    public BBArenaJoinSign(BuildBattle parent, BBArena arena, Location loc) {
        super(parent, BBSignType.JOIN, loc, arena);
    }

    public BBArenaJoinSign(BuildBattle parent, Location loc) {
        super(parent, BBSignType.JOIN, loc);
    }

    @Override
    public void handleClick(Player whoClicked, Action clickType) {
        if(clickType == Action.RIGHT_CLICK_BLOCK) {
            arena.addPlayer(whoClicked);
        }
    }

    @Override
    public void update() {
        if (Bukkit.getPluginManager().isPluginEnabled(BuildBattle.getInstance())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), () -> {
                sign.setLine(0, Message.SIGN_JOIN_FIRST_LINE.getMessage().replace("%teamsize%", String.valueOf(arena.getTeamSize())).replace("%arena%", arena.getName()).replace("%gamestate%", arena.getBBArenaState().getPrefix()).replace("%players%", arena.getTotalPlayers()).replace("%mode%", arena.getGameType().getName()));
                sign.setLine(1, Message.SIGN_JOIN_SECOND_LINE.getMessage().replace("%teamsize%", String.valueOf(arena.getTeamSize())).replace("%arena%", arena.getName()).replace("%gamestate%", arena.getBBArenaState().getPrefix()).replace("%players%", arena.getTotalPlayers()).replace("%mode%", arena.getGameType().getName()));
                sign.setLine(2, Message.SIGN_JOIN_THIRD_LINE.getMessage().replace("%teamsize%", String.valueOf(arena.getTeamSize())).replace("%arena%", arena.getName()).replace("%gamestate%", arena.getBBArenaState().getPrefix()).replace("%players%", arena.getTotalPlayers()).replace("%mode%", arena.getGameType().getName()));
                sign.setLine(3, Message.SIGN_JOIN_FOURTH_LINE.getMessage().replace("%teamsize%", String.valueOf(arena.getTeamSize())).replace("%arena%", arena.getName()).replace("%gamestate%", arena.getBBArenaState().getPrefix()).replace("%players%", arena.getTotalPlayers()).replace("%mode%", arena.getGameType().getName()));
                sign.update(true);
                if (BuildBattle.getInstance().getSettings().isReplaceBlockBehindSigns()) {
                    CompatBridge.setTypeAndData(blockBehind, arena.getBBArenaState().getBlockMaterial(), (byte) arena.getBBArenaState().getBlockMaterial().getData());
                    //getBlockBehind().setType(getArena().getBBArenaState().getBlockMaterial().getMaterial());
                }
            }, 20L);
        }
    }
}
