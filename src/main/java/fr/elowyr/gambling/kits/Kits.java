package fr.elowyr.gambling.kits;

import fr.elowyr.gambling.util.config.Config;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Kits {

    private String kitName;
    private ItemStack[] armor;
    private ItemStack[] stuff;
    private ItemStack icon;
    private final transient Config config;

    @Override
    public String toString() {
        return "Kits{" +
                "kitName='" + kitName + '\'' +
                ", armor=" + Arrays.toString(armor) +
                ", stuff=" + Arrays.toString(stuff) +
                ", icon=" + icon +
                ", config=" + config +
                '}';
    }

    public Kits(String kitName, ItemStack[] armor, ItemStack[] stuff, ItemStack icon, Config file) {
        this.kitName = kitName;
        this.armor = armor;
        this.stuff = stuff;
        this.icon = icon;
        this.config = file;
    }

    public String getKitName() {
        return kitName;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack[] getStuff() {
        return stuff;
    }

    public void setStuff(ItemStack[] stuff) {
        this.stuff = stuff;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public Config getConfig() {
        return config;
    }
}
