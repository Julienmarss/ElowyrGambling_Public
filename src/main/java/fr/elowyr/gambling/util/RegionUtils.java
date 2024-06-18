package fr.elowyr.gambling.util;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RegionUtils {

    public static boolean inArea(Location location, World world, String region) {
        if (!location.getWorld().getName().equals(world.getName())) {
            return false; // Avoid unnecessary checks if the worlds are different
        }
        RegionContainer regionContainer = WorldGuardPlugin.inst().getRegionContainer();
        if (regionContainer != null) {
            RegionManager regionManager = regionContainer.get(world);
            if (regionManager != null) {
                ProtectedRegion protectedRegion = regionManager.getRegion(region);
                return protectedRegion != null && protectedRegion.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            }
        }
        return false;
    }

    public static boolean inAreas(Location location, World world, String... regions) {
        if (!location.getWorld().getName().equals(world.getName())) {
            return false; // Avoid unnecessary checks if the worlds are different
        }
        RegionContainer regionContainer = WorldGuardPlugin.inst().getRegionContainer();
        if (regionContainer != null) {
            RegionManager regionManager = regionContainer.get(world);
            if (regionManager != null) {
                for (String region : regions) {
                    ProtectedRegion protectedRegion = regionManager.getRegion(region);
                    if (protectedRegion != null && protectedRegion.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getPlayerCountInRegion(Player player, String regionName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (plugin instanceof WorldGuardPlugin) {
            WorldGuardPlugin worldGuard = (WorldGuardPlugin) plugin;
            ProtectedRegion region = worldGuard.getRegionManager(player.getWorld()).getRegion(regionName);

            if (region != null) {
                int count = 0;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (region.contains(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ())) {
                        count++;
                    }
                }

                return count;
            } else {
                // La région n'existe pas
                return -1;
            }
        } else {
            // WorldGuard n'est pas installé ou activé
            return -1;
        }
    }
}
