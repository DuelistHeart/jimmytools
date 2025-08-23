package com.duelco.ui.hud.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class DistrictTabList extends TabListRenderer<String> {
    private String districtName;

    public DistrictTabList(String id) {
        super(id);
        this.scrollTexture = Identifier.of("jimmytools", "ui/scroll_nearby_top.png");
        tabWidth = 200;
        tabHeight = 40;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Override
    Text getHeaderText() {
        return Text.literal(districtName);
    }

    @Override
    Text getFooterText() {
        return Text.literal("Powered by the rawb.tv commmunity!");
    }

    @Override
    void executeLoop(String datum, DrawContext context, int i) {
//        Identifier ANIMATED_TEXTURE = Identifier.of("jimmytools", "ui/mousedance.png");

        if (y + animatedHeight > y + ((i % 15) * lineHeight) + 4) {

//            context.drawTexture(RenderLayer::getGuiTextured, ANIMATED_TEXTURE, x + padding + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, 0, 0, tabWidth + (tabWidth/5), 12, tabWidth + (tabWidth/5), 12);
            context.drawText(client.textRenderer, datum, x + padding + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, Colors.WHITE, true);
        }
    }

    @Override
    void setPosition() {
        this.x = (client.getWindow().getScaledWidth() / 2 - tabWidth / 2) + 25;
        this.y = (client.getWindow().getScaledHeight() / 2 - tabHeight / 2) + 100;
    }
}
