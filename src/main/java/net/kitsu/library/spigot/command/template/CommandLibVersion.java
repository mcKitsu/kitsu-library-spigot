package net.kitsu.library.spigot.command.template;

import net.kitsu.library.spigot.command.CommandLibHandler;
import net.kitsu.library.spigot.command.CommandLibMessage;
import net.kitsu.library.util.Buffer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public abstract class CommandLibVersion extends CommandLibHandler {
    private final String permission;

    public CommandLibVersion(String permission){
        this.permission = String.format("%s.%s", permission, this.getCommand());
    }

    public abstract PluginDescriptionFile getPluginDescriptionFile();

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public String getCommand() {
        return "version";
    }

    @Override
    public String getDescription(String locale){
        return this.getMessage(locale, CommandLibMessage.VERSION_DESC.getKey()).replace("%PLUGIN_NAME%", this.getPluginName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, Buffer<String> args) {
        if(!this.hasPermission(sender))
            return false;

        String locale = "en_us";

        if(sender instanceof Player){
            locale = ((Player) sender).getLocale();
        }

        PluginDescriptionFile d = this.getPluginDescriptionFile();

        if(d == null){
            sender.sendMessage(this.getMessage(locale, CommandLibMessage.PLUGIN_DESCRIPTION_NULL.getKey()));

        }else{

            ComponentBuilder componentBuilder = new ComponentBuilder();

            componentBuilder.append("Name: ").color(ChatColor.GOLD);
            componentBuilder.append(d.getName()).color(ChatColor.WHITE);

            componentBuilder.append("\nVersion: ").color(ChatColor.GOLD);
            componentBuilder.append(d.getVersion()).color(ChatColor.WHITE);;

            String labelFormat = this.getMessage(locale, CommandLibMessage.VERSION_LABEL.getKey());
            labelFormat = labelFormat.replace("%PLUGIN_NAME%", d.getName());
            sender.spigot().sendMessage(this.getLabel(labelFormat));

            sender.spigot().sendMessage(componentBuilder.create());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, Buffer<String> args) {
        return null;
    }

}
