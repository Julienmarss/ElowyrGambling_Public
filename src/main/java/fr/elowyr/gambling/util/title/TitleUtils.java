package fr.elowyr.gambling.util.title;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleUtils
{
    
    public static int getPing(final Player player) {
        return ((CraftPlayer)player).getHandle().ping;
    }
    
    public static Material getType(final String data) {
        try {
            return Material.getMaterial(Integer.parseInt(data));
        }
        catch (Throwable ignored) {
            try {
                return Material.valueOf(data.toUpperCase());
            }
            catch (Throwable ignored2) {
                return Material.getMaterial(data);
            }
        }
    }
    
    public static void sendActionBar(final Player player, final String message) {
        sendPacket(player, new PacketPlayOutChat(fromText(message), (byte)2));
    }
    
    public static void broadcastActionBar(final String message) {
        final PacketPlayOutChat packet = new PacketPlayOutChat(fromText(message), (byte)2);
        for (final Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }
    
    public static void sendTitle(final Player player, final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
        if (player == null || !player.isOnline() || !(player instanceof CraftPlayer)) {
            return;
        }
        final PlayerConnection con = ((CraftPlayer)player).getHandle().playerConnection;
        con.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent)null, fadeIn, stay, fadeOut));
        if (subTitle != null) {
            con.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, fromText(subTitle)));
        }
        if (title != null) {
            con.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, fromText(title)));
        }
    }
    
    public static void broadcastTitle(final String title, final String subTitle, final int fadeIn, final int stay, final int fadeOut) {
        final PacketPlayOutTitle times = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent)null, fadeIn, stay, fadeOut);
        final PacketPlayOutTitle subTitlePacket = (subTitle != null) ? new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, fromText(subTitle)) : null;
        final PacketPlayOutTitle titlePacket = (title != null) ? new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, fromText(title)) : null;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final PlayerConnection con = ((CraftPlayer)player).getHandle().playerConnection;
            con.sendPacket(times);
            if (subTitlePacket != null) {
                con.sendPacket(subTitlePacket);
            }
            if (titlePacket != null) {
                con.sendPacket(titlePacket);
            }
        }
    }
    
    public static void sendPacket(final Player player, final Packet<?> packet) {
        if (player == null || !player.isOnline() || !(player instanceof CraftPlayer)) {
            return;
        }
        final PlayerConnection con = ((CraftPlayer)player).getHandle().playerConnection;
        if (con != null) {
            con.sendPacket(packet);
        }
    }
    
    private static IChatBaseComponent fromText(final String text) {
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
    }
}
