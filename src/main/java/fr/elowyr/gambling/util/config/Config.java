package fr.elowyr.gambling.util.config;

import fr.elowyr.gambling.Gambling;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Config extends YamlConfiguration {

    private String fileName;
    private JavaPlugin plugin;

    public Config(JavaPlugin plugin, String fileName, String folderName) {
        this(plugin, fileName, ".yml", folderName);
    }

    public Config(JavaPlugin plugin, String fileName) {
        this(plugin, fileName, "");
    }

    public Config(JavaPlugin plugin, String fileName, String fileExtension, String folderName) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile(folderName);
    }

    public String getFileName() {
        return this.fileName;
    }

    public JavaPlugin getPlugin() {
        return Gambling.getInstance();
    }

    private void createFile(String folderName) {
        File folder = new File(Gambling.getInstance().getDataFolder(), folderName);
        try {
            File file = new File(folder, this.fileName);

            if (!file.exists()) {
                if (Gambling.getInstance().getResource(this.fileName) != null) {
                    Gambling.getInstance().saveResource(this.fileName, false);
                } else {
                    this.save(file);
                }
                this.load(file);
            } else {
                this.load(file);
                this.save(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteFile(String fileName, String folderName) {
        File folder = new File(Gambling.getInstance().getDataFolder(), folderName);
        File file = new File(folder, fileName + ".yml");

        if (file.exists())
            file.delete();
    }

    public void save(String folderName) {
        File fold = new File(Gambling.getInstance().getDataFolder(), folderName);

        try {
            this.save(new File(fold, this.fileName));
        } catch (Exception ex) {
        }
    }


}