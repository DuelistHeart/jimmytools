package com.duelco.ui.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.item.component.ResolvableProfile;
import org.joml.Matrix3x2fStack;

import java.util.Locale;

/**
 * The "&lt;NAME&gt; JOINS THE BATTLE!" splash played when a character is picked on the roster. The pick itself reaches
 * the server straight away; this is only a show on top, drawn by the roster screen while it is open and by the HUD
 * once the server closes the menu, so it carries on over the character loading in.
 */
public final class CharacterReveal {
    // Timeline, in ms after the pick
    private static final int FLASH_IN = 70;      // whites out the roster, then the splash cuts in underneath
    private static final int FLASH_OUT = 360;
    private static final int NAME_DROP = 250;    // the name falls toward the screen...
    private static final int NAME_HIT = 430;     // ...and lands: shake, speed lines, smash
    private static final int SUBTITLE_IN = 640;  // "JOINS THE BATTLE!" and the fanfare
    private static final int EXIT_AT = 2900;
    private static final int EXIT_TIME = 420;
    private static final int SKIPPABLE_AFTER = 450;

    private static final float WIPE_SKEW = 0.3f;
    private static final SplashRenderer.Palette FIRE = SplashRenderer.Palette.of(0.06f);

    private static String name = "";
    private static ResolvableProfile profile;
    private static float hue;
    private static long startedAt = -1;
    private static int exitAt = EXIT_AT;
    private static int cuesPlayed;

    private CharacterReveal() {
    }

    static void start(String fighterName, ResolvableProfile fighterProfile) {
        name = fighterName;
        profile = fighterProfile;
        hue = SplashRenderer.hue(fighterName);
        startedAt = Util.getMillis();
        exitAt = EXIT_AT;
        cuesPlayed = 0;
        play(SoundEvents.FIRECHARGE_USE, 0.7f, 0.5f);
    }

    static boolean isActive() {
        return startedAt >= 0 && elapsed() < exitAt + EXIT_TIME;
    }

    /** Whether the reveal draws its own fighter this frame. Only one skin model can be drawn per frame. */
    static boolean drawsFighter() {
        if (!isActive() || elapsed() < FLASH_IN) return false;
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        return wipeEdge(elapsed(), width, minecraft.getWindow().getGuiScaledHeight()) < width;
    }

    /** Jumps to the closing wipe. Ignored just after the pick, so the rest of a double click doesn't skip the show. */
    static void skip() {
        int elapsed = elapsed();
        if (isActive() && elapsed >= SKIPPABLE_AFTER && elapsed < exitAt) {
            exitAt = elapsed;
        }
    }

    /** HUD entry point. While the roster screen is open it draws the reveal on top of itself instead. */
    public static void extractHud(GuiGraphicsExtractor context) {
        if (isActive() && !(Minecraft.getInstance().gui.screen() instanceof CharacterSelectScreen)) {
            extract(context);
        }
    }

    static void extract(GuiGraphicsExtractor context) {
        if (!isActive()) return;
        int t = elapsed();
        int width = context.guiWidth();
        int height = context.guiHeight();
        playCues(t);

        if (t < FLASH_IN) {
            context.fill(0, 0, width, height, SplashRenderer.fade((float) t / FLASH_IN, 0xFFFFFF));
            return;
        }

        if (t < exitAt) {
            extractSplash(context, width, height, t);
            return;
        }

        // A slanted slash sweeps the splash away left to right. The splash is cut off with a straight scissor edge,
        // so the slash is wide enough to hide that edge all the way down.
        int lean = (int) (height * WIPE_SKEW);
        int edge = wipeEdge(t, width, height);
        if (edge < width) {
            context.enableScissor(Math.max(edge, 0), 0, width, height);
            extractSplash(context, width, height, t);
            context.disableScissor();
        }
        SplashRenderer.skewFill(context, edge - lean - 24, 0, edge + 30, height, WIPE_SKEW, 0xFF000000, 0xFF000000);
        SplashRenderer.skewFill(context, edge - lean - 18, 0, edge + 22, height, WIPE_SKEW, SplashRenderer.RED, SplashRenderer.DARK_RED);
        SplashRenderer.skewFill(context, edge + 22, 0, edge + 26, height, WIPE_SKEW, SplashRenderer.GOLD, SplashRenderer.GOLD);
    }

    /** Where the closing slash has swept to: everything left of this x is cleared. Off the left edge until it starts. */
    private static int wipeEdge(int t, int width, int height) {
        int lean = (int) (height * WIPE_SKEW);
        float exit = SplashRenderer.progress(t, exitAt, EXIT_TIME);
        return (int) Mth.lerp(SplashRenderer.easeInOut(exit), -(lean + 30), width + lean + 24);
    }

    private static void extractSplash(GuiGraphicsExtractor context, int width, int height, int t) {
        long now = Util.getMillis();
        Font font = Minecraft.getInstance().font;
        SplashRenderer.Palette palette = SplashRenderer.Palette.of(hue);
        Matrix3x2fStack pose = context.pose();

        // The camera shakes as the name lands
        float shake = t >= NAME_HIT ? Mth.square(1f - SplashRenderer.progress(t, NAME_HIT, 450)) : 0f;
        float shakeX = Mth.sin(t * 0.13f) * 7f * shake;
        float shakeY = Mth.cos(t * 0.17f) * 5f * shake;
        pose.pushMatrix();
        pose.translate(shakeX, shakeY);

        float cx = width / 2f;
        float chestY = height * 0.46f;
        float size = height * 0.42f;
        float reach = (float) Math.hypot(width, height) * 0.75f;
        float land = SplashRenderer.easeOut(SplashRenderer.progress(t, FLASH_IN, 650));
        // The fighter lands from close up, then keeps creeping toward the camera while the splash holds
        float zoom = 1.22f - 0.22f * land + 0.05f * SplashRenderer.progress(t, 1000, 2500);
        float victory = SplashRenderer.easeOutBack(SplashRenderer.progress(t, NAME_HIT - 90, 240));
        float spin = now * 0.00012f + (1f - land) * 1.1f;

        SplashRenderer.backdrop(context, -12, -12, width + 12, height + 12, palette);
        SplashRenderer.burst(context, cx, chestY, reach, palette, spin, 1f);
        SplashRenderer.emblem(context, font, SplashRenderer.initial(name), cx, chestY, height * 0.36f, SplashRenderer.fade(0.12f, palette.glow()));
        SplashRenderer.flames(context, -12, width + 12, height + 12, height * 0.45f, palette, now, 0.55f);
        SplashRenderer.sparks(context, cx, chestY, reach * 0.6f, palette, SplashRenderer.progress(t, FLASH_IN, 1100), 40);
        SplashRenderer.fighter(context, profile, cx + shakeX, chestY + shakeY, size * zoom, size * 1.27f, -18f, victory, now);
        SplashRenderer.flames(context, -12, width + 12, height + 12, height * 0.18f, palette, now + 7919, 0.95f);
        SplashRenderer.embers(context, 0, width, height, height * 0.8f, palette, now, 60, 1f);
        if (t >= NAME_HIT) {
            SplashRenderer.focusLines(context, cx, chestY, width, height, 1f - SplashRenderer.progress(t, NAME_HIT, 450));
        }
        extractName(context, font, palette, width, height, t);
        extractSubtitle(context, font, width, height, t, now);
        pose.popMatrix();

        // Letterbox bars, outside the shake
        int bar = (int) (height * 0.075f * SplashRenderer.easeOut(SplashRenderer.progress(t, FLASH_IN, 300)));
        if (bar > 0) {
            context.fill(0, 0, width, bar, 0xFF000000);
            context.fill(0, height - bar, width, height, 0xFF000000);
            context.fill(0, bar, width, bar + 1, SplashRenderer.fade(0.7f, palette.flame()));
            context.fill(0, height - bar - 1, width, height - bar, SplashRenderer.fade(0.7f, palette.flame()));
        }

        // The white-out clearing, and a smaller pop as the name lands
        float flash = 1f - SplashRenderer.easeOut(SplashRenderer.progress(t, FLASH_IN, FLASH_OUT - FLASH_IN));
        if (t >= NAME_HIT) {
            flash = Math.max(flash, 0.4f * (1f - SplashRenderer.progress(t, NAME_HIT, 160)));
        }
        if (flash > 0f) {
            context.fill(0, 0, width, height, SplashRenderer.fade(flash, 0xFFFFFF));
        }
    }

    /** The fighter's name, falling from in front of the screen and speeding up until it slams into place. */
    private static void extractName(GuiGraphicsExtractor context, Font font, SplashRenderer.Palette palette, int width, int height, int t) {
        float drop = SplashRenderer.progress(t, NAME_DROP, NAME_HIT - NAME_DROP);
        if (drop <= 0f) return;
        Component text = Component.literal(name.toUpperCase(Locale.ROOT)).withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC);
        int textWidth = font.width(text);
        float scale = Math.min(height * 0.12f / 8f, width * 0.88f / textWidth);
        float slam = scale * (1f + 1.9f * (1f - drop * drop));
        SplashRenderer.title(context, font, text, (width - textWidth * slam) / 2f, height * 0.72f - 4f * slam, slam,
                0xFFFFFF, palette.pale(), palette.ink(), palette.flame(), Math.min(1f, drop * 2.5f));
    }

    /** "JOINS THE BATTLE!" on a banner that snaps open from the middle, with flames licking up behind the letters. */
    private static void extractSubtitle(GuiGraphicsExtractor context, Font font, int width, int height, int t, long now) {
        float open = SplashRenderer.progress(t, SUBTITLE_IN, 260);
        if (open <= 0f) return;
        Component text = Component.literal("JOINS THE BATTLE!").withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC);
        int textWidth = font.width(text);
        float scale = Math.min(height * 0.045f / 8f, width * 0.7f / textWidth);
        float cx = width / 2f;
        float centreY = height * 0.84f;
        float halfWidth = (textWidth * scale / 2f + 28f) * SplashRenderer.easeOutBack(open);
        float halfHeight = 4f * scale + 5f;
        SplashRenderer.skewFill(context, (int) (cx - halfWidth), (int) (centreY - halfHeight), (int) (cx + halfWidth), (int) (centreY + halfHeight),
                0.35f, 0xE0240404, 0xE0080000);
        SplashRenderer.skewFill(context, (int) (cx - halfWidth), (int) (centreY + halfHeight) - 2, (int) (cx + halfWidth), (int) (centreY + halfHeight),
                0.35f, SplashRenderer.RED, SplashRenderer.RED);

        float letters = SplashRenderer.progress(t, SUBTITLE_IN + 80, 200);
        if (letters <= 0f) return;
        float left = cx - textWidth * scale / 2f;
        float top = centreY - 4f * scale;
        SplashRenderer.flames(context, (int) left, (int) (left + textWidth * scale), (int) (top + 4f * scale), 7f * scale, FIRE, now, letters * 0.85f);
        SplashRenderer.title(context, font, text, left, top, scale, 0xFFFFFF, 0xFFE07A, 0x3A0A00, 0xFF6A00, letters);
    }

    private static void playCues(int t) {
        if (cuesPlayed == 0 && t >= NAME_HIT) {
            cuesPlayed = 1;
            if (t < exitAt) play(SoundEvents.MACE_SMASH_GROUND_HEAVY, 0.9f, 0.55f);
        }
        if (cuesPlayed == 1 && t >= SUBTITLE_IN) {
            cuesPlayed = 2;
            if (t < exitAt) play(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1f, 0.6f);
        }
    }

    private static void play(SoundEvent sound, float pitch, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    private static int elapsed() {
        return (int) (Util.getMillis() - startedAt);
    }
}
