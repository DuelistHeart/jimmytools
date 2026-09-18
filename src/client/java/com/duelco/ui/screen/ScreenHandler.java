package com.duelco.ui.screen;

import com.duelco._enum.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class ScreenHandler {
    public static void displayScreen(Screen screen, Minecraft client) {
        switch (screen) {
            case BINGO_CARDS_SCREEN -> client.gui.setScreen(new BingoScreen());
            case BINGO_ITEMS_SCREEN -> client.gui.setScreen(new BingoItemsScreen());
            case INVENTORY_SCREEN -> {
                if (client.player != null) {
                    client.gui.setScreen(new InventoryScreen(client.player));
                }
            }
        }
    }

    public static void displayConfirmationScreen(Minecraft client, String title, Runnable onConfirm, Runnable onReturn) {
        client.gui.setScreen(new ConfirmationScreen(title, onConfirm, onReturn));
    }
}
