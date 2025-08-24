package com.duelco.handlers;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemHandler {
    public static ItemStack getItemFromString(String itemName) {
        try {
            // Parse the item name as an Identifier (e.g., "minecraft:apple")
            Identifier itemId = Identifier.of(itemName);

            // Retrieve the item from the registry
            if (Registries.ITEM.containsId(itemId)) {
                return Registries.ITEM.get(itemId).getDefaultStack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Items.AIR.getDefaultStack();

    }
}
