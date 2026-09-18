package com.duelco.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.ClickAction;

public class BagHandler {
    public static void clickCraftingSlot(Minecraft client, int craftingGridSlotIndex) {
        // Ensure the client has the player's inventory open
        if (client.gui.screen() instanceof InventoryScreen inventoryScreen) {
            // Get the slot ID for the crafting grid
            int slotId = craftingGridSlotIndex + 1; // Adjust for slot index offset

            // Simulate the click
            if (client.gameMode != null) {
                client.gameMode.handleInventoryButtonClick(
                        inventoryScreen.getMenu().containerId, // Inventory container ID
                        InputConstants.MOUSE_BUTTON_LEFT
                );

                System.out.println("Clicked crafting grid slot: " + craftingGridSlotIndex);
            }
        }
    }
}
