package me.drawe.buildbattle.utils;

import me.drawe.buildbattle.BuildBattle;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeUtils {

    public static void connectPlayerToServer(Player p, String serverName) {
        if(serverName != null) {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("Connect");
                out.writeUTF(serverName);
                p.getPlayer().sendPluginMessage(BuildBattle.getInstance(), "BungeeCord", b.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
