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
        tabWidth = 200;
        tabHeight = 40;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Override
    Component getHeaderText() {
        return Component.literal(districtName);
    }

    @Override
    Component getFooterText() {
        return Component.literal("Powered by the rawb.tv commmunity!");
    }

    @Override
    void executeLoop(String datum, GuiGraphicsExtractor context, int i) {
//        Identifier ANIMATED_TEXTURE = Identifier.fromNamespaceAndPath("jimmytools", "ui/mousedance.png");

        if (y + animatedHeight > y + ((i % 15) * lineHeight) + 4) {

//            context.blit(RenderPipelines.GUI_TEXTURED, ANIMATED_TEXTURE, x + padding + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, 0, 0, tabWidth + (tabWidth/5), 12, tabWidth + (tabWidth/5), 12);
            context.text(client.font, datum, x + padding + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, CommonColors.WHITE, true);
        }
    }

    @Override
    void setPosition() {
        this.x = (client.getWindow().getGuiScaledWidth() / 2 - tabWidth / 2) + 25;
        this.y = (client.getWindow().getGuiScaledHeight() / 2 - tabHeight / 2) + 100;
    }
}
