package com.duelco.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.toasts.SystemToast;

public class ToastManager {
    public static void displayToast(String title, String description) {
        Minecraft.getInstance().gui.toastManager().addToast(new SystemToast(
                SystemToast.SystemToastId.CHUNK_LOAD_FAILURE,
                Component.literal(title),
                Component.literal(description)
        ));
    }
}
