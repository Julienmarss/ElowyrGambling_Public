package fr.elowyr.gambling.util.config;

import fr.elowyr.gambling.Gambling;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private Gambling gambling;
    private final FileConfiguration config;

    public ConfigManager(Gambling gambling) {
        this.gambling = gambling;
        this.config = gambling.getConfig();
    }

    public String getString(String path) {
        return ChatColor.translateAlternateColorCodes('&', gambling.getConfig().getString(path));
    }

    public List<String> getStringList(String path) {

        List<String> stringList = gambling.getConfig().getStringList(path);
        ArrayList<String> toReturn = new ArrayList<>();

        stringList.forEach(line -> toReturn.add(ChatColor.translateAlternateColorCodes('&', line)));

        return toReturn;
    }

    public void setDouble(String path, double value) {
        config.set(path, value);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    private double getDouble(String path) {
        return config.getDouble(path);
    }

    public double getFloat(String path) {
        return config.getDouble(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void updateConfig() {
        gambling.saveConfig();
        gambling.reloadConfig();
    }
}

