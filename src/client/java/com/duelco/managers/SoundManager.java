package com.duelco.managers;

import com.duelco._enum.SoundEvent;
import com.duelco.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class SoundManager {
    private static MinecraftClient client = MinecraftClient.getInstance();
    public static void playSound(SoundEvent soundEvent) {
        switch (soundEvent) {
            case PLACE_MARKER:
                if (ModConfig.isBingoMarkerPlaceSoundEnabled) client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_PACKED_MUD_FALL, 2.0F));
                break;
            case REMOVE_MARKER:
                if (ModConfig.isBingoMarkerRemoveSoundEnabled) client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, 1.5F));
                break;
            case CLEAR_BINGO_CARD:
                if (ModConfig.isBingoCardClearSoundEnabled) client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.5F));
                break;
            case GENERATE_BINGO_CARD:
                if (ModConfig.isBingoCardGenerateSoundEnabled) client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.5F));
                break;
        }
    }
}
