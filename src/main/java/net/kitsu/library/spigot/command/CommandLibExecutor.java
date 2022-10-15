package net.kitsu.library.spigot.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.logging.Logger;

public interface CommandLibExecutor{

    default Logger getLogger(){
        return Bukkit.getLogger();
    }

    String getPermission();
    String getCommand();
    default String getDescription(String locale){
        return "";
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    boolean onCommand(CommandSender sender, Command command, String label, net.kitsu.library.util.Buffer<String> args);

    /**
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    List<String> onTabComplete(CommandSender sender, Command command, String label, net.kitsu.library.util.Buffer<String> args);

    default boolean hasPermission(CommandSender commandSender){
        return commandSender.hasPermission(this.getPermission());
    }
}
