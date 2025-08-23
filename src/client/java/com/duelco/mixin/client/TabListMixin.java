package com.duelco.mixin.client;

import com.duelco.handlers.RegexHandler;
import com.duelco.managers.DataManager;
import com.duelco.obj.general.PlotInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class TabListMixin {

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "onPlayerListHeader", at = @At("HEAD"))
    private void captureTabList(PlayerListHeaderS2CPacket packet, CallbackInfo ci) {
        if (packet != null) {
            // Lords of Minecraft 2\nonline: 4 (24) | tps: 20.0 | ping: 0ms
            String header = packet.header().getString();
            String footer = packet.footer().getString();

            // Regex pattern
            Pattern pattern = Pattern.compile("(\\d+ \\(\\d+\\)) \\| tps: ([\\d.]+) \\| ping: (\\d+)ms");
            Matcher matcher = pattern.matcher(header);

            PlotInfo plotInfo = RegexHandler.parsePlotInfo(footer.split("\n")[0]); // Parse footer line for district/plot info

            if (plotInfo != null) {
                LOGGER.info("Parsed plot info: Plot: {}, District: {}, Owner: {}", plotInfo.getPlot(), plotInfo.getDistrict(), plotInfo.getOwner());

                // Update DataManager with parsed plot info
                DataManager.getDataStore().getTabData().setPlotInfo(plotInfo);

                // Uncomment if you want to set individual fields
//                DataManager.getDataStore().getTabData().setDistrict(plotInfo.getDistrict());
//                DataManager.getDataStore().getTabData().setPlot(plotInfo.getPlot());
//                DataManager.getDataStore().getTabData().setOwner(plotInfo.getOwner());
            } else {
                LOGGER.debug("Failed to parse plot info from footer: {}", footer);
            }

            if (matcher.find()) {
                String onlinePlayers = matcher.group(1);  // "4 (24)"
                String tps = matcher.group(2);            // "20.0"
                String ping = matcher.group(3);           // "0ms"

                DataManager.getDataStore().getTabData().setPing(Double.parseDouble(ping));
                DataManager.getDataStore().getTabData().setTps(Double.parseDouble(tps));
                DataManager.getDataStore().getTabData().setCurrentServerPlayerCount(Integer.parseInt(onlinePlayers.split(" ")[0]));
                DataManager.getDataStore().getTabData().setTotalPlayerCount(Integer.parseInt(onlinePlayers.split(" ")[1].substring(1, onlinePlayers.split(" ")[1].length() - 1)));
            } else {
                LOGGER.debug("No match found!");
            }
        }
    }
}
