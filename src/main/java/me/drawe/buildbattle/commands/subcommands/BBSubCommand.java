package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import org.bukkit.command.CommandSender;

public abstract class BBSubCommand {

    private String subCommand;
    private String permissionRequired;
    private String description;
    private boolean isAdminCommand;

    public BBSubCommand(String subCommand, String description, String permissionRequired, boolean isAdminCommand) {
        this.subCommand = subCommand;
        this.description = description;
        this.permissionRequired = permissionRequired;
        this.isAdminCommand = isAdminCommand;
    }

    public abstract boolean execute(BBCommand cmd, CommandSender sender, String[] args);

    public String getPermissionRequired() {
        return permissionRequired;
    }

    public String getDescription() {
        return description;
    }

    public String getSubCommand() {
        return subCommand;
    }

    public boolean isAdminCommand() {
        return isAdminCommand;
    }
}

