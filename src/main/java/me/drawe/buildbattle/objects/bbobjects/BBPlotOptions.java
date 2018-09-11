package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlotBiome;
import me.drawe.buildbattle.utils.ItemCreator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

import static me.drawe.buildbattle.utils.ReflectionUtils.getNMSClass;
import static me.drawe.buildbattle.utils.ReflectionUtils.sendPacket;


public class BBPlotOptions {

    private BBPlot plot;
    private ItemStack currentFloorItem;
    private WeatherType currentWeather;
    private BBPlotTime currentTime;
    private PlotBiome currentBiome;


    public BBPlotOptions(BBPlot plot) {
        this.plot = plot;
        this.currentFloorItem = ItemCreator.create(GameManager.getDefaultFloorMaterial(), 1, Message.GUI_OPTIONS_CHANGE_FLOOR_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.change_floor_item.lore")), null, null);
        this.currentWeather = WeatherType.CLEAR;
        this.currentTime = BBPlotTime.NOON;
        this.currentBiome = PlotBiome.FOREST;
    }

    public BBPlot getPlot() {
        return plot;
    }

    public ItemStack getCurrentFloorItem() {
        return currentFloorItem;
    }

    public void setCurrentFloorItem(ItemStack currentFloorItem) {
            if (currentFloorItem.getType().isBlock()) {
                if(!isItemValidForChange(currentFloorItem)) {
                    for(Player p : getPlot().getTeam().getPlayers()) p.sendMessage(Message.FLOOR_DENY_CHANGE.getChatMessage());
                    return;
                } else {
                    this.currentFloorItem = ItemCreator.create(currentFloorItem.getType(), 1, getCurrentFloorItem().getItemMeta().getDisplayName(), getCurrentFloorItem().getItemMeta().getLore(), null, null);
                    getPlot().changeFloor(currentFloorItem);
                    for (Player p : getPlot().getTeam().getPlayers())
                        p.sendMessage(Message.FLOOR_CHANGED.getChatMessage());
                }
            } else if (currentFloorItem.getType() == Material.WATER_BUCKET) {
                this.currentFloorItem = ItemCreator.create(currentFloorItem.getType(), 1, getCurrentFloorItem().getItemMeta().getDisplayName(), getCurrentFloorItem().getItemMeta().getLore(), null, null);
                getPlot().changeFloor(Material.WATER);
                for(Player p : getPlot().getTeam().getPlayers()) p.sendMessage(Message.FLOOR_CHANGED.getChatMessage());
            } else if (currentFloorItem.getType() == Material.LAVA_BUCKET) {
                this.currentFloorItem = ItemCreator.create(currentFloorItem.getType(), 1, getCurrentFloorItem().getItemMeta().getDisplayName(), getCurrentFloorItem().getItemMeta().getLore(), null, null);
                getPlot().changeFloor(Material.LAVA);
                for(Player p : getPlot().getTeam().getPlayers()) p.sendMessage(Message.FLOOR_CHANGED.getChatMessage());
            } else {
                for(Player p : getPlot().getTeam().getPlayers()) p.sendMessage(Message.FLOOR_DENY_CHANGE.getChatMessage());
            }
     }

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(WeatherType currentWeather, boolean b) {
        this.currentWeather = currentWeather;
        if(b) {
            getPlot().getTeam().getCaptain().sendMessage(Message.WEATHER_CHANGED.getChatMessage().replaceAll("%weather%", getCurrentWeather().name()));
        }
        for(Player p : getPlot().getTeam().getPlayers()) p.setPlayerWeather(getCurrentWeather());
    }

    public BBPlotTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(BBPlotTime currentTime, boolean broadcast) {
        this.currentTime = currentTime;
        for(Player p : getPlot().getTeam().getPlayers()) p.setPlayerTime(getCurrentTime().getTime(), false);
        if(broadcast) {
            getPlot().getTeam().getCaptain().sendMessage(Message.TIME_CHANGED.getChatMessage().replaceAll("%time%", getCurrentTime().getName()));
        }
    }

    private boolean isItemValidForChange(ItemStack currentFloorItem) {
        Material m = currentFloorItem.getType();
        if (m == Material.CACTUS
                || m == Material.STONE_BUTTON
                || m.name().contains("BUTTON")
                || m == Material.LEVER
                || m == Material.POPPY
                || m == Material.DANDELION
                || m == Material.VINE
                || m == Material.SUNFLOWER
                || m == Material.TRIPWIRE_HOOK) {
            return false;
        } else {
            return true;
        }
    }


   public void setCurrentBiome(PlotBiome currentBiome, boolean broadcast) {
        this.currentBiome = currentBiome;
        if(broadcast) {
            for(Player p : getPlot().getTeam().getPlayers()) p.sendMessage(Message.BIOME_CHANGED.getChatMessage().replaceAll("%biome%", getCurrentBiome().getName()));
        }
        for(Location l : getPlot().getBlocksInPlot()) {
            l.getBlock().setBiome(currentBiome.getBiome());
        }
        for(Chunk c : getPlot().getChunksInPlot()) {
            for (Player p : getPlot().getArena().getPlayers()) {
                try {
                    sendPacket(p, getNMSClass("PacketPlayOutMapChunk").getConstructor(getNMSClass("Chunk"), boolean.class, int.class).newInstance(c.getClass().getMethod("getHandle").invoke(c), true, 65535));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    try {
                        sendPacket(p, getNMSClass("PacketPlayOutMapChunk").getConstructor(getNMSClass("Chunk"), int.class).newInstance(c.getClass().getMethod("getHandle").invoke(c), 65535));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }


    public PlotBiome getCurrentBiome() {
        return currentBiome;
    }
}
