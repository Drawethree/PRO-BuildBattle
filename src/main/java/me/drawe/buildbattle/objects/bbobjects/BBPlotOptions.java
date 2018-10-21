package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlotBiome;
import me.drawe.buildbattle.utils.ItemCreator;
import me.drawe.buildbattle.utils.ReflectionUtils;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;


public class BBPlotOptions {

    private BBPlot plot;
    private ItemStack currentFloorItem;
    private WeatherType currentWeather;
    private BBPlotTime currentTime;
    private PlotBiome currentBiome;
    private CompMaterial currentFloorMaterial;


    public BBPlotOptions(BBPlot plot) {
        this.plot = plot;
        this.currentFloorItem = ItemCreator.create(GameManager.getDefaultFloorMaterial(), 1, Message.GUI_OPTIONS_CHANGE_FLOOR_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("messages.yml").get().getStringList("gui.options.items.change_floor_item.lore")), null, null);
        this.currentFloorMaterial = CompMaterial.fromMaterial(currentFloorItem.getType());
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
                    this.currentFloorItem = ItemCreator.create(currentFloorMaterial, 1, getCurrentFloorItem().getItemMeta().getDisplayName(), getCurrentFloorItem().getItemMeta().getLore(), null, null);
                    getPlot().changeFloor(currentFloorItem);
                    for (Player p : getPlot().getTeam().getPlayers())
                        p.sendMessage(Message.FLOOR_CHANGED.getChatMessage());
                }
            } else if (currentFloorItem.getType() == CompMaterial.WATER_BUCKET.getMaterial()) {
                this.currentFloorItem = ItemCreator.create(currentFloorMaterial, 1, getCurrentFloorItem().getItemMeta().getDisplayName(), getCurrentFloorItem().getItemMeta().getLore(), null, null);
                getPlot().changeFloor(CompMaterial.WATER);
                for(Player p : getPlot().getTeam().getPlayers()) p.sendMessage(Message.FLOOR_CHANGED.getChatMessage());
            } else if (currentFloorItem.getType() == CompMaterial.LAVA_BUCKET.getMaterial()) {
                this.currentFloorItem = ItemCreator.create(currentFloorMaterial, 1, getCurrentFloorItem().getItemMeta().getDisplayName(), getCurrentFloorItem().getItemMeta().getLore(), null, null);
                getPlot().changeFloor(CompMaterial.LAVA);
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
        CompMaterial m = CompMaterial.fromMaterial(currentFloorItem.getType());
        if (m == CompMaterial.CACTUS
                || m == CompMaterial.STONE_BUTTON
                || m.name().contains("BUTTON")
                || m == CompMaterial.LADDER
                || m.name().contains("SAPLING")
                || m == CompMaterial.SEA_PICKLE
                || m == CompMaterial.GRASS
                || m == CompMaterial.FERN
                || m == CompMaterial.DEAD_BUSH
                || m == CompMaterial.SEAGRASS
                || m == CompMaterial.BLUE_ORCHID
                || m == CompMaterial.ALLIUM
                || m == CompMaterial.END_ROD
                || m == CompMaterial.LILAC
                || m == CompMaterial.PEONY
                || m == CompMaterial.ROSE_BUSH
                || m == CompMaterial.TALL_GRASS
                || m == CompMaterial.LARGE_FERN
                || m == CompMaterial.PAINTING
                || m.name().contains("TULIP")
                || m.name().contains("GATE")
                || m.name().contains("PRESSURE")
                || m == CompMaterial.LILY_PAD
                || m == CompMaterial.LEVER
                || m == CompMaterial.POPPY
                || m == CompMaterial.DANDELION
                || m == CompMaterial.VINE
                || m == CompMaterial.SUNFLOWER
                || m == CompMaterial.TRIPWIRE_HOOK) {
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
                    ReflectionUtils.sendPacket(p, ReflectionUtils.getNMSClass("PacketPlayOutMapChunk").getConstructor(ReflectionUtils.getNMSClass("Chunk"), boolean.class, int.class).newInstance(c.getClass().getMethod("getHandle").invoke(c), true, 65535));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    try {
                        ReflectionUtils.sendPacket(p, ReflectionUtils.getNMSClass("PacketPlayOutMapChunk").getConstructor(ReflectionUtils.getNMSClass("Chunk"), int.class).newInstance(c.getClass().getMethod("getHandle").invoke(c), 65535));
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
