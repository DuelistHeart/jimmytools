package com.duelco.ui.hud.tab;

import com.duelco.managers.CharacterMapperManager;
import com.duelco.managers.DataManager;
import com.duelco.obj.general.Player;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class CharacterTabList extends TabListRenderer<Player> {

    public CharacterTabList(String id) {
        super(id);
        this.scrollTexture = Identifier.of("jimmytools", "ui/scroll_nearby_top.png");
        tabWidth = 100;
    }

    @Override
    Text getHeaderText() {
        return Text.literal("Nearby").styled(style -> style.withColor(Colors.CYAN))
                .append(Text.literal(" (" + CharacterMapperManager.getPlayers().size() + ")").styled(style -> style.withColor(Colors.WHITE)));
    }

    @Override
    Text getFooterText() {
        return null;
    }

    @Override
    void executeLoop(Player datum, DrawContext context, int i) {
        if (y + animatedHeight > y + ((i % 15) * lineHeight) + 4) {
            // Draw the player's head (size: 16x16 pixels)
            PlayerSkinDrawer.draw(context, datum.getSkinTexture(), x + padding +  (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, 8, true, false, -1);

            // 18 char max before trailing off with ".."
            // Draw the player's name next to their head
            context.drawText(client.textRenderer, datum.getCharacterName(), x + padding + 12 + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, Colors.WHITE, true);
        }
    }

    @Override
    void setPosition() {
        this.x = (client.getWindow().getScaledWidth() / 2 - tabWidth / 2) + 125;
        this.y = (client.getWindow().getScaledHeight() / 2 - tabHeight / 2) - 5;
    }
}
