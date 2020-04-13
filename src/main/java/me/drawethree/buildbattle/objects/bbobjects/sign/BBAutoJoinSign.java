package me.drawethree.buildbattle.objects.bbobjects.sign;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.bbobjects.BBGameMode;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;


public class BBAutoJoinSign extends BBSign {

    private BBGameMode gamemode;

    public BBAutoJoinSign(BuildBattle plugin, Location location) {
        super(plugin, BBSignType.AUTO_JOIN, location);
        this.load();
    }

    public BBAutoJoinSign(BuildBattle plugin, BBGameMode gamemode, Location location) {
        super(plugin, BBSignType.AUTO_JOIN, location);
        this.gamemode = gamemode;
    }

    @Override
    public void handleClick(Player whoClicked, Action clickType) {
        if (clickType == Action.RIGHT_CLICK_BLOCK) {
            BBArena arenaToAutoJoin = this.getParent().getArenaManager().getArenaToAutoJoin(gamemode);
            if (arenaToAutoJoin != null) {
                arenaToAutoJoin.addPlayer(whoClicked);
            } else {
                whoClicked.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
            }
        }
    }

    @Override
    public void update() {
        if (Bukkit.getPluginManager().isPluginEnabled(BuildBattle.getInstance())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.getParent(), () -> {
                if (gamemode == null) {
                    getSign().setLine(0, Message.SIGN_AUTO_JOIN_FIRST_LINE.getMessage());
                    getSign().setLine(1, Message.SIGN_AUTO_JOIN_SECOND_LINE.getMessage());
                    getSign().setLine(2, Message.SIGN_AUTO_JOIN_THIRD_LINE.getMessage());
                    getSign().setLine(3, Message.SIGN_AUTO_JOIN_FOURTH_LINE.getMessage());
                    getSign().update(true);
                    return;
                }

                switch (gamemode) {
                    case SOLO:
                        getSign().setLine(0, Message.SIGN_AUTO_JOIN_SOLO_FIRST_LINE.getMessage());
                        getSign().setLine(1, Message.SIGN_AUTO_JOIN_SOLO_SECOND_LINE.getMessage());
                        getSign().setLine(2, Message.SIGN_AUTO_JOIN_SOLO_THIRD_LINE.getMessage());
                        getSign().setLine(3, Message.SIGN_AUTO_JOIN_SOLO_FOURTH_LINE.getMessage());
                        break;
                    case TEAM:
                        getSign().setLine(0, Message.SIGN_AUTO_JOIN_TEAM_FIRST_LINE.getMessage());
                        getSign().setLine(1, Message.SIGN_AUTO_JOIN_TEAM_SECOND_LINE.getMessage());
                        getSign().setLine(2, Message.SIGN_AUTO_JOIN_TEAM_THIRD_LINE.getMessage());
                        getSign().setLine(3, Message.SIGN_AUTO_JOIN_TEAM_FOURTH_LINE.getMessage());
                        break;
                }
                getSign().update(true);
            }, 20L);
        }
    }

    @Override
    public void load() {

        if(this.sign == null) {
            return;
        }

        String loadedSting = this.getParent().getFileManager().getConfig("signs.yml").get().getString("signs." + this.getType().getConfigPath() + "." + LocationUtil.getStringFromLocationXYZ(this.getLocation()));
        if (loadedSting.equals("BOTH")) {
            this.gamemode = null;
        } else {
            this.gamemode = BBGameMode.valueOf(loadedSting);
        }
    }

    @Override
    public void save() {
        this.getParent().getFileManager().getConfig("signs.yml").set("signs." + this.getType().getConfigPath() + "." + LocationUtil.getStringFromLocationXYZ(this.getLocation()), this.gamemode == null ? "BOTH" : gamemode.name()).save();
    }
}
