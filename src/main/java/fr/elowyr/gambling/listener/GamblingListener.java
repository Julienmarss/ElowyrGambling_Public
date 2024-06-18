package fr.elowyr.gambling.listener;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.util.RegionUtils;
import fr.elowyr.gambling.util.config.Base64Save;
import fr.elowyr.gambling.util.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class GamblingListener implements Listener {

    private Gambling gambling;
    private Config config;

    public GamblingListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDie(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        if (victim.getKiller() != null) {
            Player killer = victim.getKiller();
            if (!this.gambling.getMatchManager().getPlayerMatchMap().containsKey(victim)
                    || !this.gambling.getMatchManager().getPlayerMatchMap().containsKey(killer))
                return;

            int stat = victim.getStatistic(Statistic.DEATHS);
            int statKills = victim.getStatistic(Statistic.PLAYER_KILLS);

            victim.setStatistic(Statistic.DEATHS, Math.max((stat - 1), 0));
            killer.setStatistic(Statistic.PLAYER_KILLS, Math.max((statKills - 1), 0));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "classification give-player gambling "+ killer.getName() + " 1");
            event.getDrops().clear();
            event.setDeathMessage(null);

            this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> victim.spigot().respawn(), 5L);
            this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> this.gambling.getMatchManager()
                    .endMatch(killer, victim), 20L);
        } else {
            if (!this.gambling.getMatchManager().getPlayerMatchMap().containsKey(victim))
                return;

            Player duel = this.gambling.getMatchManager().getPlayerMatchMap().get(victim);
            event.getDrops().clear();
            this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> victim.spigot().respawn(), 5L);
            this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> this.gambling.getMatchManager()
                    .endMatch(duel, victim), 20L);
        }
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            if (event.getSlotType() == InventoryType.SlotType.CRAFTING || event.getSlotType() == InventoryType.SlotType.RESULT) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(final PlayerPickupItemEvent e) {
        Player victim = e.getPlayer();

        if (RegionUtils.inArea(e.getPlayer().getLocation(), Bukkit.getWorld("world"), "pvpgambling")) {
            e.setCancelled(true);
            e.getItem().remove();
        }
        if (victim.getKiller() != null) {
            Player killer = victim.getKiller();
            if (!this.gambling.getMatchManager().getPlayerMatchMap().containsKey(victim)
                    || !this.gambling.getMatchManager().getPlayerMatchMap().containsKey(killer))
                return;

            e.setCancelled(true);
            e.getItem().remove();

        } else {
            if (!this.gambling.getMatchManager().getPlayerMatchMap().containsKey(victim))
                return;

            e.setCancelled(true);
            e.getItem().remove();
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!this.gambling.getMatchManager().getPlayerMatchMap().containsKey(event.getPlayer())) return;
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Player players : this.gambling.getMatchManager().getHidePlayer()) {
            if (players != player) {
                players.hidePlayer(player);
            }
        }

    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
            if (this.gambling.getMatchManager().getPlayerWaitMap().get(player).isUseMoney()) {

                this.gambling.getEconomy().depositPlayer(player,
                        this.gambling.getMatchManager().getPlayerWaitMap().get(player).getMoney());
            } else {
                this.config = new Config(gambling, player.getName(), "items");

                ItemStack[] itemStacks = new ItemStack[]{this.gambling.getMatchManager().getPlayerWaitMap()
                        .get(player).getItemStack()};
                this.config.set("bet", Base64Save.itemStackArrayToBase64(itemStacks));
                this.config.save("items");
            }
            this.gambling.getMatchManager().getPlayerWaitMap().remove(player);
        }

        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            Player target = gambling.getMatchManager().getPlayerMatchMap().get(player);

            this.config = new Config(gambling, player.getName(), "players");
            this.config.set("armor", Base64Save
                    .itemStackArrayToBase64(this.gambling.getMatchManager().getPlayerInventory()
                            .get(player.getUniqueId()).getArmorContents()));
            this.config.set("stuff", Base64Save
                    .itemStackArrayToBase64(this.gambling.getMatchManager().getPlayerInventory()
                            .get(player.getUniqueId()).getContents()));

            this.config.save("players");
            this.gambling.getMatchManager().endMatch(target, player);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            event.setCancelled(true);
            player.sendMessage(this.gambling.getConfigManager().getString("CANNOT-LAUNCH-ITEM"));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.gambling.getSaveManager().loadStuffWait(player);
        this.gambling.getSaveManager().loadStuffMatch(player);
        Location location = player.getLocation().getWorld().getSpawnLocation();
        location.getChunk().unload();
        location.getChunk().load();

        File fileplayer = this.gambling.getFilePlayersStorage();
        File fileCheckPlayer = new File(fileplayer, player.getName() + ".yml");

        if (fileCheckPlayer.exists()) {
            this.config = new Config(this.gambling, player.getName(), "players");

            this.gambling.getServer().getScheduler().runTaskLaterAsynchronously(this.gambling, () -> {
                this.gambling.getMatchManager().endConnexion(player, config);
                player.teleport(location);
            }, 20L);
            this.config.deleteFile(player.getName(), "players");
        }

        File fileStuff = this.gambling.getStoreItem();
        File fileCheckStuff = new File(fileStuff, player.getName() + ".yml");

        if (fileCheckStuff.exists()) {
            this.config = new Config(this.gambling, player.getName(), "items");

            try {
                ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(config.getString("bet"));
                player.getInventory().setContents(stuff);
                player.updateInventory();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.config.deleteFile(player.getName(), "items");
        }
    }

    @EventHandler
    public void onLostFeed(FoodLevelChangeEvent event) {

        HumanEntity humanEntity = event.getEntity();

        if (humanEntity instanceof Player) {
            Player player = (Player) humanEntity;
            if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
                event.setCancelled(true);
            }
        }
    }
}