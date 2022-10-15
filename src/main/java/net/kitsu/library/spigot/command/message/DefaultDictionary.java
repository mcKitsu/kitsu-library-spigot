package net.kitsu.library.spigot.command.message;

import net.kitsu.localedictionary.api.Dictionary;

import java.util.List;
import java.util.Map;

public class DefaultDictionary implements net.kitsu.localedictionary.api.Dictionary{
    private final Map<String, String> defaultDictionary;

    public DefaultDictionary(Map<String, String> lang){
        this.defaultDictionary = lang;
    }

    @Override
    public String getMessage(String locale, String key) {
        if(key == null)
            key = "null";

        String result = this.defaultDictionary.get(key);
        if(result == null)
            return key;

        return result;
    }

    @Override
    public Dictionary getDictionary(List<String> list) {
        return null;
    }
}
