package fr.elowyr.gambling.util.config;

import com.google.common.io.BaseEncoding;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ItemSave {

    public static String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null) return "null";

        ByteArrayOutputStream outputStream = null;
        try {
            Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
            Constructor<?> nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = getOBClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
            getNMSClass("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
            outputStream = new ByteArrayOutputStream();
            getNMSClass("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, OutputStream.class).invoke(null, nbtTagCompound, outputStream);
        } catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return BaseEncoding.base64().encode(outputStream.toByteArray());
    }


    public static ItemStack deserializeItemStack(String itemStackString) {
        if (itemStackString.equals("null")) return null;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(BaseEncoding.base64().decode(itemStackString));

        Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
        Class<?> nmsItemStackClass = getNMSClass("ItemStack");
        Object nbtTagCompound = null;
        ItemStack itemStack = null;
        try {
            nbtTagCompound = getNMSClass("NBTCompressedStreamTools").getMethod("a", InputStream.class).invoke(null, inputStream);
            Object craftItemStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass).invoke(null, nbtTagCompound);
            itemStack = (ItemStack) getOBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, craftItemStack);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }

        return itemStack;
    }

    private static Class<?> getNMSClass(String paramString) {
        String str1 = Bukkit.getServer().getClass().getPackage().getName();
        String str2 = str1.replace(".", ",").split(",")[3];
        String str3 = "net.minecraft.server." + str2 + "." + paramString;
        Class<?> localClass = null;
        try {
            localClass = Class.forName(str3);
        } catch (ClassNotFoundException localClassNotFoundException) {
            localClassNotFoundException.printStackTrace();
            System.err.println("Unable to find reflection class " + str3 + "!");
        }
        return localClass;
    }

    private static Class<?> getOBClass(String paramString) {
        String var1 = Bukkit.getServer().getClass().getPackage().getName();
        String var2 = var1.replace(".", ",").split(",")[3];
        String var3 = "org.bukkit.craftbukkit." + var2 + "." + paramString;
        Class<?> localClass = null;
        try {
            localClass = Class.forName(var3);
        } catch (ClassNotFoundException localClassNotFoundException) {
            localClassNotFoundException.printStackTrace();
        }
        return localClass;
    }

    public static double version;

    public static double getNMSVersion() {
        if (version != 0)
            return version;
        String var1 = Bukkit.getServer().getClass().getPackage().getName();
        String[] arrayOfString = var1.replace(".", ",").split(",")[3].split("_");
        String var2 = arrayOfString[0].replace("v", "");
        String var3 = arrayOfString[1];
        return version = Double.parseDouble(var2 + "." + var3);
    }

    public static boolean isNewVersion() {
        return !isOldVersion();
    }

    public static boolean isOneHand() {
        return getNMSVersion() == 1.7 || getNMSVersion() == 1.8;
    }

    public static boolean isOldVersion() {
        double version = getNMSVersion();
        return version == 1.7 || version == 1.8 || version == 1.9 || version == 1.10 || version == 1.13
                || version == 1.12;
    }
}
