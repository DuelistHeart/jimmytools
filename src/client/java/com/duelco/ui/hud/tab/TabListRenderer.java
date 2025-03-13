package com.duelco.ui.hud.tab;

import com.duelco.handlers.CharacterMappingHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.IntStream;

public abstract class TabListRenderer<T> {
    protected static final MinecraftClient client = MinecraftClient.getInstance();
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

    public void render(DrawContext context) {
        boolean scrollsVisible = animatedHeight > 0;
        if (client == null || client.player == null || client.getNetworkHandler() == null) {
            return;
        }

        CharacterMappingHandler.mapNearbyPlayers();

        if (client.options.playerListKey.isPressed()) {
            this.setPosition();

            // Animate height increase (expands downwards)
            animatedHeight += (int) ANIMATION_SPEED;
            animatedHeight = Math.min(animatedHeight, tabHeight); // Clamp to max height
        } else {
            // Animate height decrease (collapses smoothly)
            animatedHeight -= (int) 8.0f;
            animatedHeight = Math.max(animatedHeight, 0); // Ensure it doesn't go negative
        }

        // Render a background for the custom tab list
        context.fillGradient(x, y, x + tabWidth, y + animatedHeight, 0xFFF8DCC2, 0xFFAB9179);

        // Loop through the player list and draw custom tab names with iterator
        IntStream.range(0, data.size()).forEach(i -> executeLoop(data.get(i), context, i));

        if (scrollsVisible) {
            drawScrolls(context);
//            context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("jimmytools", "ui/scroll_online_top.png"), x-20, y-12, 0, 0, 240, 16, 240, 16);
//        context.drawTexture(Identifier.of("jimmytools", "textures/ui/scroll_online_top.png"), 10, 10, 0, 0, 32, 32, 32, 32);;
        }
    }

    private void drawScrolls(DrawContext context) {
        context.drawTexture(RenderLayer::getGuiTextured, scrollTexture, x-20, y-8, 0, 0, tabWidth + 40, 8, tabWidth + 40, 8);
        context.drawTexture(RenderLayer::getGuiTextured, scrollTexture, x-20, y+animatedHeight, 0, 0, tabWidth + 40, 8, tabWidth + 40, 8);
    }

    abstract void executeLoop(T datum, DrawContext context, int i);
    abstract void setPosition();
}
