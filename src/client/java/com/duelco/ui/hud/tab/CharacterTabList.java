package com.duelco.ui.hud.tab;

import com.duelco.obj.general.Player;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.util.Colors;

public class CharacterTabList extends TabListRenderer<Player> {

    public CharacterTabList(String id) {
        super(id);
        tabWidth = 100;
    }

    @Override
    void executeLoop(Player datum, DrawContext context, int i) {
        if (y + animatedHeight > y + ((i % 15) * lineHeight) + 4) {
            // Draw the player's head (size: 16x16 pixels)
            PlayerSkinDrawer.draw(context, datum.getSkinTexture(), x + padding +  (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, 8, true, false, -1);

            // Draw the player's name next to their head
            context.drawText(client.textRenderer, datum.getCharacterName(), x + padding + 12 + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, Colors.BLACK, false);
        }
    }

    @Override
    void setPosition() {
        this.x = (client.getWindow().getScaledWidth() / 2 - tabWidth / 2) + 125;
        this.y = (client.getWindow().getScaledHeight() / 2 - tabHeight / 2) - 5;
    }
}
