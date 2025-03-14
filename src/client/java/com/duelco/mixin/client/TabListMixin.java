package com.duelco.mixin.client;

import com.duelco.managers.DataManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class TabListMixin {

    @Inject(method = "onPlayerListHeader", at = @At("HEAD"))
    private void captureTabList(PlayerListHeaderS2CPacket packet, CallbackInfo ci) {
        if (packet != null) {
            // Lords of Minecraft 2\nonline: 4 (24) | tps: 20.0 | ping: 0ms
            String header = packet.header().getString();

            // Regex pattern
            Pattern pattern = Pattern.compile("(\\d+ \\(\\d+\\)) \\| tps: ([\\d.]+) \\| ping: (\\d+)ms");
            Matcher matcher = pattern.matcher(header);

            if (matcher.find()) {
                String onlinePlayers = matcher.group(1);  // "4 (24)"
                String tps = matcher.group(2);            // "20.0"
                String ping = matcher.group(3);           // "0ms"

                DataManager.getDataStore().getTabData().setPing(Double.parseDouble(ping));
                DataManager.getDataStore().getTabData().setTps(Double.parseDouble(tps));
                DataManager.getDataStore().getTabData().setCurrentServerPlayerCount(Integer.parseInt(onlinePlayers.split(" ")[0]));
                DataManager.getDataStore().getTabData().setTotalPlayerCount(Integer.parseInt(onlinePlayers.split(" ")[1].substring(1, onlinePlayers.split(" ")[1].length() - 1)));
            } else {
                System.out.println("No match found!");
            }
        }
    }
}
