package com.duelco.ui.hud.tab;

import com.duelco.handlers.CharacterMappingHandler;
import com.duelco.managers.DataManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
        setColumns(2, 110); // the server's first two tab sections
        this.scrollTexture = Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_roll_online.png");
    }

    void setPosition() {
        this.x = playerLeft();
        this.y = listTop();
    }

    @Override
    Component getHeaderText() {
        return Component.literal("Server").withStyle(style -> style.withColor(CommonColors.GREEN)).append(Component.literal(" (" +
                DataManager.getDataStore().getTabData().getCurrentServerPlayerCount() + "/" +
                DataManager.getDataStore().getTabData().getTotalPlayerCount() + ")").withStyle(style -> style.withColor(CommonColors.WHITE)))
                .append(Component.literal("  TPS: ").withStyle(style -> style.withColor(CommonColors.LIGHT_GRAY))
                        .append(Component.literal(String.valueOf(DataManager.getDataStore().getTabData().getTps()))
                                .withStyle(style -> style.withColor(CommonColors.WHITE)))
                        .append(Component.literal("  Ping: ").withStyle(style -> style.withColor(CommonColors.LIGHT_GRAY))
                                .append(Component.literal(String.valueOf((int) DataManager.getDataStore().getTabData().getPing()))
                                        .withStyle(style -> style.withColor(CommonColors.WHITE)))
                                .append(Component.literal("ms").withStyle(style -> style.withColor(CommonColors.WHITE)))
                        ));
    }

    @Override
    Component getFooterText() {
        return Component.literal("Lords of Minecraft 2").withStyle(style -> style.withColor(CommonColors.YELLOW));
    }

    @Override
    void executeLoop(PlayerInfo datum, GuiGraphicsExtractor context, int i) {
        drawEntry(context, datum.getSkin().body().texturePath(), CharacterMappingHandler.getDisplayName(datum), i);
    }
}
