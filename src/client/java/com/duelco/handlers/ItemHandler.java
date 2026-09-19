package com.duelco.handlers;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemHandler {
    public static ItemStack getItemFromString(String itemName) {
        try {
            // Parse the item name as an Identifier (e.g., "minecraft:apple")
            Identifier itemId = Identifier.parse(itemName);

            // Retrieve the item from the registry
            if (BuiltInRegistries.ITEM.get(itemId).isPresent()) {
                return BuiltInRegistries.ITEM.get(itemId).get().value().getDefaultInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Items.AIR.getDefaultInstance();

    }
}
