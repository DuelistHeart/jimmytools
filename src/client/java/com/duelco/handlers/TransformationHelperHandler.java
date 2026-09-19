package com.duelco.handlers;

import com.duelco.config.ModConfig;
import com.duelco.managers.TransformationHelperManager;
import com.duelco.obj.general.Transformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformationHelperHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("handlers.TransformationHelper");
    private static final Minecraft client = Minecraft.getInstance();
    private static final TransformationHelperManager transformationHelperManager = new TransformationHelperManager();
    private static boolean isSettingUpTransformation = false;
    private static Transformation tempTransformation;

    public static boolean isSettingUpTransformation() {
        return isSettingUpTransformation;
    }

    public static void beginTransformationSetup() {
        isSettingUpTransformation = true;
        LOGGER.info("Setting up transformation.");
        ModConfig.regularSkin = getPlayerSkin();
        PlayerMessagerHandler.sendMessage(Component.literal("Please attempt to transform manually (/skin <url>).").withStyle(ChatFormatting.GREEN));
    }

    public static void completeTransformationSetup() {
        isSettingUpTransformation = false;
        LOGGER.info("Transformation setup completed.");
        PlayerMessagerHandler.sendMessage(Component.literal("Setup completed!.").withStyle(ChatFormatting.GREEN));
    }

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
            PlayerInfo playerEntry = client.player.connection.getOnlinePlayers().stream().filter(entry -> entry.getProfile().id().equals(client.player.getGameProfile().id()))
                    .findFirst()
                    .orElse(null);
            if (playerEntry != null) {
                // body() is declared as ClientAsset.Texture; only downloaded skins carry a URL.
                if (playerEntry.getSkin().body() instanceof ClientAsset.DownloadedTexture downloaded) {
                    return downloaded.url();
                }
            }
        }
        return null;
    }
}
