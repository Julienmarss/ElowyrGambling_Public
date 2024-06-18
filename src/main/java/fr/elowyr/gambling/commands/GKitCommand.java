package fr.elowyr.gambling.commands;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.kits.Kits;
import fr.elowyr.gambling.util.command.ACommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GKitCommand extends ACommand {

    private Gambling gambling;

    public GKitCommand(Gambling gambling) {
        super(gambling, "gkit", "command.gkit", false);
        this.gambling = gambling;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Inventory inventory = Bukkit.createInventory(
                null,
                this.gambling.getKitConfig().getInt("KIT-MENU.INVENTORY-SIZE"),
                ChatColor.translateAlternateColorCodes('&',
                        this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME")));

        if (args.length == 0 || args.length == 1) {
            this.gambling.getKitManager().openKitsMenu(inventory, player);
            return true;
        }
        if (args.length == 2) {

            if (player.hasPermission(this.gambling.getKitConfig().getString("ADMIN-PERM"))) {
                if (args[0].equalsIgnoreCase("create")) {
                    String kitName = args[1];
                    this.gambling.getKitManager().createKit(kitName, player);
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    String kitName = args[1];
                    Kits kits = this.gambling.getKitManager().getKitByName(kitName);

                    if (kits != null) {
                        this.gambling.getKitManager().deleteKit(player, kits);
                    } else {
                        player.sendMessage(this.gambling.getConfigManager().getString("KIT-NOT-EXIST"));
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("icon")) {
                    String kitName = args[1];
                    if (this.gambling.getKitManager().getKitByName(kitName) != null) {
                        this.gambling.getKitManager().editIcon(kitName, player);
                    }
                }
            } else {
                this.gambling.getKitManager().openKitsMenu(inventory, player);
            }

        }
        return true;
    }
}
