package com.duelco.ui.hud.tab;

import com.duelco.managers.CharacterMapperManager;
import com.duelco.obj.general.Player;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.resources.Identifier;

public class CharacterTabList extends TabListRenderer<Player> {

    public CharacterTabList(String id) {
        super(id);
        this.scrollTexture = Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_nearby_top.png");
        setColumns(1, 120); // the server's third tab section
    }

    @Override
    Component getHeaderText() {
        return Component.literal("Nearby").withStyle(style -> style.withColor(0x00FFFF))
                .append(Component.literal(" (" + CharacterMapperManager.getPlayers().size() + ")").withStyle(style -> style.withColor(CommonColors.WHITE)));
    }

    @Override
    Component getFooterText() {
        if (client.getConnection() == null || client.getConnection().getServerData() == null) return null;
        return Component.literal(client.getConnection().getServerData().ip).withStyle(style -> style.withColor(0x00FFFF));
    }

    @Override
    void executeLoop(Player datum, GuiGraphicsExtractor context, int i) {
        drawEntry(context, datum.getSkinTexture(), datum.getCharacterName(), i);
    }

    @Override
    void setPosition() {
        this.x = characterLeft();
        this.y = listTop();
    }
}
