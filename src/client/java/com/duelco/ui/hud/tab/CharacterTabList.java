package com.duelco.ui.hud.tab;

import com.duelco.managers.CharacterMapperManager;
import com.duelco.managers.DataManager;
import com.duelco.obj.general.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.resources.Identifier;

public class CharacterTabList extends TabListRenderer<Player> {

    public CharacterTabList(String id) {
        super(id);
        this.scrollTexture = Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_nearby_top.png");
        tabWidth = 120;
    }

    @Override
    Component getHeaderText() {
        return Component.literal("Nearby").withStyle(style -> style.withColor(0x00FFFF))
                .append(Component.literal(" (" + CharacterMapperManager.getPlayers().size() + ")").withStyle(style -> style.withColor(CommonColors.WHITE)));
    }

    @Override
    Component getFooterText() {
        return Component.literal("   " + Minecraft.getInstance().getConnection().getServerData().ip).withStyle(style -> style.withColor(0x00FFFF));
    }

    @Override
    void executeLoop(Player datum, GuiGraphicsExtractor context, int i) {
        if (y + animatedHeight > y + ((i % 15) * lineHeight) + 4) {
            // Draw the player's head (size: 16x16 pixels)
            PlayerFaceExtractor.extractRenderState(context, datum.getSkinTexture(), x + padding +  (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, 8, true, false, -1);

            // 18 char max before trailing off with ".."
            // Draw the player's name next to their head
            context.text(client.font, datum.getCharacterName(), x + padding + 12 + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, CommonColors.WHITE, true);
        }
    }

    @Override
    void setPosition() {
        this.x = (client.getWindow().getGuiScaledWidth() / 2 - tabWidth / 2) + 125;
        this.y = (client.getWindow().getGuiScaledHeight() / 2 - tabHeight / 2) - 25;
    }
}
