package com.duelco.ui.hud.tab;

import com.duelco.managers.DataManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.List;

public class PlayerTabList extends TabListRenderer<PlayerListEntry> {
    public PlayerTabList(String id, List<PlayerListEntry> data) {
        super(id, data);
    }

    public PlayerTabList(String id) {
        super(id);
        this.scrollTexture = Identifier.of("jimmytools", "ui/scroll_online_top.png");
    }

    void setPosition() {
        this.x = (client.getWindow().getScaledWidth() / 2 - tabWidth / 2) - 75;
        this.y = (client.getWindow().getScaledHeight() / 2 - tabHeight / 2) - 5;
    }

    @Override
    Text getHeaderText() {
        return Text.literal("Online").styled(style -> style.withColor(Colors.GREEN)).append(Text.literal(" (" +
                DataManager.getDataStore().getTabData().getCurrentServerPlayerCount() + "/" +
                DataManager.getDataStore().getTabData().getTotalPlayerCount() + ")").styled(style -> style.withColor(Colors.WHITE)))
                .append(Text.literal("  TPS: ").styled(style -> style.withColor(Colors.LIGHT_GRAY))
                        .append(Text.literal(String.valueOf(DataManager.getDataStore().getTabData().getTps()))
                                .styled(style -> style.withColor(Colors.WHITE)))
                        .append(Text.literal("  Ping: ").styled(style -> style.withColor(Colors.LIGHT_GRAY))
                                .append(Text.literal(String.valueOf(DataManager.getDataStore().getTabData().getPing()))
                                        .styled(style -> style.withColor(Colors.WHITE)))
                                .append(Text.literal("ms").styled(style -> style.withColor(Colors.WHITE)))
                        ));
    }

    @Override
    Text getFooterText() {
        return Text.literal("           Lords of Minecraft 2").styled(style -> style.withColor(Colors.YELLOW));
    }

    @Override
    void executeLoop(PlayerListEntry datum, DrawContext context, int i) {
        if (y + animatedHeight > y + ((i % 15) * lineHeight) + 4) {
            PlayerSkinDrawer.draw(context, datum.getSkinTextures().texture(), x + padding + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, 8, true, false, -1);

            // Draw the player's name next to their head
            context.drawText(client.textRenderer, datum.getDisplayName().getString(), x + padding + 12 + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, Colors.WHITE, true);
        }
    }
}
