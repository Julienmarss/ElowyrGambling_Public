package fr.elowyr.gambling.util.worldguard.listener;

import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldguard.protection.managers.*;
import com.sk89q.worldguard.protection.regions.*;
import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.util.worldguard.events.RegionEnterEvent;
import fr.elowyr.gambling.util.worldguard.events.RegionEnteredEvent;
import fr.elowyr.gambling.util.worldguard.events.RegionLeaveEvent;
import fr.elowyr.gambling.util.worldguard.events.RegionLeftEvent;
import fr.elowyr.gambling.util.worldguard.movement.MovementWay;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.util.*;

public class WGRegionEventsListener implements Listener
{
    private WorldGuardPlugin wgPlugin;
    private Gambling plugin;
    private Map<Player, Set<ProtectedRegion>> playerRegions;
    
    public WGRegionEventsListener(Gambling plugin, WorldGuardPlugin wgPlugin) {
        this.plugin = plugin;
        this.wgPlugin = wgPlugin;
        this.playerRegions = new HashMap<>();
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        final Set<ProtectedRegion> regions = this.playerRegions.remove(e.getPlayer());
        if (regions != null) {
            for (final ProtectedRegion region : regions) {
                final RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                final RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                Gambling.getInstance().getServer().getPluginManager().callEvent(leaveEvent);
                Gambling.getInstance().getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Set<ProtectedRegion> regions = this.playerRegions.remove(e.getPlayer());
        if (regions != null) {
            for (final ProtectedRegion region : regions) {
                final RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                final RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                Gambling.getInstance().getServer().getPluginManager().callEvent(leaveEvent);
                Gambling.getInstance().getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent e) {
        e.setCancelled(this.updateRegions(e.getPlayer(), MovementWay.MOVE, e.getTo(), e));
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        this.updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getPlayer().getLocation(), e);
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        this.updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getRespawnLocation(), e);
    }
    
    private synchronized boolean updateRegions(final Player player, final MovementWay movement, final Location to, final PlayerEvent event) {
        Set<ProtectedRegion> regions;
        if (this.playerRegions.get(player) == null) {
            regions = new HashSet<>();
        }
        else {
            regions = new HashSet<>(this.playerRegions.get(player));
        }
        final Set<ProtectedRegion> oldRegions = new HashSet<>(regions);
        final RegionManager rm = this.wgPlugin.getRegionManager(to.getWorld());
        if (rm == null) {
            return false;
        }
        final HashSet<ProtectedRegion> appRegions = new HashSet<>(rm.getApplicableRegions(to).getRegions());
        final ProtectedRegion globalRegion = rm.getRegion("__global__");
        if (globalRegion != null) {
            appRegions.add(globalRegion);
        }
        for (final ProtectedRegion region : appRegions) {
            if (!regions.contains(region)) {
                final RegionEnterEvent e = new RegionEnterEvent(region, player, movement, event);
                Gambling.getInstance().getServer().getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                Bukkit.getScheduler().runTaskLater(Gambling.getInstance(), () -> {
                    final RegionEnteredEvent e1 = new RegionEnteredEvent(region, player, movement, event);
                    Gambling.getInstance().getServer().getPluginManager().callEvent( e1);
                }, 1L);
                regions.add(region);
            }
        }
        final Iterator<ProtectedRegion> itr = regions.iterator();
        while (itr.hasNext()) {
            final ProtectedRegion region = itr.next();
            if (!appRegions.contains(region)) {
                if (rm.getRegion(region.getId()) != region) {
                    itr.remove();
                }
                else {
                    final RegionLeaveEvent e2 = new RegionLeaveEvent(region, player, movement, event);
                    Gambling.getInstance().getServer().getPluginManager().callEvent((Event)e2);
                    if (e2.isCancelled()) {
                        regions.clear();
                        regions.addAll(oldRegions);
                        return true;
                    }
                    Bukkit.getScheduler().runTaskLater(Gambling.getInstance(), () -> {
                        final RegionLeftEvent e = new RegionLeftEvent(region, player, movement, event);
                        Gambling.getInstance().getServer().getPluginManager().callEvent(e);
                    }, 1L);
                    itr.remove();
                }
            }
        }
        this.playerRegions.put(player, regions);
        return false;
    }
}
