package fr.elowyr.gambling.listener;

import fr.elowyr.gambling.Gambling;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClaimListener implements Listener {

    private final Gambling gambling;

    public ClaimListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getInventory().getName().equalsIgnoreCase(gambling.getConfigManager().getString("GAMBLING-CLAIM.INVENTORY-NAME"))) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta())
                return;

            event.getWhoClicked().closeInventory();
        }
    }
}
