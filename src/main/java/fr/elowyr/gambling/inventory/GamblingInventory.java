package fr.elowyr.gambling.inventory;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.util.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GamblingInventory {

    public void openMainInventory(Player player, Gambling gambling) {
        Inventory inventory = Bukkit.createInventory(null,
                gambling.getConfigManager().getInt("GAMBLING-MENU-MAIN.INVENTORY-SIZE"),
                 gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.INVENTORY-NAME"));

        List<String> listGambling = new ArrayList<>(gambling.getConfigManager().getStringList("GAMBLING-MENU-MAIN.LIST.LORE"));
        List<String> listFight = new ArrayList<>(gambling.getConfigManager().getStringList("GAMBLING-MENU-MAIN.FIGHT.LORE"));

        int data = gambling.getConfigManager().getInt("GAMBLING-MENU-MAIN.GLASS-DATA");
        String glassName = gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.GLASS-NAME");

        for (String values : gambling.getConfigManager().getStringList("GAMBLING-MENU-MAIN.GLASS")) {
            inventory.setItem(Integer.parseInt(values), new ItemBuilder(Material.STAINED_GLASS_PANE, 1,
                    (byte) data).setName(glassName).toItemStack());
        }

        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-MENU-MAIN.LIST.SLOT"),
                new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.LIST.MATERIAL")))
                        .setName(gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.LIST.NAME"))
                        .setLore(listGambling)
                        .toItemStack());

        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-MENU-MAIN.FIGHT.SLOT"),
                new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.FIGHT.MATERIAL")))
                        .setName(gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.FIGHT.NAME"))
                        .setLore(listFight)
                        .toItemStack());

        player.openInventory(inventory);
    }

    public void openFightMenu(Player player, Gambling gambling) {
        Inventory inventory = Bukkit.createInventory(null,
                gambling.getConfigManager().getInt("GAMBLING-CREATE.INVENTORY-SIZE"),
                gambling.getConfigManager().getString("GAMBLING-CREATE.INVENTORY-NAME"));

        int data = gambling.getConfigManager().getInt("GAMBLING-CREATE.GLASS-DATA");
        String glassName = gambling.getConfigManager().getString("GAMBLING-CREATE.GLASS-NAME");

        for (String values : gambling.getConfigManager().getStringList("GAMBLING-CREATE.GLASS")) {
            inventory.setItem(Integer.parseInt(values), new ItemBuilder(Material.STAINED_GLASS_PANE, 1,
                    (byte) data).setName(glassName).toItemStack());
        }

        List<String> loreMoney = new ArrayList<>(gambling.getConfigManager().getStringList("GAMBLING-CREATE.MONEY.LORE"));

        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-CREATE.MONEY.SLOT"),
                new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-CREATE.MONEY.MATERIAL")))
                        .setName(gambling.getConfigManager().getString("GAMBLING-CREATE.MONEY.NAME"))
                        .setLore(loreMoney)
                        .toItemStack());

        player.openInventory(inventory);
    }
}
