package com.duelco.ui.hud.tab;

import com.duelco.config.ModConfig;
import com.duelco.handlers.CharacterMappingHandler;
import com.duelco.managers.DataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.stream.IntStream;

public abstract class TabListRenderer<T> {
    protected static final Minecraft client = Minecraft.getInstance();
    protected int animatedHeight = 0;
    protected final float ANIMATION_SPEED = 2.0f;
    protected final int lineHeight = 10;
    protected final int padding = 5;

    protected int x;
    protected int y;
    protected int tabWidth = 200;
    protected int tabHeight = (15 * lineHeight) + padding;
    protected Identifier scrollTexture;

    private String id;
    private List<T> data;

    public TabListRenderer(String id) {
        this.id = id;
        data = null;
    }

    public TabListRenderer(String id, List<T> data) {
        this.id = id;
        this.data = data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void render(GuiGraphicsExtractor context) {
        boolean scrollsVisible = animatedHeight > 0;
        if (!ModConfig.isCustomTablistEnabled || client == null || client.player == null || client.getConnection() == null) {
            return;
        }

        CharacterMappingHandler.mapNearbyPlayers();

        if (client.options.keyPlayerList.isDown()) {
            this.setPosition();

            // Animate height increase (expands downwards)
            animatedHeight += (int) ModConfig.openScrollSpeed;
            animatedHeight = Math.min(animatedHeight, tabHeight); // Clamp to max height
        } else {
            // Animate height decrease (collapses smoothly)
            animatedHeight -= (int) ModConfig.closeScrollSpeed;
            animatedHeight = Math.max(animatedHeight, 0); // Ensure it doesn't go negative
        }

        // Render a background for the custom tab list
        context.fillGradient(x, y, x + tabWidth, y + animatedHeight, 0xFFF8DCC2, 0xFFAB9179);

        // Loop through the player list and draw custom tab names with iterator
        IntStream.range(0, data.size()).forEach(i -> executeLoop(data.get(i), context, i));

        if (scrollsVisible) {
            drawScrolls(context);
            if (getHeaderText() != null) context.text(client.font, getHeaderText(), x + padding, y-6, 0xFFFFFF, true);
            if (getFooterText() != null) context.text(client.font, getFooterText(), x + padding, y + animatedHeight+2, 0xFFFFFF, true);
        }
    }

    private void drawScrolls(GuiGraphicsExtractor context) {
        context.blit(RenderPipelines.GUI_TEXTURED, scrollTexture, x-(tabWidth/10), y-8, 0, 0, tabWidth + (tabWidth/5), 12, tabWidth + (tabWidth/5), 12);
        context.blit(RenderPipelines.GUI_TEXTURED, scrollTexture, x-(tabWidth/10), y+animatedHeight, 0, 0, tabWidth + (tabWidth/5), 12, tabWidth + (tabWidth/5), 12);
    }

    abstract Component getHeaderText();
    abstract Component getFooterText();
    abstract void executeLoop(T datum, GuiGraphicsExtractor context, int i);
    abstract void setPosition();
}
