package com.duelco.ui.hud.tab;

import com.duelco.config.ModConfig;
import com.duelco.util.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

import java.util.List;
import java.util.stream.IntStream;

public abstract class TabListRenderer<T> {
    protected static final Minecraft client = Minecraft.getInstance();
    protected static final int ROWS = 14; // matches the rows-per-column of the server's tab list
    protected static final int GAP = 20;  // half the space between neighbouring scrolls
    protected static final int ENTRY_OFFSET = 7; // inset of the first row below the top roll (and of the last above the bottom one)
    protected static final int HEAD_SIZE = 8;

    private static final int LINE_HEIGHT = 10;
    private static final int PADDING = 8;
    protected static final int LIST_HEIGHT = (2 * ENTRY_OFFSET) + (ROWS * LINE_HEIGHT);
    private static final int PLAYER_WIDTH = (2 * 110) + (2 * PADDING);    // two columns, see PlayerTabList
    private static final int CHARACTER_WIDTH = 120 + (2 * PADDING);       // one column, see CharacterTabList
    protected static final int DISTRICT_HEIGHT = 46; // room for the plot name and owner lines, centred vertically by DistrictTabList

    // scroll_roll_*.png is [left cap | stretchable middle | right cap]; parchment_grain.png tiles over the background
    private static final int ROLL_CAP = 10;
    private static final int ROLL_MID = 4;
    private static final int ROLL_HEIGHT = 12;
    private static final int GRAIN_SIZE = 32;
    private static final Identifier GRAIN_TEXTURE = Identifier.fromNamespaceAndPath("jimmytools", "ui/parchment_grain.png");
    private static final int EDGE_SHADOW = 0x3B2410;
    private static final int[] EDGE_ALPHAS = {0x38, 0x22, 0x10};

    private static final float MAX_SCALE = 0.95f;
    private static final int SCREEN_MARGIN = 8;
    private static final int HOTBAR_CLEARANCE = 32; // keeps the footers clear of the hotbar and status bars
    private static final int HEADER_SPACE = 8;      // scroll roll + header text above a list
    private static final int FOOTER_SPACE = 12;

    // Shared by all three scrolls; recomputed every frame by updateLayout()
    private static float layoutScale = MAX_SCALE;
    private static int viewWidth;
    private static int viewHeight;
    private static int layoutTop;

    protected int animatedHeight = 0;
    protected final float ANIMATION_SPEED = 2.0f;
    protected final int lineHeight = LINE_HEIGHT;
    protected final int padding = PADDING;

    protected int x;
    protected int y;
    protected int columns = 1;
    protected int columnWidth = 120;
    protected int tabWidth = columnWidth + (2 * padding);
    protected int tabHeight = LIST_HEIGHT;
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

    /** Sets the number of entry columns and sizes the scroll to fit them. */
    protected void setColumns(int columns, int columnWidth) {
        this.columns = columns;
        this.columnWidth = columnWidth;
        this.tabWidth = (columns * columnWidth) + (2 * padding);
    }

    /**
     * Fits all three scrolls on screen, shrinking them when the GUI is too small to show them at full size.
     * Positions are computed in "view" coordinates, i.e. the screen as seen through {@link #getLayoutScale()}.
     */
    public static void updateLayout() {
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        // The scroll rolls stick out by a tenth of the width on each side
        int contentWidth = PLAYER_WIDTH + (2 * GAP) + CHARACTER_WIDTH + (PLAYER_WIDTH / 10) + (CHARACTER_WIDTH / 10);
        int contentHeight = HEADER_SPACE + LIST_HEIGHT + (2 * GAP) + DISTRICT_HEIGHT + FOOTER_SPACE;

        float availableWidth = screenWidth - (2 * SCREEN_MARGIN);
        float availableHeight = screenHeight - SCREEN_MARGIN - HOTBAR_CLEARANCE;
        layoutScale = Math.min(MAX_SCALE, Math.min(availableWidth / contentWidth, availableHeight / contentHeight));

        viewWidth = (int) (screenWidth / layoutScale);
        viewHeight = (int) (screenHeight / layoutScale);
        float availableViewHeight = availableHeight / layoutScale;
        layoutTop = (int) (SCREEN_MARGIN / layoutScale + (availableViewHeight - contentHeight) / 2f) + HEADER_SPACE;
    }

    public static float getLayoutScale() {
        return layoutScale;
    }

    /** Left edge of the player scroll; the player and character scrolls are centred as a pair. */
    protected static int playerLeft() {
        return (viewWidth - (PLAYER_WIDTH + (2 * GAP) + CHARACTER_WIDTH)) / 2;
    }

    protected static int characterLeft() {
        return playerLeft() + PLAYER_WIDTH + (2 * GAP);
    }

    protected static int centeredLeft(int width) {
        return (viewWidth - width) / 2;
    }

    /** Top edge shared by the player and character scrolls. */
    protected static int listTop() {
        return layoutTop;
    }

    public void render(GuiGraphicsExtractor context) {
        boolean scrollsVisible = animatedHeight > 0;
        if (!ModConfig.isCustomTablistEnabled || client == null || client.player == null || !ServerUtils.isLordsServer()) {
            return;
        }

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

        drawParchment(context);

        // Loop through the data and draw the entries
        if (data != null) {
            IntStream.range(0, data.size()).forEach(i -> executeLoop(data.get(i), context, i));
        }

        if (scrollsVisible) {
            drawScrolls(context);
            drawCentered(context, getHeaderText(), y - 6);
            drawCentered(context, getFooterText(), y + animatedHeight + 2);
        }
    }

    private void drawParchment(GuiGraphicsExtractor context) {
        if (animatedHeight <= 0) return;
        int bottom = y + animatedHeight;
        context.fillGradient(x, y, x + tabWidth, bottom, 0xFFFADFC2, 0xFFC5AC91);

        // Paper grain, tiled and clipped to the part of the scroll that is currently unrolled
        for (int ty = y; ty < bottom; ty += GRAIN_SIZE) {
            int h = Math.min(GRAIN_SIZE, bottom - ty);
            for (int tx = x; tx < x + tabWidth; tx += GRAIN_SIZE) {
                int w = Math.min(GRAIN_SIZE, x + tabWidth - tx);
                context.blit(RenderPipelines.GUI_TEXTURED, GRAIN_TEXTURE, tx, ty, 0, 0, w, h, GRAIN_SIZE, GRAIN_SIZE);
            }
        }

        // Soft shading where the paper curls into the rolls and at the sides
        for (int i = 0; i < EDGE_ALPHAS.length; i++) {
            int shade = (EDGE_ALPHAS[i] << 24) | EDGE_SHADOW;
            context.fill(x + i, y, x + i + 1, bottom, shade);
            context.fill(x + tabWidth - 1 - i, y, x + tabWidth - i, bottom, shade);
            context.fill(x, y + i, x + tabWidth, y + i + 1, shade);
            context.fill(x, bottom - 1 - i, x + tabWidth, bottom - i, shade);
        }
    }

    private void drawScrolls(GuiGraphicsExtractor context) {
        int rollX = x - (tabWidth / 10);
        int rollWidth = tabWidth + (tabWidth / 5);
        drawRoll(context, rollX, y - 8, rollWidth);
        drawRoll(context, rollX, y + animatedHeight, rollWidth);
    }

    /** Draws a wooden roll: end caps at native size with the middle stretched between them. */
    private void drawRoll(GuiGraphicsExtractor context, int rollX, int rollY, int width) {
        int textureWidth = (2 * ROLL_CAP) + ROLL_MID;
        context.blit(RenderPipelines.GUI_TEXTURED, scrollTexture, rollX, rollY, 0, 0, ROLL_CAP, ROLL_HEIGHT, textureWidth, ROLL_HEIGHT);
        context.blit(RenderPipelines.GUI_TEXTURED, scrollTexture, rollX + ROLL_CAP, rollY, ROLL_CAP, 0, width - (2 * ROLL_CAP), ROLL_HEIGHT, ROLL_MID, ROLL_HEIGHT, textureWidth, ROLL_HEIGHT);
        context.blit(RenderPipelines.GUI_TEXTURED, scrollTexture, rollX + width - ROLL_CAP, rollY, ROLL_CAP + ROLL_MID, 0, ROLL_CAP, ROLL_HEIGHT, textureWidth, ROLL_HEIGHT);
    }

    protected void drawCentered(GuiGraphicsExtractor context, Component text, int textY) {
        if (text == null) return;
        // Colour must carry full alpha or the text is not drawn
        context.text(client.font, text, x + (tabWidth - client.font.width(text)) / 2, textY, CommonColors.WHITE, true);
    }

    /**
     * Draws one head + name entry in the grid slot for index {@code i}, filling column by column.
     * Entries that don't fit the grid, or aren't yet revealed by the scroll animation, are skipped.
     */
    protected void drawEntry(GuiGraphicsExtractor context, Identifier skin, Component label, int i) {
        int column = i / ROWS;
        int row = i % ROWS;
        if (column >= columns) return;

        int entryY = y + (row * lineHeight) + ENTRY_OFFSET;
        if (y + animatedHeight < entryY + lineHeight) return;

        int entryX = x + padding + (column * columnWidth);
        int nameX = entryX + HEAD_SIZE + 4;
        PlayerFaceExtractor.extractRenderState(context, skin, entryX, entryY, HEAD_SIZE, true, false, -1);
        context.text(client.font, Language.getInstance().getVisualOrder(client.font.substrByWidth(label, entryX + columnWidth - nameX - padding)), nameX, entryY, CommonColors.WHITE, true);
    }

    abstract Component getHeaderText();
    abstract Component getFooterText();
    abstract void executeLoop(T datum, GuiGraphicsExtractor context, int i);
    abstract void setPosition();
}
