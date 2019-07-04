package me.drawe.buildbattle.objects.bbobjects.plot;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlotBiome;
import me.drawe.buildbattle.utils.ItemUtil;
import me.drawe.buildbattle.utils.ReflectionUtils;
import me.drawe.buildbattle.utils.compatbridge.model.CompSound;
import me.drawe.buildbattle.utils.compatbridge.model.XMaterial;
import org.bukkit.Chunk;
import org.bukkit.Location;
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
    private XMaterial currentFloorMaterial;

    public BBPlotOptions(BBPlot plot) {
        this.plot = plot;
        this.currentFloorItem = ItemUtil.create(BBSettings.getDefaultFloorMaterial(), 1, Message.GUI_OPTIONS_CHANGE_FLOOR_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.options.items.change_floor_item.lore")), null, null);
        this.currentFloorMaterial = XMaterial.matchXMaterial(currentFloorItem.getType());
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
        if (currentFloorItem.getType().isBlock() || currentFloorItem.getType() == XMaterial.LAVA_BUCKET.parseMaterial() || currentFloorItem.getType() == XMaterial.WATER_BUCKET.parseMaterial()) {
            if (!isItemValidForChange(currentFloorItem)) {
                for (Player p : plot.getTeam().getPlayers())
                    p.sendMessage(Message.FLOOR_DENY_CHANGE.getChatMessage());
                return;
            }

            this.currentFloorMaterial = XMaterial.matchXMaterial(currentFloorItem);
            this.currentFloorItem = ItemUtil.create(currentFloorMaterial, 1, this.currentFloorItem.getItemMeta().getDisplayName(), this.currentFloorItem.getItemMeta().getLore(), null, null);
            plot.changeFloor(currentFloorItem);
            for (Player p : plot.getTeam().getPlayers()) {
                p.sendMessage(Message.FLOOR_CHANGED.getChatMessage());
                p.playSound(p.getLocation(), CompSound.NOTE_PLING.getSound(), 1, 2.0F);
            }
        } else {
            for (Player p : plot.getTeam().getPlayers()) p.sendMessage(Message.FLOOR_DENY_CHANGE.getChatMessage());
        }
    }

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(WeatherType currentWeather, boolean broadcast) {
        this.currentWeather = currentWeather;
        if (broadcast) {
            for (Player p : plot.getTeam().getPlayers())
                p.sendMessage(Message.WEATHER_CHANGED.getChatMessage().replaceAll("%weather%", currentWeather.name()));
        }
        for (Player p : plot.getTeam().getPlayers()) p.setPlayerWeather(currentWeather);
    }

    public BBPlotTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(BBPlotTime currentTime, boolean broadcast) {
        this.currentTime = currentTime;
        for (Player p : plot.getTeam().getPlayers()) p.setPlayerTime(currentTime.getTime(), false);
        if (broadcast) {
            for (Player p : plot.getTeam().getPlayers()) p.sendMessage(Message.TIME_CHANGED.getChatMessage().replaceAll("%time%", currentTime.getName()));
        }
    }

    private boolean isItemValidForChange(ItemStack currentFloorItem) {
        return !(XMaterial.isLongGrass(currentFloorItem.getType())
                || XMaterial.isButton(currentFloorItem.getType())
                || XMaterial.isFlower(currentFloorItem.getType())
                || XMaterial.isPressurePlate(currentFloorItem.getType())
                || XMaterial.isBed(currentFloorItem.getType())
                || XMaterial.isMushroom(currentFloorItem.getType())
                || XMaterial.isChorus(currentFloorItem.getType())
                || currentFloorItem.getType() == XMaterial.LADDER.parseMaterial()
                || currentFloorItem.getType() == XMaterial.CACTUS.parseMaterial());
    }


    public void setCurrentBiome(PlotBiome currentBiome, boolean broadcast) {
        this.currentBiome = currentBiome;
        if (broadcast) {
            for (Player p : plot.getTeam().getPlayers()) p.sendMessage(Message.BIOME_CHANGED.getChatMessage().replaceAll("%biome%", getCurrentBiome().getName()));
        }
        for (Location l : plot.getBlocksInPlot()) {
            l.getBlock().setBiome(currentBiome.getBiome().getBiome());
        }
        for (Chunk c : plot.getChunksInPlot()) {
            for (Player p : plot.getArena().getPlayers()) {
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
