package com.duelco.ui.hud.tab;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.resources.Identifier;

public class DistrictTabList extends TabListRenderer<String> {
    private String districtName;

    public DistrictTabList(String id) {
        super(id);
        this.scrollTexture = Identifier.fromNamespaceAndPath("jimmytools", "ui/scroll_nearby_top.png");
        setColumns(1, 190);
        tabHeight = DISTRICT_HEIGHT;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Override
    Component getHeaderText() {
        return districtName == null ? null : Component.literal(districtName);
    }

    @Override
    Component getFooterText() {
        return Component.literal("Powered by the rawb.tv commmunity!");
    }

    @Override
    void executeLoop(String datum, GuiGraphicsExtractor context, int i) {
//        Identifier ANIMATED_TEXTURE = Identifier.fromNamespaceAndPath("jimmytools", "ui/mousedance.png");

        int entryY = y + (i * lineHeight) + ENTRY_OFFSET;
        if (y + animatedHeight > entryY + lineHeight) {
            context.text(client.font, datum, x + padding, entryY, CommonColors.WHITE, true);
        }
    }

    @Override
    void setPosition() {
        // Centered below the player and character scrolls
        this.x = centeredLeft(tabWidth);
        this.y = listTop() + (ROWS * lineHeight) + padding + (2 * GAP);
    }
}
