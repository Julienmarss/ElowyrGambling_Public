package fr.elowyr.gambling.listener;

import fr.elowyr.gambling.Gambling;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CreateGamblingListener implements Listener {

    private final Gambling gambling;

    public CreateGamblingListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void processCommand(PlayerCommandPreprocessEvent event) {
        if (this.gambling.getGamblingManager().getPlayerMoneyWait().contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(this.gambling.getConfigManager().getString("CANNOT-USE-COMMAND"));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.equalsIgnoreCase("annuler")) {
            if (this.gambling.getGamblingManager().getPlayerMoneyWait().contains(event.getPlayer())) {
                event.setCancelled(true);
                this.gambling.getGamblingManager().removePlayerMoney(player);
                player.sendMessage(this.gambling.getConfigManager().getString("CANCEL"));
            }
        }

        if (this.gambling.getGamblingManager().getPlayerMoneyWait().contains(event.getPlayer())) {
            event.setCancelled(true);
            try {
                int money = Integer.parseInt(message);

                int moneyMin = this.gambling.getConfigManager().getInt("MIN-MONEY");
                int moneyMax = this.gambling.getConfigManager().getInt("MAX-MONEY");

                if (money < moneyMin) {
                    player.sendMessage(this.gambling.getConfigManager().getString("SMALL-MONEY")
                            .replace("<minmoney>", String.valueOf(moneyMin))
                            .replace("<maxmoney>", String.valueOf(moneyMax)));
                    return;
                }

                if (money > moneyMax) {
                    player.sendMessage(this.gambling.getConfigManager().getString("SMALL-MONEY")
                            .replace("<minmoney>", String.valueOf(moneyMin))
                            .replace("<maxmoney>", String.valueOf(moneyMax)));
                    return;
                }

                if (this.gambling.getEconomy().getBalance(player) < money) {
                    player.sendMessage(this.gambling.getConfigManager().getString("NOT-MONEY")
                            .replace("<money>", String.valueOf(money)));
                    return;
                }

                this.gambling.getKitManager().openKitSelector(player);
                this.gambling.getGamblingManager().removePlayerMoney(player);
                this.gambling.getGamblingManager().getPlayerMoney().put(player, money);
            } catch (NumberFormatException e) {
                player.sendMessage(this.gambling.getConfigManager().getString("NOT-AVAILABLE-MONEY"));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getName().equalsIgnoreCase(gambling.getConfigManager().getString("KIT-SELECTOR.INVENTORY-NAME"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null
                    || !event.getCurrentItem().hasItemMeta()
                    || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE)
                return;
            String kitName = event.getCurrentItem().getItemMeta().getDisplayName();
            String newName = kitName.replace(ChatColor.translateAlternateColorCodes('&', this.gambling.getConfigManager().getString("KIT-SELECTOR.KIT.NAME")), "");

            if (this.gambling.getGamblingManager().getPlayerMoney().containsKey(player)) {
                player.performCommand("gambling create " + newName + " " + this.gambling.getGamblingManager().getPlayerMoney().get(player));
                player.closeInventory();
                this.gambling.getGamblingManager().getPlayerMoney().remove(player);
            }
        }
    }
}
