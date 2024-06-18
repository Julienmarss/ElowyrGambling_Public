package fr.elowyr.gambling.inventory;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.util.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private Gambling gambling;

    public InventoryManager(Gambling gambling) {
        this.gambling = gambling;
    }

    private int inventorySize(int direction) {
        return direction;
    }

    public String inventoryName(String direction) {
        return direction;
    }

    private ItemStack itemConfig(List<String> lore, Material material, String name) {
        return new ItemBuilder(material).setName(name).setLore(lore).toItemStack();
    }

    private ItemStack glassItem(int direction, String name) {
        return new ItemBuilder(Material.STAINED_GLASS_PANE,
                1, (byte) direction)
                .setName(name)
                .toItemStack();
    }


    private int informationSlot(int slot) {
        return slot;
    }

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null,
                inventorySize(gambling.getConfig().getInt("GAMBLING-MENU.INVENTORY-SIZE")),
                inventoryName(gambling.getConfigManager().getString("GAMBLING-MENU.INVENTORY-NAME")));

        new BukkitRunnable() {
            @Override
            public void run() {
                inventory.clear();

                for (String values : gambling.getConfigManager().getStringList("GAMBLING-MENU.GLASS")) {
                    inventory.setItem(Integer.parseInt(values), glassItem(gambling.getConfig().getInt("GAMBLING-MENU.GLASS-DATA"),
                            gambling.getConfigManager().getString("GAMBLING-MENU.GLASS-NAME")));
                }

                int slot = gambling.getConfigManager().getInt("GAMBLING-MENU.KIT.SLOT");
                Material material = Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-MENU.KIT.MATERIAL"));
                String name = gambling.getConfigManager().getString("GAMBLING-MENU.KIT.NAME");
                List<String> kitLore = new ArrayList<>(gambling.getConfigManager().getStringList("GAMBLING-MENU.KIT.LORE"));

                inventory.setItem(slot, new ItemBuilder(material).setName(name).setLore(kitLore).toItemStack());

                gambling.getMatchManager().getPlayerWaitMap().forEach((players, playerData)
                        -> {
                    ItemStack itemStackSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta skullMeta = (SkullMeta) itemStackSkull.getItemMeta();
                    skullMeta.setDisplayName(gambling.getConfigManager().getString("GAMBLING-MENU.SKULL.NAME-COLOR") + players.getName());
                    List<String> lore = new ArrayList<>();

                    gambling.getConfigManager().getStringList("GAMBLING-MENU.SKULL.LORE").forEach(line
                            -> lore.add(line.replace("<kit>", playerData.getKitName())));

                    skullMeta.setLore(lore);
                    skullMeta.setOwner(players.getName());
                    itemStackSkull.setItemMeta(skullMeta);
                    inventory.addItem(itemStackSkull);
                });
            }
        }.runTaskTimerAsynchronously(gambling, 1L, 5L);

        player.openInventory(inventory);
    }

    public void openViewInventory(Player player, Player target) {
        Inventory inventory = Bukkit.createInventory(null,
                inventorySize(gambling.getConfig().getInt("GAMBLING-VIEW.INVENTORY-SIZE")),
                inventoryName(gambling.getConfigManager().getString("GAMBLING-VIEW.INVENTORY-NAME")));

        new BukkitRunnable() {
            @Override
            public void run() {
                inventory.clear();
                if (!gambling.getMatchManager().getPlayerWaitMap().containsKey(target)) {
                    player.closeInventory();
                    this.cancel();
                    return;
                }

                for (String values : gambling.getConfigManager().getStringList("GAMBLING-VIEW.GLASS")) {
                    inventory.setItem(Integer.parseInt(values), glassItem(gambling.getConfig().getInt("GAMBLING-VIEW.GLASS-DATA"),
                            gambling.getConfigManager().getString("GAMBLING-VIEW.GLASS-NAME")));
                }

                List<String> l = gambling.getConfigManager().getStringList("GAMBLING-VIEW.START.LORE");
                List<String> lore = gambling.getConfigManager().getStringList("GAMBLING-VIEW.PREVIEW.MONEY.LORE");

                inventory.setItem(informationSlot(gambling.getConfig().getInt("GAMBLING-VIEW.START.SLOT")), itemConfig(l,
                        Material.getMaterial(gambling.getConfig().getString("GAMBLING-VIEW.START.MATERIAL")),
                        gambling.getConfigManager().getString("GAMBLING-VIEW.START.PLAYER-NAME-COLOR") + target.getName()));

                if (gambling.getMatchManager().getPlayerWaitMap().get(target).isUseMoney()) {
                    inventory.setItem(informationSlot(gambling.getConfig().getInt("GAMBLING-VIEW.PREVIEW.MONEY.SLOT")),
                            itemConfig(lore,
                                    Material.getMaterial(gambling.getConfig().getString("GAMBLING-VIEW.PREVIEW.MONEY.MATERIAL")),
                                    gambling.getConfigManager().getString("GAMBLING-VIEW.PREVIEW.MONEY.NAME-COLOR") + gambling.getMatchManager().getPlayerWaitMap().get(target).getMoney()));
                } else {
                    ItemStack itemStack = gambling.getMatchManager().getPlayerWaitMap().get(target).getItemStack();
                    inventory.setItem(informationSlot(gambling.getConfig().getInt("GAMBLING-VIEW.PREVIEW.MONEY.SLOT")), itemStack);
                }
            }
        }.runTaskTimerAsynchronously(this.gambling, 1L, 5L);

        player.openInventory(inventory);
    }

    public void openMainMenu(Player player) {

    }
}
