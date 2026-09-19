package com.duelco.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class KeybindUtils {
    public static KeyMapping getKeybind(String keybindName) {
        for (KeyMapping keyBinding : Minecraft.getInstance().options.keyMappings) {
            if (keyBinding.getName().equals(keybindName)) {
                return keyBinding;
            }
        }

        return null;
    }
}
