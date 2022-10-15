package net.kitsu.library.spigot.command;

import net.kitsu.library.spigot.command.template.CommandLibHelp;
import net.kitsu.library.util.Buffer;
import net.kitsu.localedictionary.api.Dictionary;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class CommandLibHandlerIndex extends CommandLibHandler implements CommandExecutor, TabCompleter {
    private final HashMap<String, CommandLibExecutor> commandMap;
    private final CommandLibHelp commandLibHelp;
    private final String permission;
    private final String permissionBase;
    private final String command;


    public CommandLibHandlerIndex(String command, String permission){
        if(permission == null)
            permission = "";

        if(!permission.isEmpty())
            this.permissionBase = String.format("%s.%s", permission, command);

        else
            this.permissionBase = command;

        this.permission = String.format("%s.base", this.getPermissionBase());
        this.command = command;

        this.commandMap = new HashMap<>();

        this.commandLibHelp = new CommandLibHelp(this.commandMap, this.getCommand(), this.getPermissionBase());
        this.addCommand(this.commandLibHelp);
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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.onCommand(sender, command, label, new Buffer<>(args));
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
    public boolean onCommand(CommandSender sender, Command command, String label, Buffer<String> args){
        if(!this.hasPermission(sender))
            return false;

        if(args.remaining() == 0) {
            return this.commandLibHelp.onCommand(sender, command, label, args);
        }

        String nextLabel = args.get().toLowerCase();
        CommandLibExecutor commandLibExecutor = this.commandMap.get(nextLabel);

        if(commandLibExecutor == null){
            args.position(args.position()-1);
            return this.commandLibHelp.onCommand(sender, command, label, args);

        }else{
            try {
                return commandLibExecutor.onCommand(sender, command, label, args);

            }catch (Throwable ignore){
                args.position(args.position()-1);
                return this.commandLibHelp.onCommand(sender, command, label, args);

            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return this.onTabComplete(sender, command, label, new Buffer<>(args));
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, Buffer<String> args){
        if(args.remaining() == 0)
            return null;

        if(args.remaining() == 1)
            return this.tabCompleteFilter(args.getCurrent(), sender);

        CommandLibExecutor commandLibExecutor = this.commandMap.get(args.get().toLowerCase());

        if(commandLibExecutor == null)
            return null;

        try {
            return commandLibExecutor.onTabComplete(sender, command, label, args);

        }catch (Throwable ignore){
            return null;
        }

    }

    public CommandLibExecutor addCommand(CommandLibExecutor commandLibExecutor){
        if(commandLibExecutor.getCommand() == null) {
            Bukkit.getLogger().warning(String.format("[%s] register command exception: %s\n", this.getPluginName(), this.getPermission()));
            return null;
        }

        CommandLibExecutor result = this.commandMap.put(commandLibExecutor.getCommand(), commandLibExecutor);

        if(commandLibExecutor instanceof CommandLibHandlerIndex){
            CommandLibHandlerIndex commandLibHandler = (CommandLibHandlerIndex) commandLibExecutor;
            commandLibHandler.setPluginName(this.getPluginName());
            commandLibHandler.setDictionary(this.getDictionary());
        }

        return result;
    }

    @Override
    public String getPermission(){
        return this.permission;
    }

    @Override
    public String getCommand(){
        return this.command;
    }

    public String getPermissionBase(){
        return this.permissionBase;
    }

    @Override
    public void setDictionary(Dictionary dictionary){
        super.setDictionary(dictionary);

        for(Map.Entry<String, CommandLibExecutor> entry : this.commandMap.entrySet()){
            CommandLibExecutor commandLibExecutor = entry.getValue();

            if(!(commandLibExecutor instanceof CommandLibHandler))
                continue;

            ((CommandLibHandler) commandLibExecutor).setDictionary(dictionary);
        }
    }

    @Override
    public void setPluginName(String pluginName){
        if(pluginName == null)
            pluginName = "Plugin";

        super.setPluginName(pluginName);

        for(Map.Entry<String, CommandLibExecutor> entry : this.commandMap.entrySet()){
            CommandLibExecutor commandLibExecutor = entry.getValue();

            if(!(commandLibExecutor instanceof CommandLibHandler))
                continue;

            ((CommandLibHandler) commandLibExecutor).setPluginName(pluginName);

        }
    }

    private List<String> tabCompleteFilter(String key, CommandSender commandSender){
        if(key == null)
            key = "";

        List<String> result = new LinkedList<>();
        for(Map.Entry<String, CommandLibExecutor> entry : this.commandMap.entrySet()){
            CommandLibExecutor commandLibExecutor = entry.getValue();
            if(commandLibExecutor == null)
                continue;

            if(commandLibExecutor.hasPermission(commandSender)){
                if(key.isEmpty())
                    result.add(commandLibExecutor.getCommand());

                else if(commandLibExecutor.getCommand().contains(key))
                    result.add(commandLibExecutor.getCommand());
            }
        }

        return result;
    }

    public List<String> getPermissionList(){
        List<String> result = new LinkedList<>();

        for(Map.Entry<String, CommandLibExecutor> entry : this.commandMap.entrySet()){
            CommandLibExecutor commandLibExecutor = entry.getValue();

            if(commandLibExecutor instanceof CommandLibHandlerIndex){
                CommandLibHandlerIndex commandLibHandlerIndex = (CommandLibHandlerIndex) commandLibExecutor;
                result.addAll(commandLibHandlerIndex.getPermissionList());

            }else{
                result.add(commandLibExecutor.getPermission());

            }
        }

        return result;
    }

    public void registerPermission(){
        List<String> permissionList = this.getPermissionList();
        PluginManager pluginManager = Bukkit.getPluginManager();
        Logger logger = Bukkit.getLogger();

        for(String permission : permissionList){
            try {
                pluginManager.addPermission(new Permission(permission));
                logger.info(String.format("[%s] Register permission %s", this.getPluginName(), permission));
            }catch (Throwable ignore){}
        }
    }
}
