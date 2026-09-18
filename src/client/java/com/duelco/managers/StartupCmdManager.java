package com.duelco.managers;

import com.duelco._enum.NamesCmdOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class StartupCmdManager {
    private final NamesCmdOptions namesCmdOption = NamesCmdOptions.NAMES_OFF;

    public void executeNamesCmd() {
        LocalPlayer client = Minecraft.getInstance().player;

        if (client != null) {
            switch (this.namesCmdOption) {
                case NAMES_ON -> client.connection.sendCommand("names on");
                case NAMES_OFF -> client.connection.sendCommand("names off");
                case NAMES_CHAR -> client.connection.sendCommand("names character");
            }
        }
    }
}
