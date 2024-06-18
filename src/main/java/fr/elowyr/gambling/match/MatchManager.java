package fr.elowyr.gambling.match;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.data.PlayerData;
import fr.elowyr.gambling.util.ParseLoc;
import fr.elowyr.gambling.util.config.Base64Save;
import fr.elowyr.gambling.util.save.InventoryUtils;
import fr.elowyr.gambling.util.save.PlayerInv;
import fr.elowyr.gambling.util.config.Config;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {

    private final Gambling gambling;
    private final ConcurrentHashMap<Player, Player> playerMatchMap;
    private final ConcurrentHashMap<Player, PlayerData> playerWaitMap;
    private final Map<UUID, PlayerInv> playerInventory;
    private final int maxGambling;
    private final HashMap<String, Location> playerLocation;
    private final List<Player> hidePlayer;

    public MatchManager(Gambling gambling) {
        this.gambling = gambling;
        this.playerMatchMap = new ConcurrentHashMap<>();
        this.playerWaitMap = new ConcurrentHashMap<>();
        this.playerInventory = new HashMap<>();
        this.maxGambling = this.gambling.getConfigManager().getInt("GAMBLING-MAX-PLAYERS");
        this.playerLocation = new HashMap<>();
        this.hidePlayer = new ArrayList<>();
    }

    public void messageStartDuel(Player player) {
        player.sendMessage(this.gambling.getConfigManager().getString("GAMBLING-START")
                .replace("<target>", this.playerMatchMap.get(player).getName()));
    }

    public void messageEndDuel(Player winner, Player loser) {
        List<String> message = this.gambling.getConfigManager().getStringList("GAMBLING-END");
        for (String mess : message) {
            winner.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    mess.replace("<winner>", winner.getName()).replace("<loser>", loser.getName())));

            loser.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    mess.replace("<winner>", winner.getName()).replace("<loser>", loser.getName())));
        }
    }

    public void startMatch(Player player, Player target) {

        this.putInMatch(player, target);

        this.preparePlayerToFight(player);
        this.preparePlayerToFight(target);

        this.playerLocation.put(player.getName(), player.getLocation());
        this.playerLocation.put(target.getName(), target.getLocation());

        Location pos1 = ParseLoc.getParseLoc(
                this.gambling.getArenaConfig().getString("ARENA.WORLD"),
                this.gambling.getArenaConfig().getString("ARENA.POS1"));

        Location pos2 = ParseLoc.getParseLoc(
                this.gambling.getArenaConfig().getString("ARENA.WORLD"),
                this.gambling.getArenaConfig().getString("ARENA.POS2"));

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players != this.getPlayerMatchMap().get(player)) {
                player.hidePlayer(players);
                this.hidePlayer.add(players);
            }
            if (players != this.getPlayerMatchMap().get(target)) {
                target.hidePlayer(players);
                this.hidePlayer.add(players);
            }
        }
        player.teleport(pos1);
        target.teleport(pos2);
    }

    public void endMatch(Player winner, Player loser) {
        this.removeFromMatch(winner);
        this.removeFromMatch(loser);
        winner.closeInventory();
        loser.closeInventory();

        this.messageEndDuel(winner, loser);

        this.clearInventory(winner);
        if (this.hasPreviousInventory(winner)) {
            loadInventory(winner);
        }

        this.clearInventory(loser);
        if (this.hasPreviousInventory(loser)) {
            loadInventory(loser);
        }
        if (playerLocation.containsKey(winner.getName())) {
            winner.teleport(playerLocation.get(winner.getName()));
            playerLocation.remove(winner.getName());
        } else {
            Location location = winner.getLocation().getWorld().getSpawnLocation();
            winner.teleport(location.add(0, 0, 5));
        }
        if (playerLocation.containsKey(loser.getName())) {
            loser.teleport(playerLocation.get(loser.getName()));
            playerLocation.remove(loser.getName());
        } else {
            Location location = winner.getLocation().getWorld().getSpawnLocation();
            loser.teleport(location.add(0, 0, 5));
        }

        if (gambling.getGamblingManager().getPlayerDataHashMap().get(winner).isUseMoney()) {
            double money = gambling.getGamblingManager().getPlayerDataHashMap().get(winner).getMoney();

            gambling.getEconomy().depositPlayer(winner, money * this.gambling.getConfig().getInt("MULTIPLICATOR"));
        } else {
            ItemStack itemStack = gambling.getGamblingManager().getPlayerDataHashMap().get(winner).getItemStack();

            for (int i = 0; i < this.gambling.getConfig().getInt("MULTIPLICATOR"); i++) {
                winner.getInventory().setItem(i, itemStack);
            }
        }
        gambling.getGamblingManager().removeFromMap(winner);
        gambling.getGamblingManager().removeFromMap(loser);

        winner.getActivePotionEffects().forEach(potionEffect -> winner.removePotionEffect(potionEffect.getType()));
        loser.getActivePotionEffects().forEach(potionEffect -> loser.removePotionEffect(potionEffect.getType()));

        winner.setHealth(20);
        loser.setHealth(20);

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players != loser) {
                winner.showPlayer(players);
                this.hidePlayer.remove(players);
            }
            if (players != winner) {
                loser.showPlayer(players);
                this.hidePlayer.remove(players);
            }

            if (players != winner) players.showPlayer(winner);
            if (players != loser) players.showPlayer(loser);
        }
    }

    public void endConnexion(Player player, Config config) {
        clearInventory(player);
        loadStuffFromConfig(player, config);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    public void putInMatch(Player player, Player target) {
        this.playerMatchMap.put(player, target);
        this.playerMatchMap.put(target, player);
    }

    public void removeFromMatch(Player player) {
        this.playerMatchMap.remove(player);
    }

    public void putInWaitWithMoney(Player player, String kitName, Double money) {
        this.playerWaitMap.put(player, new PlayerData(player.getName(), kitName, money));
    }

    public void putInWaitWithItemStack(Player player, String kitName, ItemStack itemStack) {
        this.playerWaitMap.put(player, new PlayerData(player.getName(), kitName, itemStack));
    }

    public void saveInventory(Player player) {
        this.playerInventory.put(player.getUniqueId(), InventoryUtils.playerInventoryFromPlayer2(player));
    }

    public void loadInventory(Player player) {
        player.getInventory().setContents(this.playerInventory.get(player.getUniqueId()).getContents());
        player.getInventory().setArmorContents(this.playerInventory.get(player.getUniqueId()).getArmorContents());
        player.updateInventory();
        this.playerInventory.remove(player.getUniqueId());
    }

    public void loadStuffFromConfig(Player player, Config config) {
        try {
            ItemStack[] armor = Base64Save.itemStackArrayFromBase64(config.getString("armor"));
            ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(config.getString("stuff"));
            player.getInventory().setArmorContents(armor);
            player.getInventory().setContents(stuff);
            player.updateInventory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPreviousInventory(Player player) {
        return this.playerInventory.containsKey(player.getUniqueId());
    }

    public void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR)});
    }

    public void preparePlayerToFight(Player player) {

        this.messageStartDuel(player);
        this.saveInventory(player);
        this.clearInventory(player);
        player.closeInventory();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);

    }

    public ConcurrentHashMap<Player, Player> getPlayerMatchMap() {
        return playerMatchMap;
    }

    public ConcurrentHashMap<Player, PlayerData> getPlayerWaitMap() {
        return playerWaitMap;
    }

    public int getMaxGambling() {
        return maxGambling;
    }

    public Map<UUID, PlayerInv> getPlayerInventory() {
        return playerInventory;
    }

    public List<Player> getHidePlayer() {
        return hidePlayer;
    }
}
