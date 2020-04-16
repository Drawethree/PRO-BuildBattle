package me.drawethree.buildbattle.objects.bbobjects.sign;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.ConfigLoadable;
import me.drawethree.buildbattle.objects.bbobjects.ConfigSaveable;
import me.drawethree.buildbattle.utils.LocationUtil;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

@Getter
public abstract class BBSign implements ConfigLoadable, ConfigSaveable {


    private BuildBattle parent;
    private BBSignType type;
    protected Sign sign;
    protected Block blockBehind;

    public BBSign(BuildBattle parent, BBSignType type, Location loc) {
        this.parent = parent;
        this.type = type;

        try {
            this.sign = (Sign) loc.getBlock().getState();
        } catch (Exception e) {
            //No sign at this location.
            return;
        }

        this.blockBehind = LocationUtil.getAttachedBlock(sign);
        this.blockBehind.setType(CompMaterial.WHITE_TERRACOTTA.getMaterial());
        this.update();
    }

    public void removeSign(Player p) {
        this.getParent().getFileManager().getConfig("signs.yml").get().set("signs." + this.type.getConfigPath() + "." + LocationUtil.getStringFromLocationXYZ(getLocation()), null);
        this.getParent().getFileManager().getConfig("signs.yml").save();

        this.blockBehind.setType(CompMaterial.AIR.getMaterial());
        if (p != null) {
            p.sendMessage(BuildBattle.getInstance().getSettings().getPrefix() + "§a" + this.type + " sign successfully removed!");
        }
    }

    public abstract void handleClick(Player whoClicked, Action clickType);

    public abstract void update();

    public Location getLocation() {
        return sign.getLocation();
    }

    public boolean isValid() {
        return this.sign != null;
    }

    public void printInvalidCause() {
        if(this.sign == null) {
            this.parent.warning("Invalid Location.");
        }
    }
}
