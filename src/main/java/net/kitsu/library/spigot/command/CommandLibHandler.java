package net.kitsu.library.spigot.command;

import net.kitsu.localedictionary.api.Dictionary;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class CommandLibHandler implements CommandLibExecutor {
    private String pluginName;
    private Dictionary dictionary;

    public void setPluginName(String pluginName) {
        if (pluginName == null)
            pluginName = "Plugin";

        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary() {
        return this.dictionary;
    }

    public String getMessage(String locale, String key) {
        Dictionary dictionary = this.getDictionary();
        if (dictionary == null)
            return CommandLibMessage.getDefaultDictionary().getMessage(locale, key);

        return dictionary.getMessage(locale, key);
    }

    public BaseComponent[] getLabel(String label) {
        final int labelLength = 52;

        if (label == null)
            label = "";

        ComponentBuilder componentBuilder = new ComponentBuilder();

        StringBuilder stringBuilderSymbol = new StringBuilder();
        int symbolLength = ((labelLength - label.length()) / 2);

        for (int i = 0; i < symbolLength; ++i) {
            stringBuilderSymbol.append("-");
        }

        String symbol = stringBuilderSymbol.toString();

        componentBuilder.append(String.format("%s ", symbol)).color(ChatColor.YELLOW);
        componentBuilder.append(label).color(ChatColor.WHITE);

        if (((labelLength - label.length()) % 2) == 1)
            componentBuilder.append(" -" + symbol).color(ChatColor.YELLOW);

        else
            componentBuilder.append(" " + symbol).color(ChatColor.YELLOW);

        return componentBuilder.create();
    }
}