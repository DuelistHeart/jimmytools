package com.duelco.ui.screen;

import com.duelco._enum.Screen;
import com.duelco.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class ScreenHandler {
    public static void displayScreen(Screen screen, MinecraftClient client) {
        switch (screen) {
            case BINGO_CARDS_SCREEN -> client.setScreen(new BingoScreen());
            case BINGO_ITEMS_SCREEN -> client.setScreen(new BingoItemsScreen());
            case INVENTORY_SCREEN -> client.setScreen(new InventoryScreen(client.player));
            case CONFIG_SCREEN -> client.setScreen(ModConfig.build().generateScreen(client.currentScreen));
        }
    }

    public static void displayConfirmationScreen(MinecraftClient client, String title, Runnable onConfirm, Runnable onReturn) {
        client.setScreen(new ConfirmationScreen(title, onConfirm, onReturn));
    }
}
