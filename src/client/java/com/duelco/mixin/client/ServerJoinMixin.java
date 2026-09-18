package com.duelco.mixin.client;

import com.duelco.config.ModConfig;
import com.duelco.managers.StartupCmdManager;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkAddon.class)
public class ServerJoinMixin {
    @Unique
    private final StartupCmdManager startupCmdManager = new StartupCmdManager();

    @Inject(method = "onServerReady", at = @At("HEAD"))
    private void onServerJoin(CallbackInfo ci) {
        if (ModConfig.startupCommandsNamesEnabled) {
            Minecraft client = Minecraft.getInstance();

            if (client.player != null) {
                // Retrieve the network connection
                ClientPacketListener connection = client.player.connection;

                if (connection != null && connection.getServerData() != null && connection.getServerData().ip.startsWith("lords.rawb.tv")) {
                    client.execute(startupCmdManager::executeNamesCmd);
                }
            }
        }
    }
}