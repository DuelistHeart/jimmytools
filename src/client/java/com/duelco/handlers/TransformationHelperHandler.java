package com.duelco.handlers;

import com.duelco.config.ModConfig;
import com.duelco.managers.TransformationHelperManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.server.players.PlayerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformationHelperHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("handlers.TransformationHelper");
    private static final Minecraft client = Minecraft.getInstance();
    private static final TransformationHelperManager transformationHelperManager = new TransformationHelperManager();

    public static void execute() {
        if (client.player != null) {
            LOGGER.debug("Retrieving skin url.");
            String skinUrl = getPlayerSkin();
            LOGGER.debug("The retrieved player skin is {}", skinUrl);
            LOGGER.debug("Executing transformation.");
            String newSkin = transformationHelperManager.handleTransform(getPlayerSkin());

            if (newSkin != null) {
                LOGGER.debug("The new player skin is {}", newSkin);
                client.player.connection.sendCommand("skin " + newSkin);
                LOGGER.debug("The command is /skin {}", newSkin);
                ModConfig.HANDLER.save();
            }
        } else {
            LOGGER.debug("Player is null, skipping command execution.");
        }
    }

    private static String getPlayerSkin() {
        if (client.player != null) {
            return client.player.getSkin().body().toString();
        }
        return null;
    }
}
