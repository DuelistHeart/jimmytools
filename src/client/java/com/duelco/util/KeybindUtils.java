package com.duelco.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class KeybindUtils {
    public static KeyBinding getKeybind(String keybindName) {
        for (KeyBinding keyBinding : MinecraftClient.getInstance().options.allKeys) {
            if (keyBinding.getTranslationKey().equals(keybindName)) {
                return keyBinding;
            }
        }

        return null;
    }
}
