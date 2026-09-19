package com.duelco.handlers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerMessagerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("handlers.PlayerMessagerHandler");
    private static final Minecraft client;
    private static final Component prefix;

    static {
        client = Minecraft.getInstance();
        prefix = Component.literal("[").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("JimmyTools").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("] ").withStyle(ChatFormatting.GREEN));
    }

    public static void sendMessage(Component message) {
        LOGGER.info("Sending message: {}", message);

        if (client.player != null) client.player.sendSystemMessage(prefix.copy().append(message));
    }
}
