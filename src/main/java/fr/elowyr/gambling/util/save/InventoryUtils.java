package fr.elowyr.gambling.util.save;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InventoryUtils {

    public static PlayerInv playerInventoryFromPlayer(Player player) {
        return new PlayerInv(player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public static PlayerInv playerInventoryFromPlayer2(final Player player) {
        final PlayerInv inv = new PlayerInv();
        inv.setContents(player.getInventory().getContents());
        inv.setArmorContents(player.getInventory().getArmorContents());
        return inv;
    }

    public static String playerInvToString(PlayerInv inv) {
        if(inv == null) return "null";

        StringBuilder builder = new StringBuilder();
        ItemStack[] armor = inv.getArmorContents();

        for (int i = 0; i < armor.length; i++) {
            if (i == 3) {
                if (armor[i] == null) {
                    builder.append(itemStackToString(new ItemStack(Material.AIR)));
                } else {
                    builder.append(itemStackToString(armor[3]));
                }
            } else {
                if (armor[i] == null) {
                    builder.append(itemStackToString(new ItemStack(Material.AIR))).append(";");
                } else {
                    builder.append(itemStackToString(armor[i])).append(";");
                }
            }
        }

        builder.append("|");

        for (int i = 0; i < inv.getContents().length; ++i) {
            builder.append(i).append("#").append(itemStackToString(inv.getContents()[i]))
                    .append((i == inv.getContents().length - 1) ? "" : ";");
        }

        return builder.toString();
    }

    public static String playerInventoryToString(PlayerInventory inv) {
        StringBuilder builder = new StringBuilder();
        ItemStack[] armor = inv.getArmorContents();

        for (int i = 0; i < armor.length; i++) {
            if (i == 3) {
                if (armor[i] == null) {
                    builder.append(itemStackToString(new ItemStack(Material.AIR)));
                } else {
                    builder.append(itemStackToString(armor[3]));
                }
            } else {
                if (armor[i] == null) {
                    builder.append(itemStackToString(new ItemStack(Material.AIR))).append(";");
                } else {
                    builder.append(itemStackToString(armor[i])).append(";");
                }
            }
        }

        builder.append("|");

        for (int i = 0; i < inv.getContents().length; ++i) {
            builder.append(i).append("#").append(itemStackToString(inv.getContents()[i]))
                    .append((i == inv.getContents().length - 1) ? "" : ";");
        }

        return builder.toString();
    }

    public static PlayerInv playerInventoryFromString(String in) {
        if(in == null || in.equals("unset") || in.equals("null") || in.equals("'null'")) return null;

        PlayerInv inv = new PlayerInv();

        String[] data = in.split("\\|");
        ItemStack[] armor = new ItemStack[data[0].split(";").length];

        for (int i = 0; i < data[0].split(";").length; ++i) {
            armor[i] = itemStackFromString(data[0].split(";")[i]);
        }

        inv.setArmorContents(armor);
        ItemStack[] contents = new ItemStack[data[1].split(";").length];

        for (String s : data[1].split(";")) {
            int slot = Integer.parseInt(s.split("#")[0]);

            if (s.split("#").length == 1) {
                contents[slot] = null;
            } else {
                contents[slot] = itemStackFromString(s.split("#")[1]);
            }
        }

        inv.setContents(contents);
        return inv;
    }

    @SuppressWarnings("deprecation")
    public static String itemStackToString(ItemStack item) {
        StringBuilder builder = new StringBuilder();

        if (item != null) {
            String isType = String.valueOf(item.getType().getId());
            builder.append("t@").append(isType);

            if (item.getDurability() != 0) {
                String isDurability = String.valueOf(item.getDurability());
                builder.append(":d@").append(isDurability);
            }

            if (item.getAmount() != 1) {
                String isAmount = String.valueOf(item.getAmount());
                builder.append(":a@").append(isAmount);
            }

            Map<Enchantment, Integer> isEnch = (Map<Enchantment, Integer>) item.getEnchantments();
            if (isEnch.size() > 0) {
                for (Map.Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
                    builder.append(":e@").append(ench.getKey().getId()).append("@").append(ench.getValue());
                }
            }

            if (item.hasItemMeta()) {
                ItemMeta imeta = item.getItemMeta();
                if (imeta.hasDisplayName()) {
                    builder.append(":dn@").append(imeta.getDisplayName());
                }
                if (imeta.hasLore()) {
                    builder.append(":l@").append(imeta.getLore());
                }
            }
        }

        return builder.toString();
    }

    public static String inventoryToString(Inventory inv) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < inv.getContents().length; ++i) {
            builder.append(i).append("#").append(itemStackToString(inv.getContents()[i]));
            if (i != inv.getContents().length - 1) {
                builder.append(";");
            }
        }

        return builder.toString();
    }

    public static Inventory inventoryFromString(String in) {
        Inventory inv = Bukkit.createInventory((InventoryHolder) null, 54);
        String[] split;
        @SuppressWarnings("unused")
        String[] data = split = in.split(";");

        for (String s : split) {
            String[] info = s.split("#");
            inv.setItem(Integer.parseInt(info[0]), (info.length > 1) ? itemStackFromString(info[1]) : null);
        }

        return inv;
    }

    @SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
    public static ItemStack itemStackFromString(String in) {
        ItemStack item = null;
        ItemMeta meta = null;
        String[] split;

        if (in == "null") {
            return new ItemStack(Material.AIR);
        }

        @SuppressWarnings("unused")
        String[] data = split = in.split(":");
        for (String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String s2 = itemAttribute[0];
            switch (s2) {
                case "t": {
                    item = new ItemStack(Material.getMaterial((int) Integer.valueOf(itemAttribute[1])));
                    meta = item.getItemMeta();
                    break;
                }
                case "d": {
                    if (item != null) {
                        item.setDurability((short) Short.valueOf(itemAttribute[1]));
                        break;
                    }
                    break;
                }
                case "a": {
                    if (item != null) {
                        item.setAmount((int) Integer.valueOf(itemAttribute[1]));
                        break;
                    }
                    break;
                }
                case "e": {
                    if (item != null) {
                        item.addEnchantment(Enchantment.getById((int) Integer.valueOf(itemAttribute[1])),
                                (int) Integer.valueOf(itemAttribute[2]));
                        break;
                    }
                    break;
                }
                case "dn": {
                    if (meta != null) {
                        meta.setDisplayName(itemAttribute[1]);
                        break;
                    }
                    break;
                }
                case "l": {
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");
                    List<String> lore = Arrays.asList(itemAttribute[1].split(","));
                    for (int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);
                        if (s != null) {
                            if (s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }
                                lore.set(x, s);
                            }
                        }
                    }
                    if (meta != null) {
                        meta.setLore((List) lore);
                        break;
                    }
                    break;
                }
            }
        }
        if (meta != null && (meta.hasDisplayName() || meta.hasLore())) {
            item.setItemMeta(meta);
        }
        return item;
    }

    public static String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        if (minutes == 0) {
            return seconds + " secondes";
        }
        seconds %= 60;
        return minutes + " minutes and " + seconds + " seconds";
    }

    @SuppressWarnings("rawtypes")
    public static void listFields() {
        try {
            String name = Bukkit.getServer().getClass().getPackage().getName();

            Class craftItemStack = Class.forName(name + ".inventory.CraftItemStack");

            for(Method m : craftItemStack.getDeclaredMethods()) {
                Bukkit.getLogger().severe(m.getName());

                for(Parameter p : m.getParameters()) {
                    Bukkit.getLogger().severe(m.getName() + " " + p.getName() + " " + p.getType().getName());
                }
            }

            for(Field f : craftItemStack.getDeclaredFields()) {
                Bukkit.getLogger().severe(f.getType().getCanonicalName() + " " + f.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String getFriendlyItemName(ItemStack is) {
        String pckg = Bukkit.getServer().getClass().getPackage().getName();

        try {
            Class craftItemStack = Class.forName(pckg + ".inventory.CraftItemStack");
            Method m = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
            Object o = m.invoke(craftItemStack, is);
            Method nm = o.getClass().getDeclaredMethod("getName");
            Object om = nm.invoke(o, (Object[]) null);

            return (String) om;
        } catch (ClassNotFoundException| NoSuchMethodException|SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
