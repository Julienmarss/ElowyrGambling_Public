package fr.elowyr.gambling.commands;

import fr.elowyr.gambling.Gambling;
import fr.elowyr.gambling.util.command.ACommand;
import fr.elowyr.gambling.util.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GArenaCommand extends ACommand {

    private Gambling gambling;

    public GArenaCommand(Gambling gambling) {
        super(gambling, "garena", "command.garena", false);
        this.gambling = gambling;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        int size = this.gambling.getArenaConfig().getInt("ARENA-MENU.INVENTORY-SIZE");
        String name = ChatColor.translateAlternateColorCodes('&',
                this.gambling.getArenaConfig().getString("ARENA-MENU.INVENTORY-NAME"));

        Inventory inventory = Bukkit.createInventory(null, size, name);

        int data = this.gambling.getArenaConfig().getInt("ARENA-MENU.GLASS-DATA");
        String glassName = ChatColor.translateAlternateColorCodes('&',
                this.gambling.getArenaConfig().getString("ARENA-MENU.GLASS-NAME"));

        for (String values : gambling.getArenaConfig().getStringList("ARENA-MENU.GLASS")) {
            inventory.setItem(Integer.parseInt(values), new ItemBuilder(Material.STAINED_GLASS_PANE, 1,
                    (byte) data).setName(glassName).toItemStack());
        }

        List<String> pos1List = new ArrayList<>();
        List<String> pos2List = new ArrayList<>();

        String firstPos = this.gambling.getArenaConfig().getString("ARENA.POS1");
        String secondPos = this.gambling.getArenaConfig().getString("ARENA.POS2");

        this.gambling.getArenaConfig().getStringList("ARENA-MENU.POS.1.LORE").forEach(line ->
                pos1List.add(ChatColor.translateAlternateColorCodes('&', line.replace("<position>", firstPos))));

        this.gambling.getArenaConfig().getStringList("ARENA-MENU.POS.2.LORE").forEach(line ->
                pos2List.add(ChatColor.translateAlternateColorCodes('&', line.replace("<position>", secondPos))));

        ItemStack pos1 = new ItemBuilder(Material
                .getMaterial(this.gambling.getArenaConfig().getString("ARENA-MENU.POS.1.MATERIAL")))
                .setName(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.POS.1.NAME")))
                .setLore(pos1List)
                .toItemStack();

        ItemStack pos2 = new ItemBuilder(Material
                .getMaterial(this.gambling.getArenaConfig().getString("ARENA-MENU.POS.2.MATERIAL")))
                .setName(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.POS.2.NAME")))
                .setLore(pos2List)
                .toItemStack();

        inventory.setItem(this.gambling.getArenaConfig().getInt("ARENA-MENU.POS.1.SLOT"), pos1);
        inventory.setItem(this.gambling.getArenaConfig().getInt("ARENA-MENU.POS.2.SLOT"), pos2);

        player.openInventory(inventory);
        return true;
    }
}
