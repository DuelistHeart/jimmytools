package com.duelco.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ContainerInput;

public class BagHandler {
    public static void clickCraftingSlot(Minecraft client, int craftingGridSlotIndex) {
        // Ensure the client has the player's inventory open
        if (client.gui.screen() instanceof InventoryScreen inventoryScreen) {
            // Get the slot ID for the crafting grid
            int slotId = craftingGridSlotIndex + 1; // Adjust for slot index offset

            // Simulate the click
            if (client.gameMode != null) {
                client.gameMode.handleContainerInput(
                        client.player.containerMenu.containerId, // Inventory sync ID
                        slotId,                                   // Slot ID to click
                        0,                                        // Mouse button (0 = left, 1 = right)
                        ContainerInput.PICKUP,                   // Action type (PICKUP simulates a normal click)
                        client.player                            // Player entity
                );

                System.out.println("Clicked crafting grid slot: " + craftingGridSlotIndex);
            }
        }
    }
}
