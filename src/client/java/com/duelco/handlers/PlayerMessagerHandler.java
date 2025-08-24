package com.duelco.handlers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerMessagerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("handlers.PlayerMessagerHandler");
    private static final MinecraftClient client;
    private static final Text prefix;

    static {
        client = MinecraftClient.getInstance();
        prefix = Text.literal("[").formatted(Formatting.GREEN)
                .append(Text.literal("JimmyTools").formatted(Formatting.GRAY))
                .append(Text.literal("] ").formatted(Formatting.GREEN));
    }

    public static void sendMessage(Text message) {
        LOGGER.info("Sending message: {}", message);

        if (client.player != null) client.player.sendMessage(prefix.copy().append(message), false);
    }
}
