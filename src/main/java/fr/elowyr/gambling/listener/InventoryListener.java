package fr.elowyr.gambling.listener;

import fr.elowyr.gambling.Gambling;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class InventoryListener implements Listener {

    private Gambling gambling;

    public InventoryListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }
        if (event.getInventory().getName().equalsIgnoreCase(this.gambling.getInventoryManager().inventoryName(gambling.getConfigManager().getString("GAMBLING-MENU.INVENTORY-NAME")))) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                Player target = Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()
                        .replace(this.gambling.getConfigManager().getString("GAMBLING-MENU.SKULL.NAME-COLOR"),
                                ""));

                if (player == target) {
                    player.sendMessage(this.gambling.getConfigManager().getString("CANNOT-DUEL-YOURSELF"));
                    player.closeInventory();
                    return;
                }

                if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
                    player.sendMessage(this.gambling.getConfigManager().getString("ALREADY-GAMBLING"));
                    player.closeInventory();
                    return;
                }
                gambling.getInventoryManager().openViewInventory(player, target);
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-MENU.KIT.MATERIAL"))) {
                Inventory inventory = Bukkit.createInventory(
                        null, this.gambling.getKitConfig().getInt("KIT-MENU.INVENTORY-SIZE"),
                        ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME")));

                this.gambling.getKitManager().openKitsMenu(inventory, player);

            }
        }
    }

    @EventHandler
    public void onStartDuel(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null) {
            return;
        }

        if (event.getInventory().getName().equalsIgnoreCase(this.gambling.getInventoryManager()
                .inventoryName(gambling.getConfigManager().getString("GAMBLING-VIEW.INVENTORY-NAME")))) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType()
                    == Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-VIEW.START.MATERIAL"))) {
                event.setCancelled(true);
                Player target = Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()
                        .replace(this.gambling.getConfigManager().getString("GAMBLING-VIEW.START.PLAYER-NAME-COLOR"),
                                ""));

                String kitName = this.gambling.getMatchManager().getPlayerWaitMap().get(target).getKitName();

                if (this.gambling.getMatchManager().getPlayerWaitMap().get(target).isUseMoney()) {
                    double money = this.gambling.getMatchManager().getPlayerWaitMap().get(target).getMoney();
                    if (this.gambling.getEconomy().getBalance(player) < money) {
                        player.sendMessage(this.gambling.getConfigManager().getString("DONT-HAVE-ENOUGHT-MONEY"));
                        player.closeInventory();
                        return;
                    }

                    if (this.gambling.getKitManager().getKitByName(kitName) == null) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-AVAIBLE-KIT"));
                        return;
                    }
                    this.startWithMoney(player, target, money, kitName);

                } else {
                    ItemStack itemStack = this.gambling.getMatchManager().getPlayerWaitMap().get(target).getItemStack();

                    int amount = 0;
                    ItemStack playerItem = null;
                    Map<Enchantment, Integer> enchants = new HashMap<>();

                    for (Map.Entry<Integer, ? extends ItemStack> set : player.getInventory().all(itemStack.getType()).entrySet()) {
                        amount += set.getValue().getAmount();
                        playerItem = set.getValue();
                        set.getValue().getEnchantments().forEach((enchants::put));
                    }

                    int difference = this.gambling.getConfigManager().getInt("DURABILITY-DIFFERENCE");

                    if (playerItem != null) {

                        int playerDurability = playerItem.getDurability();
                        int targetDurabiltity = itemStack.getDurability();
                        int durabilityMoy = playerDurability - targetDurabiltity;

                        if (durabilityMoy > difference) {
                            player.closeInventory();
                            player.sendMessage(this.gambling.getConfigManager().getString("DIFFERENCE").replace("<item>", itemStack.toString()));
                            return;
                        }
                        if (enchants.size() > 0) {
                            if (enchants == itemStack.getEnchantments()) {

                                if (amount > itemStack.getAmount()) {
                                    playerItem.setAmount(playerItem.getAmount() - itemStack.getAmount());
                                } else if (amount == itemStack.getAmount()) {
                                    player.getInventory().remove(itemStack.getType());
                                } else if (amount < itemStack.getAmount()) {
                                    player.sendMessage(this.gambling.getConfigManager().getString("DONT-HAVE-ANY").replace("<item>", itemStack.getType().name()));
                                    player.closeInventory();
                                    return;
                                }


                                startWithItem(player, target, itemStack, kitName);
                            } else {
                                player.sendMessage(this.gambling.getConfigManager().getString("ITEM-NOT-SAME-ENCHANT").replace("<item>", itemStack.getType().name()));
                                player.closeInventory();
                            }
                        } else {
                            if (amount > itemStack.getAmount()) {
                                playerItem.setAmount(playerItem.getAmount() - itemStack.getAmount());
                            } else if (amount == itemStack.getAmount()) {
                                player.getInventory().remove(itemStack.getType());
                            } else if (amount < itemStack.getAmount()) {
                                player.sendMessage(this.gambling.getConfigManager().getString("DONT-HAVE-ANY").replace("<item>", itemStack.getType().name()));
                                player.closeInventory();
                                return;
                            }
                            startWithItem(player, target, itemStack, kitName);
                        }
                    } else {
                        player.sendMessage(this.gambling.getConfigManager().getString("DONT-HAVE-ITEM").replace("<item>", itemStack.getType().name()));
                        player.closeInventory();
                    }
                }
            }
        }
    }

    private void startWithMoney(Player player, Player target, double money, String kitName) {
        this.gambling.getEconomy().withdrawPlayer(player, money);
        this.gambling.getMatchManager().startMatch(player, target);

        this.gambling.getGamblingManager().putDataWithMoney(target, kitName, money);
        this.gambling.getGamblingManager().putDataWithMoney(player, kitName, money);

        this.gambling.getMatchManager().getPlayerWaitMap().remove(target);

        this.gambling.getKitManager().giveKit(kitName, player);
        this.gambling.getKitManager().giveKit(kitName, target);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 52525, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 52525, 1));
    }

    private void startWithItem(Player player, Player target, ItemStack itemStack, String kitName) {

        this.gambling.getMatchManager().startMatch(player, target);
        this.gambling.getGamblingManager().putDataWithItemStack(target, kitName, itemStack);
        this.gambling.getGamblingManager().putDataWithItemStack(player, kitName, itemStack);

        this.gambling.getMatchManager().getPlayerWaitMap().remove(target);

        this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> {
            this.gambling.getKitManager().giveKit(kitName, player);
            this.gambling.getKitManager().giveKit(kitName, target);
        }, 20L);
    }
}
