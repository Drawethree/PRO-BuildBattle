package me.drawethree.buildbattle.objects.bbobjects.sign;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.utils.LocationUtil;
import org.bukkit.Location;

public abstract class BBArenaSign extends BBSign {

    @Getter
    public BBArena arena;

    public BBArenaSign(BuildBattle parent, BBSignType type, Location loc, BBArena arena) {
        super(parent, type, loc);
        this.arena = arena;
    }

    public BBArenaSign(BuildBattle parent, BBSignType type, Location loc) {
        super(parent, type, loc);
        this.load();
    }

    @Override
    public void load() {
        if(this.sign == null) {
            return;
        }
        this.arena = this.getParent().getArenaManager().getArena(this.getParent().getFileManager().getConfig("src/main/resources/signs.yml").get().getString("signs." + this.getType().getConfigPath() + "." + LocationUtil.getStringFromLocationXYZ(this.getLocation())));
    }

    @Override
    public void save() {
        this.getParent().getFileManager().getConfig("src/main/resources/signs.yml").set("signs." + this.getType().getConfigPath() + "." + LocationUtil.getStringFromLocationXYZ(this.getLocation()), this.arena.getName()).save();
    }

    @Override
    public boolean isValid() {
        return this.arena != null && super.isValid();
    }

    @Override
    public void printInvalidCause() {
        if (this.arena == null) {
            this.getParent().warning("Invalid Arena.");
        }
        super.printInvalidCause();

    }
}
