package com.duelco.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class ServerUtils {
    private static final String LORDS_ADDRESS = "lords.rawb.tv";

    /** Whether the current connection is to the Lords of Minecraft 2 server. False in singleplayer. */
    public static boolean isLordsServer() {
        return isLordsServer(Minecraft.getInstance().getConnection());
    }

    public static boolean isLordsServer(ClientPacketListener connection) {
        return connection != null && connection.getServerData() != null && connection.getServerData().ip.startsWith(LORDS_ADDRESS);
    }
}
