package net.kitsu.library.spigot.command.template;

import net.kitsu.library.spigot.command.CommandLibExecutor;
import net.kitsu.library.spigot.command.CommandLibHandler;
import net.kitsu.library.spigot.command.CommandLibMessage;
import net.kitsu.library.util.Buffer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandLibHelp extends CommandLibHandler {
    private final HashMap<String, CommandLibExecutor> commandList;
    private final String permission;
    private final String superCommand;

    public CommandLibHelp(HashMap<String, CommandLibExecutor> commandList, String supperCommand, String permission){
        this.commandList = commandList;
        this.permission = String.format("%s.%s", permission, this.getCommand());
        this.superCommand = supperCommand;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription(String locale){
        return this.getMessage(locale, CommandLibMessage.HELP_DESC.getKey());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, Buffer<String> args) {
        if(sender instanceof Player)
            return this.onCommandPlayer(((Player) sender), command, label, args);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§a");
        stringBuilder.append(this.superCommand);
        stringBuilder.append("\n");
        String locale = "en_us";

        for(Map.Entry<String, CommandLibExecutor> entry : this.commandList.entrySet()){
            String description = entry.getValue().getDescription(locale);
            if(description == null)
                description = this.getMessage(locale, CommandLibMessage.NO_DESCRIPTION.getKey());

            stringBuilder.append(String.format("  %s - %s\n", entry.getKey(), description));
        }

        sender.sendMessage(stringBuilder.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, Buffer<String> args) {
        return null;
    }

    private boolean onCommandPlayer(Player player, Command command, String label, Buffer<String> args){
        if(!this.hasPermission(player))
            return false;

        this.showHeader(player, command, label, args);

        String locale = player.getLocale();

        for(Map.Entry<String, CommandLibExecutor> entry : this.commandList.entrySet()){
            TextComponent textComponent = new TextComponent(String.format("  %s", entry.getKey()));
            textComponent.setColor(ChatColor.GREEN);

            String description = entry.getValue().getDescription(locale);
            if(description == null){
                description = this.getMessage(locale, CommandLibMessage.NO_DESCRIPTION.getKey());
            }


            textComponent.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new Text(description)));
            player.spigot().sendMessage(textComponent);
        }

        return true;
    }

    private void showHeader(Player player, Command command, String label, Buffer<String> args){
        String locale = player.getLocale();

        String labelFormat = this.getMessage(locale, CommandLibMessage.HELP_LABEL.getKey());
        labelFormat = labelFormat.replace("%PLUGIN_NAME%", this.getPluginName());
        player.spigot().sendMessage(this.getLabel(labelFormat));

        player.spigot().sendMessage(this.getCommandPath(label, args));
    }

    private BaseComponent[] getCommandPath(String label, Buffer<String> args){
        ComponentBuilder componentBuilder = new ComponentBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("/%s ", label));

        String[] s = args.array();
        int position = args.position();

        if(position != 0){
            if(s[position-1].compareToIgnoreCase(this.getCommand()) == 0)
                --position;
        }

        for(int i=0; i<position; ++i){
            stringBuilder.append(s[i]);
            stringBuilder.append(" ");
        }

        componentBuilder.append("Command: ").color(ChatColor.GOLD);
        componentBuilder.append(stringBuilder.toString()).color(ChatColor.WHITE);

        return componentBuilder.create();
    }


}
