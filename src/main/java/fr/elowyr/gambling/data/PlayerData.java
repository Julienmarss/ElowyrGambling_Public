package fr.elowyr.gambling.data;

import org.bukkit.inventory.ItemStack;

public class PlayerData {

    private String playerName;
    private String kitName;
    private double money;
    private ItemStack itemStack;
    private boolean useMoney;

    public PlayerData(String playerName, String kitName, double money) {
        this.playerName = playerName;
        this.kitName = kitName;
        this.money = money;
        this.useMoney = true;
    }

    public PlayerData(String playerName, String kitName, ItemStack itemStack) {
        this.playerName = playerName;
        this.kitName = kitName;
        this.itemStack = itemStack;
        this.useMoney = false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getKitName() {
        return kitName;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public boolean isUseMoney() {
        return useMoney;
    }

    public void setUseMoney(boolean useMoney) {
        this.useMoney = useMoney;
    }
}
