package com.duelco.ui.hud.tab;

import com.duelco.managers.DataManager;
import com.duelco.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.resources.Identifier;

import java.util.List;

public class PlayerTabList extends TabListRenderer<PlayerInfo> {
    public PlayerTabList(String id, List<PlayerInfo> data) {
        super(id, data);
    }

    public PlayerTabList(String id) {
        super(id);
        this.tabWidth = 180;
        this.tabHeight = (12 * lineHeight) + padding;
        this.scrollTexture = Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_online_top.png");
    }

    void setPosition() {
        this.x = (client.getWindow().getGuiScaledWidth() / 2 - tabWidth / 2) - 75;
        this.y = (client.getWindow().getGuiScaledHeight() / 2 - tabHeight / 2) - 50;
    }

    @Override
    Component getHeaderText() {
        return Component.literal("Online").withStyle(style -> style.withColor(CommonColors.GREEN)).append(Component.literal(" (" +
                DataManager.getDataStore().getTabData().getCurrentServerPlayerCount() + "/" +
                DataManager.getDataStore().getTabData().getTotalPlayerCount() + ")").withStyle(style -> style.withColor(CommonColors.WHITE)))
                .append(Component.literal("  TPS: ").withStyle(style -> style.withColor(CommonColors.LIGHT_GRAY))
                        .append(Component.literal(String.valueOf(DataManager.getDataStore().getTabData().getTps()))
                                .withStyle(style -> style.withColor(CommonColors.WHITE)))
                        .append(Component.literal("  Ping: ").withStyle(style -> style.withColor(CommonColors.LIGHT_GRAY))
                                .append(Component.literal(String.valueOf(DataManager.getDataStore().getTabData().getPing()))
                                        .withStyle(style -> style.withColor(CommonColors.WHITE)))
                                .append(Component.literal("ms").withStyle(style -> style.withColor(CommonColors.WHITE)))
                        ));
    }

    @Override
    Component getFooterText() {
        return Component.literal("           Lords of Minecraft 2").withStyle(style -> style.withColor(CommonColors.YELLOW));
    }

    @Override
    void executeLoop(PlayerInfo datum, GuiGraphicsExtractor context, int i) {
        if (y + animatedHeight > y + ((i % 12) * lineHeight) + 4) {
            PlayerFaceExtractor.extractRenderState(context, datum.getSkin().body().texturePath(), x + padding + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, 8, true, false, -1);

            // Draw the player's name next to their head
            context.text(client.font, datum.getTabListDisplayName().getString(), x + padding + 12 + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, CommonColors.WHITE, true);
        }
    }
}
