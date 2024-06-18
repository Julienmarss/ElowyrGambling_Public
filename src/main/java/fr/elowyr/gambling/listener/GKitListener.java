package fr.elowyr.gambling.listener;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.util.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GKitListener implements Listener {

    private Gambling gambling;

    public GKitListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onKitClic(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null) {
            return;
        }

        if (event.getInventory().getName().equalsIgnoreCase(this.gambling.getInventoryManager()
                .inventoryName(ChatColor.translateAlternateColorCodes('&', gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME"))))) {
            event.setCancelled(true);

            if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                event.setCancelled(true);
                return;
            }
            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getKitConfig().getString("KIT-MENU.BACK.MATERIAL"))
                    && event.getCurrentItem().hasItemMeta()
                    && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor
                    .translateAlternateColorCodes('&',
                            this.gambling.getKitConfig().getString("KIT-MENU.BACK.NAME")))) {

                this.gambling.getInventoryManager().openInventory(player);

            } else if (event.getCurrentItem().hasItemMeta()
                    && (!event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor
                    .translateAlternateColorCodes('&',
                            this.gambling.getKitConfig().getString("KIT-MENU.BACK.NAME"))))) {
                String kitName = event.getCurrentItem().getItemMeta().getDisplayName();
                String newName = kitName.replace(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.KIT.NAME")), "");
                this.openInventoryKit(newName, player, gambling.getKitManager().getKitArmor(newName), gambling.getKitManager().getKitStuff(newName));

            } else event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreviewClic(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null) {
            return;
        }

        String inventoryName = event.getInventory().getName();
        String newName = inventoryName.replace(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-VIEW.INVENTORY-NAME")), "");

        if (this.gambling.getKitManager().getKitByName(newName) != null) {
            if (!event.getCurrentItem().hasItemMeta()) {
                event.setCancelled(true);
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getKitConfig().getString("KIT-VIEW.BACK.MATERIAL"))
                    && event.getCurrentItem().hasItemMeta()
                    && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor
                    .translateAlternateColorCodes('&',
                            this.gambling.getKitConfig().getString("KIT-VIEW.BACK.NAME")))) {
                Inventory inventory = Bukkit.createInventory(null, this.gambling.getKitConfig().getInt("KIT-MENU.INVENTORY-SIZE"), ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME")));
                this.gambling.getKitManager().openKitsMenu(inventory, player);
            }
            event.setCancelled(true);
        }
    }

    public void openInventoryKit(String kitName, Player player, ItemStack[] armorContents, ItemStack[] contents) {

        Inventory inventory = Bukkit.createInventory(null,
                this.gambling.getKitConfig().getInt("KIT-VIEW.INVENTORY-SIZE"),
                ChatColor.translateAlternateColorCodes('&',
                        this.gambling.getKitConfig().getString("KIT-VIEW.INVENTORY-NAME") + kitName));
        for (ItemStack items : armorContents) {
            inventory.addItem(items);
        }

        int startPlace = 17;

        for (ItemStack items : contents) {
            startPlace += 1;
            inventory.setItem(startPlace, items);
        }

        int data = this.gambling.getKitConfig().getInt("KIT-VIEW.GLASS-DATA");
        String glassName = ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.GLASS-NAME"));

        for (String values : gambling.getKitConfig().getStringList("KIT-VIEW.GLASS")) {
            inventory.setItem(Integer.parseInt(values), new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) data).setName(glassName).toItemStack());
        }
        inventory.setItem(17, new ItemBuilder(Material.getMaterial(this.
                gambling.getKitConfig().getString("KIT-VIEW.BACK.MATERIAL")))
                .setName(ChatColor.translateAlternateColorCodes('&',
                        this.gambling.getKitConfig().getString("KIT-VIEW.BACK.NAME")))
                .toItemStack());
        player.openInventory(inventory);
    }
}
