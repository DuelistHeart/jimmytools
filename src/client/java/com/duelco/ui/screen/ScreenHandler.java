package com.duelco.ui.screen;

import com.duelco._enum.Screen;
import com.duelco.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class ScreenHandler {
    public static void displayScreen(Screen screen, Minecraft client) {
        switch (screen) {
            case INVENTORY_SCREEN -> client.gui.setScreen(new InventoryScreen(client.player));
            case CONFIG_SCREEN -> client.gui.setScreen(ModConfig.build().generateScreen(client.gui.screen()));
        }
    }

    public static void displayConfirmationScreen(Minecraft client, String title, Runnable onConfirm, Runnable onReturn) {
        client.gui.setScreen(new ConfirmationScreen(title, onConfirm, onReturn));
    }
}
