package fr.elowyr.gambling.commands;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.util.command.ACommand;
import fr.elowyr.gambling.inventory.GamblingInventory;
import fr.elowyr.gambling.util.title.TitleUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GamblingCommand extends ACommand {

    private Gambling gambling;

    public GamblingCommand(Gambling gambling) {
        super(gambling, "gambling", "command.gambling", false);
        this.gambling = gambling;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        List<String> helpMessage = this.gambling.getConfigManager().getStringList("GAMBLING-HELP");
        List<String> helpAdmin = this.gambling.getConfigManager().getStringList("GAMBLING-HELP-ADMIN");
        GamblingInventory gamblingInventory = new GamblingInventory();

        if (args.length == 0) {
            if (this.gambling.getConfigManager().getBoolean("WORLDGUARD-GAMBLING-COMMAND-ACTIVE")) {
                if (this.isRegionProtected(player.getLocation())) {
                    gamblingInventory.openMainInventory(player, this.gambling);
                } else {
                    player.sendMessage(this.gambling.getConfigManager().getString("CANNOT-DO-COMMAND-HERE"));
                }
                return true;
            } else {
                gamblingInventory.openMainInventory(player, this.gambling);
            }
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                if (player.hasPermission("command.gambling.help")) {
                    for (String message : helpMessage) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("admin")) {
                if (player.hasPermission("command.gambling.admin")) {
                    for (String message : helpAdmin) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("leave")) {
                if (!this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
                    player.sendMessage(this.gambling.getConfigManager().getString("NOT-IN-GAMBLING"));
                    return true;
                } else {
                    if (this.gambling.getMatchManager().getPlayerWaitMap().get(player).isUseMoney()) {
                        double money = this.gambling.getMatchManager().getPlayerWaitMap().get(player).getMoney();
                        this.gambling.getEconomy().depositPlayer(player, money);
                    } else {
                        ItemStack itemStack = this.gambling.getMatchManager().getPlayerWaitMap().get(player).getItemStack();
                        player.getInventory().addItem(itemStack);

                    }

                    this.gambling.getMatchManager().getPlayerWaitMap().remove(player);
                    player.sendMessage(this.gambling.getConfigManager().getString("LEAVE-GAMBLING"));
                }
                return true;
            }
            for (String message : helpMessage) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }

        if (args.length == 3) {

            int gamblingSize = this.gambling.getMatchManager().getMaxGambling();
            if (this.gambling.getMatchManager().getPlayerWaitMap().size() >= gamblingSize) {
                player.sendMessage(this.gambling.getConfigManager().getString("GAMBLING-MAX"));
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {

                String kitName = args[1];
                if (this.gambling.getKitManager().getKitByName(kitName) != null
                        && this.gambling.getKitManager().getIcon(kitName).getType() != Material.REDSTONE_BLOCK) {

                    try {
                        double money = Integer.parseInt(args[2]);

                        this.gambling.getEconomy().withdrawPlayer(player, money);
                        this.gambling.getMatchManager().putInWaitWithMoney(player, kitName, money);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 4F, 4F);
                        TitleUtils.sendTitle(player,
                                this.gambling.getConfigManager().getString("GAMBLING-TITLE"),
                                this.gambling.getConfigManager().getString("GAMBLING-SUBTITLE"),
                                20, 20, 40);
                    } catch (NumberFormatException exception) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-NUMBER"));
                    }
                } else {
                    player.sendMessage(this.gambling.getConfigManager().getString("KIT-NOT-EXIST"));
                    return true;
                }
            }
        }
        return true;
    }

    public boolean isRegionProtected(Location location) {

        WorldGuardPlugin worldGuard = this.gambling.getWorldGuardPlugin();
        RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);
        if (regions.size() == 0) {
            return false;
        } else {
            for (ProtectedRegion region : regions) {
                for (String protectedregion : this.gambling.getConfigManager().getStringList("WORLDGUARD-GAMBLING-COMMAND")) {
                    if (region.getId().equalsIgnoreCase(protectedregion)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
