package com.duelco.handlers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemHandler {
    public static ItemStack getItemFromString(String itemName) {
        // Using the itemName string, retrieve an itemstack.
        // Please support the defaultstacks listed in the init() method in BingoPossibleItemsList.java
        switch (itemName) {
            case "Apple":
                return Items.APPLE.getDefaultInstance();
            case "Rabbit Foot":
                return Items.RABBIT_FOOT.getDefaultInstance();
            case "Bone":
                return Items.BONE.getDefaultInstance();
            case "Potion":
                return Items.POTION.getDefaultInstance();
            case "Rotten Flesh":
                return Items.ROTTEN_FLESH.getDefaultInstance();
            case "Filled Map":
                return Items.FILLED_MAP.getDefaultInstance();
            case "Wooden Shovel":
                return Items.WOODEN_SHOVEL.getDefaultInstance();
            case "Cherry Sapling":
                return Items.CHERRY_SAPLING.getDefaultInstance();
            case "Music Disc Cat":
                return Items.MUSIC_DISC_CAT.getDefaultInstance();
            case "Paper":
                return Items.PAPER.getDefaultInstance();
            case "Fishing Rod":
                return Items.FISHING_ROD.getDefaultInstance();
            case "Warped Fungus on a Stick":
                return Items.WARPED_FUNGUS_ON_A_STICK.getDefaultInstance();
            case "Iron Pickaxe":
                return Items.IRON_PICKAXE.getDefaultInstance();
            case "Glass Bottle":
                return Items.GLASS_BOTTLE.getDefaultInstance();
            default:
                return Items.AIR.getDefaultInstance();
        }

    }
}
