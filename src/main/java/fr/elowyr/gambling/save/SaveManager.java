package fr.elowyr.gambling.save;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.data.PlayerData;
import fr.elowyr.gambling.util.config.Base64Save;
import fr.elowyr.gambling.util.config.Config;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class SaveManager {

    private final Gambling gambling;
    private File folder;
    private Config saveConfig;

    public SaveManager(Gambling gambling) {
        this.gambling = gambling;
        this.folder = this.gambling.getSaveCrash();
    }

    public void savePlayerFromWait(Player player) {

        if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
            this.gambling.getMatchManager().getPlayerWaitMap().forEach(((players, playerData) -> {
                this.saveConfig = new Config(this.gambling, players.getName() + "-wait", "saves");

                if (playerData.isUseMoney()) {
                    this.saveConfig.set("money", (int) playerData.getMoney());
                } else {

                    ItemStack[] itemStacks = new ItemStack[]{this.gambling.getMatchManager().getPlayerWaitMap()
                            .get(players).getItemStack()};
                    this.saveConfig.set("item", Base64Save.itemStackArrayToBase64(itemStacks));
                }
                this.saveConfig.save("saves");
            }));
        }
    }

    public void savePlayerFromMatch(Player player) {
        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            this.gambling.getMatchManager().getPlayerMatchMap().forEach(((players, player2) -> {
                this.saveConfig = new Config(this.gambling, players.getName() + "-match", "saves");

                if (this.gambling.getGamblingManager().getPlayerDataHashMap().get(players).isUseMoney()) {
                    PlayerData playerData = this.gambling.getGamblingManager().getPlayerDataHashMap().get(players);

                    this.saveConfig.set("money", playerData.getMoney());
                } else {

                    ItemStack[] itemStacks = new ItemStack[]{this.gambling.getGamblingManager().getPlayerDataHashMap()
                            .get(players).getItemStack()};
                    this.saveConfig.set("item", Base64Save.itemStackArrayToBase64(itemStacks));
                }

                this.saveConfig.set("stuff", Base64Save.itemStackArrayToBase64(this.gambling.getMatchManager().getPlayerInventory().get(players.getUniqueId()).getContents()));
                this.saveConfig.set("armor", Base64Save.itemStackArrayToBase64(this.gambling.getMatchManager().getPlayerInventory().get(players.getUniqueId()).getArmorContents()));

                this.saveConfig.save("saves");
            }));
        }

    }

    public void loadStuffWait(Player player) {
        File file = new File(folder, player.getName() + "-wait.yml");

        if (file.exists()) {
            Config config = new Config(gambling, player.getName() + "-wait", "saves");

            if (config.get("item") != null) {
                try {
                    ItemStack[] item = Base64Save.itemStackArrayFromBase64(config.getString("item"));

                    player.getInventory().addItem(item);
                    player.updateInventory();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (config.get("money") != null) {
                this.gambling.getEconomy().depositPlayer(player, config.getInt("money"));
            }
            config.deleteFile(player.getName() + "-wait", "saves");
        }
    }

    public void loadStuffMatch(Player player) {
        File file = new File(folder, player.getName() + "-match.yml");
        Location location = player.getLocation().getWorld().getSpawnLocation();

        if (file.exists()) {
            Config config = new Config(gambling, player.getName() + "-match", "saves");
            if (config.get("item") != null) {
                this.gambling.getServer().getScheduler().runTaskLaterAsynchronously(this.gambling, () -> {
                    this.endConnexion(player, config);
                    player.teleport(location);
                    try {

                        ItemStack[] item = Base64Save.itemStackArrayFromBase64(config.getString("item"));
                        player.getInventory().addItem(item);
                        player.updateInventory();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, 20L);

            } else if (config.get("money") != null) {
                this.gambling.getEconomy().depositPlayer(player, config.getDouble("money"));
                this.gambling.getServer().getScheduler().runTaskLaterAsynchronously(this.gambling, () -> {
                    this.endConnexion(player, config);
                    player.teleport(location);
                }, 20L);
            }

            config.deleteFile(player.getName() + "-match", "saves");
        }
    }

    public void endConnexion(Player player, Config config) {

        this.gambling.getMatchManager().loadStuffFromConfig(player, config);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }
}
