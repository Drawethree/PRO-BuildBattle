package me.drawethree.buildbattle.objects.bbobjects.plot;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.PlotBiome;
import me.drawethree.buildbattle.utils.ItemUtil;
import me.drawethree.buildbattle.utils.ReflectionUtils;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import me.drawethree.buildbattle.utils.compatbridge.model.CompSound;
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
    private CompMaterial currentFloorMaterial;

    public BBPlotOptions(BBPlot plot) {
        this.plot = plot;
        this.currentFloorItem = ItemUtil.create(BuildBattle.getInstance().getSettings().getDefaultFloorMaterial(), 1, Message.GUI_OPTIONS_CHANGE_FLOOR_ITEM_DISPLAYNAME.getMessage(), ItemUtil.colorizeLore(BuildBattle.getInstance().getFileManager().getConfig("src/main/resources/translates.yml").get().getStringList("gui.options.items.change_floor_item.lore")), null, null);
        this.currentFloorMaterial = CompMaterial.fromMaterial(currentFloorItem.getType());
        this.currentWeather = WeatherType.CLEAR;
        this.currentTime = BBPlotTime.NOON;
        this.currentBiome = PlotBiome.FOREST;
    }

    public void setCurrentFloorItem(Player changer, ItemStack currentFloorItem) {

        if(this.plot.getArena().getPlugin().getSettings().getRestricedBlocks().contains(currentFloorItem.getType())) {
            changer.sendMessage(Message.BLOCK_RESTRICTED.getChatMessage());
            return;
        }

        if (currentFloorItem.getType().isBlock() || currentFloorItem.getType() == CompMaterial.LAVA_BUCKET.getMaterial() || currentFloorItem.getType() == CompMaterial.WATER_BUCKET.getMaterial()) {
            if (!isItemValidForChange(currentFloorItem)) {
                if(changer != null) {
                    changer.sendMessage(Message.FLOOR_DENY_CHANGE.getChatMessage());
                    return;
                }
            }

            this.currentFloorMaterial = CompMaterial.fromMaterial(currentFloorItem.getType());
            this.currentFloorItem = ItemUtil.create(currentFloorMaterial, 1, this.currentFloorItem.getItemMeta().getDisplayName(), this.currentFloorItem.getItemMeta().getLore(), null, null);
            plot.changeFloor(currentFloorMaterial);
            if(changer != null) {
                changer.sendMessage(Message.FLOOR_CHANGED.getChatMessage());
                changer.playSound(changer.getLocation(), CompSound.NOTE_PLING.getSound(), 1, 2.0F);
            }

        } else {
            if(changer != null) {
                changer.sendMessage(Message.FLOOR_DENY_CHANGE.getChatMessage());
            }
        }
    }

    public void setCurrentWeather(Player changer, WeatherType currentWeather, boolean broadcast) {
        this.currentWeather = currentWeather;
        if (broadcast && changer != null) {
            changer.sendMessage(Message.WEATHER_CHANGED.getChatMessage().replace("%weather%", currentWeather.name()));
        }
        for (Player p : plot.getTeam().getPlayers()) p.setPlayerWeather(currentWeather);
    }

    public void setCurrentTime(Player changer, BBPlotTime currentTime, boolean broadcast) {
        this.currentTime = currentTime;
        for (Player p : plot.getTeam().getPlayers()) p.setPlayerTime(currentTime.getTime(), false);
        if (broadcast && changer != null) {
            changer.sendMessage(Message.TIME_CHANGED.getChatMessage().replace("%time%", currentTime.getName()));
        }
    }

    private boolean isItemValidForChange(ItemStack currentFloorItem) {
        return !(CompMaterial.isLongGrass(currentFloorItem.getType())
                || CompMaterial.isDoublePlant(currentFloorItem.getType())
                || CompMaterial.isFlower(currentFloorItem.getType())
                || CompMaterial.isCarpet(currentFloorItem.getType())
                || CompMaterial.isSapling(currentFloorItem.getType())
                || CompMaterial.isWoodButton(currentFloorItem.getType())
                || currentFloorItem.getType() == CompMaterial.LADDER.getMaterial()
                || currentFloorItem.getType() == CompMaterial.VINE.getMaterial()
                || currentFloorItem.getType() == CompMaterial.CHORUS_FLOWER.getMaterial()
                || currentFloorItem.getType() == CompMaterial.CHORUS_PLANT.getMaterial()
                || currentFloorItem.getType() == CompMaterial.CAMPFIRE.getMaterial()
                || currentFloorItem.getType() == CompMaterial.CACTUS.getMaterial());
    }


    public void setCurrentBiome(Player changer, PlotBiome currentBiome, boolean broadcast) {
        this.currentBiome = currentBiome;
        if (broadcast && changer != null) {
            changer.sendMessage(Message.BIOME_CHANGED.getChatMessage().replace("%biome%", getCurrentBiome().getName()));
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

    public BBPlot getPlot() {
        return plot;
    }

    public ItemStack getCurrentFloorItem() {
        return currentFloorItem;
    }

    public BBPlotTime getCurrentTime() {
        return currentTime;
    }

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }
}
