package com.duelco.managers;

import com.duelco.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class StartupCmdManager {
    private static boolean namesCmdPending = false;

    /**
     * Queues /names to run on the next client tick. It can't be sent straight from the join hook: Fabric only
     * creates its client command dispatcher once the login packet has been fully handled, and sending before
     * then throws (ClientCommandInternals.activeDispatcher is still null on the very first join of a session).
     */
    public static void queueNamesCmd() {
        namesCmdPending = true;
    }

    public static void tick() {
        if (!namesCmdPending) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return; // not in a world yet, try again next tick

        namesCmdPending = false;
        switch (ModConfig.startupCommandsNamesOption) {
            case NAMES_ON -> player.connection.sendCommand("names on");
            case NAMES_OFF -> player.connection.sendCommand("names off");
            case NAMES_CHAR -> player.connection.sendCommand("names character");
        }
    }
}
