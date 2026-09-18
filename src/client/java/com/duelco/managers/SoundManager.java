package com.duelco.managers;

import com.duelco._enum.SoundEvent;
import com.duelco.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public class SoundManager {
    private static Minecraft client = Minecraft.getInstance();
    public static void playSound(SoundEvent soundEvent) {
        switch (soundEvent) {
            case PLACE_MARKER:
                if (ModConfig.isBingoMarkerPlaceSoundEnabled) client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.MUD_FALL, 2.0F));
                break;
            case REMOVE_MARKER:
                if (ModConfig.isBingoMarkerRemoveSoundEnabled) client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BRUSH_GENERIC, 1.5F));
                break;
            case CLEAR_BINGO_CARD:
                if (ModConfig.isBingoCardClearSoundEnabled) client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.5F));
                break;
            case GENERATE_BINGO_CARD:
                if (ModConfig.isBingoCardGenerateSoundEnabled) client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.5F));
                break;
        }
    }
}
