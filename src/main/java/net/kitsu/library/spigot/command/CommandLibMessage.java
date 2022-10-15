package net.kitsu.library.spigot.command;

import net.kitsu.library.spigot.command.message.DefaultDictionary;
import net.kitsu.localedictionary.api.Dictionary;

import java.util.HashMap;
import java.util.Map;

public enum CommandLibMessage {
    NO_PERMISSION,
    NO_DESCRIPTION,
    PLUGIN_DESCRIPTION_NULL,
    HELP_LABEL,
    HELP_DESC,
    VERSION_LABEL,
    VERSION_DESC;

    public static final String keyBase = "net.kitsu.library.spigot.command";
    private static final Dictionary defaultDictionary = createDefaultDictionary();

    public String getKey(){
        return String.format("%s.%s", CommandLibMessage.keyBase, this);
    }

    public static Map<String, String> getDefaultMessage(){
        Map<String, String> result = new HashMap<>();

        result.put(CommandLibMessage.NO_PERMISSION.getKey(), "You do not have permission to run this command.");
        result.put(CommandLibMessage.NO_DESCRIPTION.getKey(), "This command no any description.");
        result.put(CommandLibMessage.PLUGIN_DESCRIPTION_NULL.getKey(), "Plugin description is null.");
        result.put(CommandLibMessage.HELP_DESC.getKey(), "Command help.");
        result.put(CommandLibMessage.HELP_LABEL.getKey(), "%PLUGIN_NAME% Help");
        result.put(CommandLibMessage.VERSION_DESC.getKey(), "%PLUGIN_NAME% Version.");
        result.put(CommandLibMessage.VERSION_LABEL.getKey(), "%PLUGIN_NAME% Version");

        return result;
    }

    private static Dictionary createDefaultDictionary(){
        return new DefaultDictionary(getDefaultMessage());
    }

    public static Dictionary getDefaultDictionary(){
        return CommandLibMessage.defaultDictionary;
    }
}
