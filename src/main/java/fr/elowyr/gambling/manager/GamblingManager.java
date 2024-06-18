package fr.elowyr.gambling.manager;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GamblingManager {

    private Gambling gambling;
    private final HashMap<Player, PlayerData> playerDataHashMap;
    private final List<Player> playerMoneyWait;
    private final HashMap<Player, Integer> playerMoney;

    public GamblingManager(Gambling gambling) {
        this.gambling = gambling;
        this.playerDataHashMap = new HashMap<>();
        this.playerMoneyWait = new ArrayList<>();
        this.playerMoney = new HashMap<>();
    }

    public void putDataWithMoney(Player player, String kitName, double money) {
        this.playerDataHashMap.put(player, new PlayerData(player.getName(), kitName, money));
    }

    public void addPlayerMoney(Player player) {
        this.playerMoneyWait.add(player);
    }

    public void removePlayerMoney(Player player) {
        this.playerMoneyWait.remove(player);
    }


    public void putDataWithItemStack(Player player, String kitName, ItemStack itemStack) {
        this.playerDataHashMap.put(player, new PlayerData(player.getName(), kitName, itemStack));
    }

    public void removeFromMap(Player player) {
        this.playerDataHashMap.remove(player);
    }

    public HashMap<Player, PlayerData> getPlayerDataHashMap() {
        return playerDataHashMap;
    }

    public List<Player> getPlayerMoneyWait() {
        return playerMoneyWait;
    }

    public HashMap<Player, Integer> getPlayerMoney() {
        return playerMoney;
    }
}
