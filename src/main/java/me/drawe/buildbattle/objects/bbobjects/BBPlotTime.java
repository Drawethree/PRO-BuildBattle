package me.drawe.buildbattle.objects.bbobjects;


import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.ItemCreator;
import me.kangarko.compatbridge.model.CompMaterial;
import org.bukkit.inventory.ItemStack;

public enum BBPlotTime {

    MORNING(Message.GUI_TIME_MORNING_ITEM_DISPLAYNAME.getMessage(), 0, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_MORNING_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.morning.lore")), null,null), 0),
    MID_MORNING(Message.GUI_TIME_MIDMORNING_ITEM_DISPLAYNAME.getMessage(),3000, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_MIDMORNING_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.mid_morning.lore")), null,null), 1),
    NOON(Message.GUI_TIME_NOON_ITEM_DISPLAYNAME.getMessage(),6000, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_NOON_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.noon.lore")), null,null), 2),
    AFTERNOON(Message.GUI_TIME_AFTERNOON_ITEM_DISPLAYNAME.getMessage(),9000, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_AFTERNOON_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.afternoon.lore")), null,null), 3),
    EVENING(Message.GUI_TIME_EVENING_ITEM_DISPLAYNAME.getMessage(),12000, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_EVENING_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.evening.lore")), null,null), 4),
    NIGHT(Message.GUI_TIME_NIGHT_ITEM_DISPLAYNAME.getMessage(),15000, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_NIGHT_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.night.lore")), null,null), 5),
    MIDNIGHT(Message.GUI_TIME_MIDNIGHT_ITEM_DISPLAYNAME.getMessage(),18000, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_MIDNIGHT_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.midnight.lore")), null,null), 6),
    AFTER_MIDNIGHT(Message.GUI_TIME_AFTERMIDNIGHT_ITEM_DISPLAYNAME.getMessage(),21000, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_AFTERMIDNIGHT_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.after_midnight.lore")), null,null), 7),
    MORNING_2(Message.GUI_TIME_MORNING_ITEM_DISPLAYNAME.getMessage(),24000, ItemCreator.create(CompMaterial.RED_TERRACOTTA, 1, Message.GUI_TIME_MORNING_ITEM_DISPLAYNAME.getMessage(), ItemCreator.convertLore(BuildBattle.getFileManager().getConfig("translates.yml").get().getStringList("gui.time.items.morning.lore")), null,null), 8);

    private String name;
    private int time;
    private int slot;
    private ItemStack item;

    BBPlotTime(String name, int time, ItemStack itemStack, int slot) {
        this.name = name;
        this.slot = slot;
        this.item = itemStack;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public static BBPlotTime getTimeFromItemStack(ItemStack item, int slot) {
        for(BBPlotTime time : values()) {
            if(time.getItem().isSimilar(item) && time.getSlot() == slot) {
                return time;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
