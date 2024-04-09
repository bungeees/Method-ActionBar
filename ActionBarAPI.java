me.xvnukz.actiobar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ActionBarAPI {

    private static String nmsVersion;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static void sendActionBar(Player player, String message) {
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
            Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent$ChatSerializer");
            Method aMethod = chatSerializerClass.getDeclaredMethod("a", String.class);
            Object chatBaseComponent = aMethod.invoke(null, "{\"text\": \"" + message + "\"}");

            if (nmsVersion.startsWith("v1_8_")) {
                Constructor<?> packetConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, byte.class);
                packet = packetConstructor.newInstance(chatBaseComponent, (byte) 2);
            } else {
                // Para versiones anteriores a 1.8, se puede usar otro constructor o método.
                // Este es un ejemplo y puede que necesites ajustarlo según la estructura NMS de 1.7.10.
                Constructor<?> packetConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, int.class);
                packet = packetConstructor.newInstance(chatBaseComponent, 2);
            }

            Method sendPacketMethod = craftPlayer.getClass().getDeclaredMethod("getHandle");
            Object entityPlayer = sendPacketMethod.invoke(craftPlayer);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packetPlayOutChatClass);
            sendPacket.invoke(playerConnection, packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
