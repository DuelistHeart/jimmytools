package com.duelco.ui.screen;

import com.duelco.config.ModConfig;
import com.duelco.mixin.client.SlotAccessor;
import com.duelco.util.ServerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A Smash-style "fighter reveal" roster that replaces the server's "&lt;name&gt;'s Characters" chest. The hovered fighter
 * takes the stage in a burst of light and fire, and picking one plays a "JOINS THE BATTLE!" reveal
 * ({@link CharacterReveal}). The server's menu is kept as-is underneath: its slots are just moved onto the roster strip,
 * so left-click (select) and F (delete) still reach the server exactly as they would from the chest.
 */
public class CharacterSelectScreen extends AbstractContainerScreen<ChestMenu> {
    private static final String TITLE_SUFFIX = "'s Characters";

    private static final int HEADER_HEIGHT = 26;
    private static final int MARGIN = 16;
    private static final int TILE_GAP = 4;
    private static final int PLATE_HEIGHT = 10; // name strip under each face, on tiles wide enough to fit one
    private static final int MIN_TILE = 18;
    private static final int MAX_TILE = 48;
    private static final int STRIP_PADDING = 7;
    private static final int HIDDEN = -10000;   // parks slots we don't draw well outside any hitbox
    private static final int FREEZE_MS = 600;   // how long a click holds the roster still; see layoutRoster

    private final String owner;
    private final long openedAt = Util.getMillis();
    private final List<Slot> tiles = new ArrayList<>();
    private final List<ItemStack> stacks = new ArrayList<>(); // what each tile shows, index for index with tiles
    private int tileSize = MAX_TILE;
    private int plateHeight = PLATE_HEIGHT;
    private int stripTop;
    private long frozenUntil;

    private Slot featured; // fighter on the stage: the last one hovered, or the first until then
    private long featuredAt;

    /** Called for every screen the game opens; returns the roster screen in place of the character chest. */
    public static Screen wrapIfCharacterMenu(Screen screen) {
        if (!ModConfig.isCustomCharacterScreenEnabled || screen == null || screen.getClass() != ContainerScreen.class) return screen;

        ContainerScreen chest = (ContainerScreen) screen;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !chest.getTitle().getString().endsWith(TITLE_SUFFIX) || !ServerUtils.isLordsServer()) {
            return screen;
        }
        return new CharacterSelectScreen(chest.getMenu(), client.player.getInventory(), chest.getTitle());
    }

    private CharacterSelectScreen(ChestMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title,
                Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                Minecraft.getInstance().getWindow().getGuiScaledHeight());
        String text = title.getString();
        this.owner = text.substring(0, text.length() - TITLE_SUFFIX.length());
    }

    @Override
    protected void init() {
        super.init();
        // The screen fills the window, so everything below works in plain screen coordinates
        leftPos = 0;
        topPos = 0;
    }

    // ---- Layout ----

    /**
     * Moves the server's slots onto the roster strip along the bottom. Rerun every frame, since the server fills the
     * menu after it opens. Right after a click the roster is held as it was: the client guesses the click's outcome
     * (lifting the head off its slot) until the server puts it back, and the fighter shouldn't blink out meanwhile.
     */
    private void layoutRoster(long now) {
        if (now >= frozenUntil) {
            tiles.clear();
            stacks.clear();
            for (Slot slot : menu.slots) {
                if (slot.container == menu.getContainer() && slot.hasItem()) {
                    tiles.add(slot);
                    stacks.add(slot.getItem());
                } else {
                    moveSlot(slot, HIDDEN, HIDDEN); // player inventory and empty chest slots aren't part of the roster
                }
            }
        }

        // One row while the tiles stay a readable size, otherwise as few rows as keep them at the minimum
        int count = tiles.size();
        int available = width - 2 * MARGIN;
        int columns = Math.max(1, Math.min(count, (available + TILE_GAP) / (MIN_TILE + TILE_GAP)));
        int rows = Math.max(1, Mth.ceil((float) count / columns));
        columns = Math.max(1, Mth.ceil((float) count / rows)); // spread the tiles evenly over those rows
        int maxTile = Mth.clamp(height / 10, MIN_TILE, MAX_TILE);
        tileSize = Mth.clamp((available - (columns - 1) * TILE_GAP) / columns, MIN_TILE, maxTile);
        plateHeight = tileSize >= 28 ? PLATE_HEIGHT : 0;
        int rowHeight = tileSize + plateHeight;
        stripTop = height - 2 * STRIP_PADDING - rows * rowHeight - (rows - 1) * TILE_GAP;

        for (int i = 0; i < count; i++) {
            int row = i / columns;
            int inRow = Math.min(columns, count - row * columns); // a short last row sits centred
            int rowWidth = inRow * tileSize + (inRow - 1) * TILE_GAP;
            moveSlot(tiles.get(i), (width - rowWidth) / 2 + (i % columns) * (tileSize + TILE_GAP),
                    stripTop + STRIP_PADDING + row * (rowHeight + TILE_GAP));
        }
    }

    private static void moveSlot(Slot slot, int x, int y) {
        if (slot.x == x && slot.y == y) return;
        ((SlotAccessor) slot).jimmytools$setX(x);
        ((SlotAccessor) slot).jimmytools$setY(y);
    }

    /** Vanilla hit-tests slots as 16x16 squares; widen that to our tiles so clicks and F land on the right slot. */
    @Override
    protected boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        if (w == 16 && h == 16) {
            return mouseX >= x && mouseX < x + tileSize && mouseY >= y && mouseY < y + tileSize + plateHeight;
        }
        return super.isHovering(x, y, w, h, mouseX, mouseY);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top) {
        return false; // the whole window is the screen; a click on the background shouldn't count as a drop outside
    }

    private void updateHover(int mouseX, int mouseY, long now) {
        // Vanilla's click and key handling (including F for deletion) reads hoveredSlot, so keep it current ourselves.
        // Nothing counts as hovered while a reveal covers the roster.
        hoveredSlot = null;
        if (!CharacterReveal.isActive()) {
            for (Slot slot : tiles) {
                if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) hoveredSlot = slot;
            }
        }

        if (featured != null && !tiles.contains(featured)) featured = null;
        if (hoveredSlot != null && hoveredSlot != featured) {
            featured = hoveredSlot;
            featuredAt = now;
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.9f, 0.2f));
        } else if (featured == null && !tiles.isEmpty()) {
            // Until something is hovered, the first character takes the stage
            featured = tiles.getFirst();
            for (int i = 0; i < tiles.size(); i++) {
                if (stacks.get(i).has(DataComponents.PROFILE)) {
                    featured = tiles.get(i);
                    break;
                }
            }
            featuredAt = now;
        }
    }

    private ItemStack stackOf(Slot slot) {
        int index = tiles.indexOf(slot);
        return index < 0 ? ItemStack.EMPTY : stacks.get(index);
    }

    // ---- Input ----

    /**
     * Starts the reveal (or with it turned off, just its slash) when a fighter is picked, then lets the click through
     * to the server as usual.
     */
    @Override
    protected void slotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput) {
        int index = tiles.indexOf(slot);
        if (index >= 0 && containerInput == ContainerInput.PICKUP && buttonNum == 0) {
            ItemStack stack = stacks.get(index);
            ResolvableProfile profile = stack.get(DataComponents.PROFILE);
            if (profile != null) {
                if (ModConfig.isCharacterRevealEnabled) CharacterReveal.start(stack.getHoverName().getString(), profile);
                else CharacterReveal.startSlash();
            }
        }
        // Snapshot the tiles before the client's guess at the click empties one; see layoutRoster
        stacks.replaceAll(ItemStack::copy);
        frozenUntil = Util.getMillis() + FREEZE_MS;
        super.slotClicked(slot, slotId, buttonNum, containerInput);
    }

    // While a reveal plays, input is kept from the roster underneath; a click, Escape or Enter skips to its end

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (CharacterReveal.isActive()) {
            CharacterReveal.skip();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return CharacterReveal.isActive() || super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        return CharacterReveal.isActive() || super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (CharacterReveal.isActive()) {
            if (event.isEscape() || event.isConfirmation()) CharacterReveal.skip();
            return true;
        }
        return super.keyPressed(event);
    }

    // ---- Rendering ----

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // No carried item or tooltip: the server's hints are on the stage, and nothing here gets dragged around
        extractContents(context, mouseX, mouseY, delta);
        if (CharacterReveal.isActive()) {
            context.nextStratum();
            CharacterReveal.extract(context);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // The stage covers the whole window, so there's no vanilla dimming underneath
    }

    @Override
    public void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        long now = Util.getMillis();
        layoutRoster(now);
        updateHover(mouseX, mouseY, now);
        extractStage(context, now);
        extractHeader(context, now);
        extractStrip(context, now);
    }

    /** The featured fighter's splash: light burst, emblem, fire, the posed model and their name. */
    private void extractStage(GuiGraphicsExtractor context, long now) {
        ItemStack stack = stackOf(featured);
        String name = stack.isEmpty() ? "" : stack.getHoverName().getString();
        ResolvableProfile profile = stack.get(DataComponents.PROFILE);
        SplashRenderer.Palette palette = SplashRenderer.Palette.of(SplashRenderer.hue(name));
        float since = now - featuredAt;
        float lands = SplashRenderer.easeOut(SplashRenderer.progress(since, 0, 280));
        float opened = SplashRenderer.easeOut(SplashRenderer.progress(now - openedAt, 0, 400));

        int stageHeight = stripTop - HEADER_HEIGHT;
        float fighterX = width * 0.68f;
        float chestY = HEADER_HEIGHT + stageHeight * 0.47f;
        float size = stageHeight * 0.5f;

        SplashRenderer.backdrop(context, 0, 0, width, stripTop, palette);
        SplashRenderer.burst(context, fighterX, chestY, (float) Math.hypot(width, stripTop), palette, now * 0.00012f, opened);
        SplashRenderer.emblem(context, font, profile != null ? SplashRenderer.initial(name) : "?", fighterX, chestY, stageHeight * 0.42f,
                SplashRenderer.fade(0.09f * opened, palette.glow()));
        SplashRenderer.flames(context, 0, width, stripTop, stageHeight * 0.3f, palette, now, 0.45f * opened);

        if (profile != null) {
            if (!CharacterReveal.drawsFighter()) {
                // Each newly featured fighter lands from a little closer in
                context.enableScissor(0, 0, width, stripTop);
                SplashRenderer.fighter(context, profile, fighterX + 18f * (1f - lands), chestY, size * (1.16f - 0.16f * lands), size * 1.16f,
                        -24f + Mth.sin(now / 1600f) * 4f, 0f, now);
                context.disableScissor();
            }
        } else if (!stack.isEmpty()) {
            // Not a character head (e.g. an empty slot); show the server's item instead
            float itemScale = stageHeight * 0.3f / 16f * (1.16f - 0.16f * lands);
            Matrix3x2fStack pose = context.pose();
            pose.pushMatrix();
            pose.translate(fighterX, chestY + Mth.sin(now / 500f) * 4f);
            pose.scale(itemScale, itemScale);
            context.item(stack, -8, -8);
            pose.popMatrix();
        }

        SplashRenderer.flames(context, 0, width, stripTop, stageHeight * 0.1f, palette, now + 7919, 0.9f * opened);
        SplashRenderer.embers(context, 0, width, stripTop, stageHeight * 0.75f, palette, now, 36, opened);

        if (stack.isEmpty()) {
            Component loading = Component.literal("LOADING ROSTER").withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC);
            float x = (width - font.width(loading) * 2f) / 2f; // centred without the dots, so it doesn't jitter
            SplashRenderer.title(context, font, loading.copy().append(".".repeat((int) (now / 300 % 4))), x, chestY - 8f, 2f,
                    0xFFFFFF, 0xC8C8D8, 0x000000, 0, 1f);
        } else {
            extractNameplate(context, stack, name, palette, since);
        }

        Component back = Component.literal("ESC ").withStyle(ChatFormatting.BOLD).append(Component.literal("Back").withStyle(ChatFormatting.GRAY));
        context.text(font, back, width - MARGIN - font.width(back), stripTop - 14, SplashRenderer.WHITE, true);

        // A flash across the stage as each new fighter lands
        float flash = 1f - SplashRenderer.progress(since, 0, 180);
        if (flash > 0f && !stack.isEmpty()) {
            context.fill(0, 0, width, stripTop, SplashRenderer.fade(flash * 0.45f, 0xFFFFFF));
        }
    }

    /** Bottom left of the stage: the featured fighter's number and name, and the server's hints for them. */
    private void extractNameplate(GuiGraphicsExtractor context, ItemStack stack, String name, SplashRenderer.Palette palette, float since) {
        int left = MARGIN + 4;
        int columnWidth = Math.max(120, (int) (width * 0.55f) - left);
        int stageHeight = stripTop - HEADER_HEIGHT;

        // The server's hints (select, delete, ...): every tooltip line after the first, which is the name
        List<FormattedCharSequence> hints = new ArrayList<>();
        List<Component> lines = getTooltipFromContainerItem(stack);
        for (int i = 1; i < lines.size(); i++) {
            hints.addAll(font.split(lines.get(i), columnWidth));
        }
        hints = hints.subList(0, Mth.clamp(stageHeight / 40, 0, hints.size())); // at most a quarter of the stage

        int hintsTop = stripTop - 12 - hints.size() * 10;
        if (!hints.isEmpty()) {
            int widest = 0;
            for (FormattedCharSequence hint : hints) {
                widest = Math.max(widest, font.width(hint));
            }
            // A dark slanted panel keeps the hints readable over the fire
            SplashRenderer.skewFill(context, -20, hintsTop - 5, left + widest + 10, stripTop - 5, 0.3f, 0xB0000000, 0xD0000000);
            for (int i = 0; i < hints.size(); i++) {
                context.text(font, hints.get(i), left, hintsTop + i * 10, SplashRenderer.WHITE, true);
            }
        }

        // The name slams in, sliding from the left, whenever the featured fighter changes
        float lands = SplashRenderer.easeOut(SplashRenderer.progress(since, 0, 200));
        Component nameText = Component.literal(name.toUpperCase(Locale.ROOT)).withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC);
        int nameWidth = font.width(nameText);
        float scale = Math.min(Math.min(5f, stageHeight * 0.13f / 8f), (float) columnWidth / nameWidth);
        float slam = scale * (1f + 0.35f * (1f - lands));
        int nameBottom = hintsTop - (hints.isEmpty() ? 2 : 12);
        SplashRenderer.title(context, font, nameText, left - 16f * (1f - lands), nameBottom - 8f * slam, slam,
                0xFFFFFF, palette.pale(), palette.ink(), palette.flame(), lands);
        int accent = ARGB.opaque(palette.flame());
        SplashRenderer.skewFill(context, left - 6, nameBottom + 3, left - 6 + (int) ((nameWidth * scale + 12) * lands), nameBottom + 6, 0.5f, accent, accent);

        Component number = Component.literal(String.format(Locale.ROOT, "NO. %02d", tiles.indexOf(featured) + 1))
                .withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC);
        context.text(font, number, left, nameBottom - (int) (8f * scale) - 13, SplashRenderer.fade(lands, 0xFFD23F), true);
    }

    private void extractHeader(GuiGraphicsExtractor context, long now) {
        // Drops in from the top as the screen opens
        float drop = SplashRenderer.easeOut(SplashRenderer.progress(now - openedAt, 0, 300));
        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        pose.translate(0, -(HEADER_HEIGHT + 4) * (1f - drop));
        context.fillGradient(0, 0, width, HEADER_HEIGHT + 14, 0xC0000000, 0x00000000);

        Component title = Component.literal("CHOOSE YOUR CHARACTER!").withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC);
        float scale = 1.5f;
        int titleWidth = (int) (font.width(title) * scale);
        SplashRenderer.skewFill(context, -HEADER_HEIGHT, 0, MARGIN + titleWidth + 12, HEADER_HEIGHT, 0.5f, SplashRenderer.RED, SplashRenderer.DARK_RED);
        SplashRenderer.skewFill(context, -HEADER_HEIGHT, HEADER_HEIGHT, MARGIN + titleWidth + 12, HEADER_HEIGHT + 2, 0.5f,
                SplashRenderer.GOLD, SplashRenderer.GOLD, HEADER_HEIGHT);
        SplashRenderer.scaledText(context, font, title, MARGIN, (HEADER_HEIGHT - 9 * scale) / 2f + 1, scale, SplashRenderer.WHITE);

        int fighters = 0;
        for (ItemStack stack : stacks) {
            if (stack.has(DataComponents.PROFILE)) fighters++;
        }
        Component roster = Component.literal(owner.toUpperCase(Locale.ROOT)).withStyle(ChatFormatting.BOLD)
                .append(Component.literal("'S ROSTER").withStyle(ChatFormatting.GRAY));
        Component count = Component.literal(fighters + (fighters == 1 ? " FIGHTER" : " FIGHTERS")).withStyle(ChatFormatting.ITALIC);
        int rosterWidth = font.width(roster);
        if (MARGIN + titleWidth + 30 + rosterWidth < width - MARGIN) {
            context.text(font, roster, width - MARGIN - rosterWidth, 4, SplashRenderer.GOLD, true);
            context.text(font, count, width - MARGIN - font.width(count), 14, SplashRenderer.GREY, true);
        }
        pose.popMatrix();
    }

    /** The roster itself: a strip of fighter tiles along the bottom that rises in as the screen opens. */
    private void extractStrip(GuiGraphicsExtractor context, long now) {
        float rise = SplashRenderer.easeOut(SplashRenderer.progress(now - openedAt, 0, 300));
        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        pose.translate(0, (height - stripTop) * (1f - rise));
        context.fillGradient(0, stripTop, width, height, 0xF0141019, 0xFA050508);
        context.fill(0, stripTop, width, stripTop + 2, SplashRenderer.RED);
        context.fill(0, stripTop + 2, width, stripTop + 3, SplashRenderer.GOLD);

        // The hovered tile is drawn last so its pop-out sits above its neighbours
        int hovered = tiles.indexOf(hoveredSlot);
        for (int i = 0; i < tiles.size(); i++) {
            if (i != hovered) extractTile(context, i, now);
        }
        if (hovered >= 0) extractTile(context, hovered, now);
        if (featured != null) {
            int lift = featured == hoveredSlot ? 3 : 0;
            extractPlayerToken(context, featured.x + tileSize - 2, featured.y - 1 - lift + (int) (Mth.sin(now / 150f) * 1.5f));
        }
        pose.popMatrix();
    }

    private void extractTile(GuiGraphicsExtractor context, int index, long now) {
        // Tiles pop in one after another when the screen opens
        float intro = SplashRenderer.progress(now - openedAt - index * 28L, 120, 240);
        if (intro <= 0f) return;

        Slot slot = tiles.get(index);
        ItemStack stack = stacks.get(index);
        boolean hovered = slot == hoveredSlot;
        boolean isFeatured = slot == featured;
        String name = stack.getHoverName().getString();
        float hue = SplashRenderer.hue(name);
        int w = tileSize;
        int h = tileSize + plateHeight;
        float scale = SplashRenderer.easeOutBack(intro) * (hovered ? 1.1f : 1f);

        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        pose.translate(slot.x + w / 2f, slot.y + h / 2f - (hovered ? 3f : 0f));
        pose.scale(scale, scale);
        pose.translate(-w / 2f, -h / 2f);

        context.fill(-1, -1, w + 1, h + 1, 0xFF000000);
        context.fillGradient(0, 0, w, h, ARGB.opaque(Mth.hsvToRgb(hue, 0.7f, 0.6f)), ARGB.opaque(Mth.hsvToRgb(hue, 0.8f, 0.2f)));
        context.fill(0, 0, w, h / 2, 0x14FFFFFF);

        ResolvableProfile profile = stack.get(DataComponents.PROFILE);
        if (profile != null) {
            PlayerFaceExtractor.extractRenderState(context, profile, 2, 2, w - 4);
            context.fillGradient(2, w - 12, w - 2, w - 2, 0x00000000, 0x80000000);
        } else {
            // Not a character head (e.g. an empty slot); show whatever item the server put there
            float itemScale = Math.min((w - 6) / 16f, 2.5f);
            pose.pushMatrix();
            pose.translate(w / 2f, w / 2f);
            pose.scale(itemScale, itemScale);
            context.item(stack, -8, -8);
            pose.popMatrix();
        }

        if (plateHeight > 0) {
            context.fill(0, w - 1, w, h, 0xE6000000);
            context.fill(0, w - 1, w, w, ARGB.opaque(Mth.hsvToRgb(hue, 0.65f, 0.95f)));
            String label = font.plainSubstrByWidth(name, w - 3);
            context.text(font, label, (w - font.width(label)) / 2, w + 1, isFeatured ? SplashRenderer.GOLD : SplashRenderer.WHITE, true);
        }

        if (hovered) {
            int pulse = (int) (0xB0 + 0x4F * Mth.sin(now / 120f));
            context.outline(-3, -3, w + 6, h + 6, (pulse << 24) | (SplashRenderer.GOLD & 0xFFFFFF));
            context.outline(-2, -2, w + 4, h + 4, SplashRenderer.WHITE);
        } else if (isFeatured) {
            context.outline(-2, -2, w + 4, h + 4, SplashRenderer.GOLD);
        }
        float flash = 1f - (now - featuredAt) / 200f;
        if (isFeatured && flash > 0f) {
            context.fill(0, 0, w, h, SplashRenderer.fade(flash * 0.6f, 0xFFFFFF));
        }
        pose.popMatrix();
    }

    /** The red "P1" chip that marks the featured fighter. */
    private void extractPlayerToken(GuiGraphicsExtractor context, int cx, int cy) {
        context.fill(cx - 9, cy - 6, cx + 9, cy + 6, 0xFF000000);
        context.fill(cx - 7, cy - 8, cx + 7, cy + 8, 0xFF000000);
        context.fill(cx - 8, cy - 5, cx + 8, cy + 5, SplashRenderer.RED);
        context.fill(cx - 6, cy - 7, cx + 6, cy + 7, SplashRenderer.RED);
        context.fill(cx - 6, cy - 7, cx + 6, cy - 4, 0x40FFFFFF);
        Component p1 = Component.literal("P1").withStyle(ChatFormatting.BOLD);
        context.text(font, p1, cx - font.width(p1) / 2 + 1, cy - 3, SplashRenderer.WHITE, true);
    }
}
