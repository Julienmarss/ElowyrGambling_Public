package fr.elowyr.gambling;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.trophonix.tradeplus.TradePlus;
import fr.elowyr.gambling.commands.GArenaCommand;
import fr.elowyr.gambling.listener.*;
import fr.elowyr.gambling.restrictions.AllowedManager;
import fr.elowyr.gambling.commands.GamblingCommand;
import fr.elowyr.gambling.commands.GKitCommand;
import fr.elowyr.gambling.inventory.InventoryManager;
import fr.elowyr.gambling.kits.KitManager;
import fr.elowyr.gambling.manager.GamblingManager;
import fr.elowyr.gambling.match.MatchManager;
import fr.elowyr.gambling.restrictions.DisabledMaterialManager;
import fr.elowyr.gambling.save.SaveManager;
import fr.elowyr.gambling.util.config.Config;
import fr.elowyr.gambling.util.config.ConfigManager;
import fr.elowyr.gambling.util.worldguard.listener.WGRegionEventsListener;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter @Setter
public class Gambling extends JavaPlugin {
    private static @Getter Gambling instance;

    private ConsoleCommandSender commandSender;
    private ConfigManager configManager;
    private WorldGuardPlugin worldGuardPlugin;
    private InventoryManager inventoryManager;
    private MatchManager matchManager;
    private Economy economy;
    private File filePlayersStorage;
    private File kitStorage;
    private File storeItem;
    private File saveCrash;
    private File winItem;
    private KitManager kitManager;
    private GamblingManager gamblingManager;
    private AllowedManager allowedManager;
    private DisabledMaterialManager disabledMaterialManager;
    private Config arenaConfig;
    private Config kitConfig;
    private SaveManager saveManager;

    @Override
    public void onEnable() {
        instance = this;
        this.commandSender = this.getServer().getConsoleSender();
        this.saveDefaultConfig();
        this.registerWorldGuard();
        this.registerManagers();
        this.registerListeners();
        this.registerCommands();
        this.setupEconomy();

        this.getServer().getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.saveManager.loadStuffWait(player);
                this.saveManager.loadStuffMatch(player);
                this.matchManager.endMatch(player, player);
            }
        }, 5L);

    }

    @Override
    public void onDisable() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.saveManager.savePlayerFromWait(player);
            this.saveManager.savePlayerFromMatch(player);
        }
        this.kitManager.saveKits();
    }

    private void registerManagers() {

        this.arenaConfig = new Config(this, "arena");
        this.kitConfig = new Config(this, "kit");

        this.filePlayersStorage = new File(this.getDataFolder(), "players");
        if (!this.filePlayersStorage.exists())
            this.filePlayersStorage.mkdir();

        this.kitStorage = new File(this.getDataFolder(), "kits");
        if (!this.kitStorage.exists())
            this.kitStorage.mkdir();

        this.storeItem = new File(this.getDataFolder(), "items");
        if (!this.storeItem.exists())
            this.storeItem.mkdir();

        this.saveCrash = new File(this.getDataFolder(), "saves");
        if (!this.saveCrash.exists())
            this.saveCrash.mkdir();

        this.winItem = new File(this.getDataFolder(), "win");
        if (!this.winItem.exists())
            this.winItem.mkdir();

        this.configManager = new ConfigManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.matchManager = new MatchManager(this);
        this.kitManager = new KitManager(this);
        this.gamblingManager = new GamblingManager(this);
        this.allowedManager = new AllowedManager(this);
        this.disabledMaterialManager = new DisabledMaterialManager(this);
        this.saveManager = new SaveManager(this);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GamblingListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CommandsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WGRegionEventsListener(this, this.worldGuardPlugin), this);
        this.getServer().getPluginManager().registerEvents(new RegionListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GArenaListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GKitListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ClaimListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GamblingInventoryListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CreateGamblingListener(this), this);
        if (this.getServer().getPluginManager().getPlugin("CombatTagPlus") != null) {
            this.getServer().getPluginManager().registerEvents(new CombatTagListener(this), this);
        }

        if (this.getServer().getPluginManager().getPlugin(this.getConfig().getString("FACTIONS-NAME")) != null) {
            this.getServer().getPluginManager().registerEvents(new FactionListener(this), this);
        }
    }

    public TradePlus getTradePlus() {
        return (TradePlus) Bukkit.getPluginManager().getPlugin("TradePlus");
    }

    private void registerCommands() {
        new GamblingCommand(this);
        new GKitCommand(this);
        new GArenaCommand(this);
    }

    private void registerWorldGuard() {
        this.worldGuardPlugin = this.getWGPlugin();
        String state = this.worldGuardPlugin == null ?
                "&cWORLDGUARD ISN'T DETECTED. PLEASE ADD WORLDGUARD IN YOUR PLUGINS LIST" :
                "&aWorldGuard detected successfuly. Enabling plugin.";

        String factionState = this.getServer().getPluginManager().getPlugin(
                this.getConfig().getString("FACTIONS-NAME")) == null ?
                "&cFACTIONS ISN'T DETECTED. PLEASE ADD FACTIONS IN YOUR PLUGINS LIST"
                : "&aFactions detected successfuly. Enabling plugin.";

        String vaultState = this.getServer().getPluginManager().getPlugin("Vault") == null ?
                "&cVAULT ISN'T DETECTED. PLEASE ADD FACTIONS IN YOUR PLUGINS LIST"
                : "&aVault detected successfuly. Enabling plugin.";

        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "   _____                 _     _ _             "));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  / ____|               | |   | (_)            "));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', " | |  __  __ _ _ __ ___ | |__ | |_ _ __   __ _ "));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', " | | |_ |/ _` | '_ ` _ \\| '_ \\| | | '_ \\ / _` |"));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', " | |__| | (_| | | | | | | |_) | | | | | | (_| |"));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  \\_____|\\__,_|_| |_| |_|_.__/|_|_|_| |_|\\__, |"));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "                                          __/ |"));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "                                         |___/"));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a============================"));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEnabled Gambling"));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fAuthor: &aSYRQL"));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', state));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', factionState));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', vaultState));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
        this.commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a============================"));
        if (worldGuardPlugin == null
                || this.getServer().getPluginManager().getPlugin("Vault") == null
                || this.getServer().getPluginManager().getPlugin(this.getConfig().getString("FACTIONS-NAME")) == null)
            this.getServer().getPluginManager().disablePlugin(this);
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    private WorldGuardPlugin getWGPlugin() {
        final Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider =
                Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
        }
    }
}
