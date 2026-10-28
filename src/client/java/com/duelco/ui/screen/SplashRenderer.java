package com.duelco.ui.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.util.Locale;
import java.util.OptionalInt;

/**
 * Drawing pieces shared by the roster's stage and the "JOINS THE BATTLE!" reveal: the backdrop, light burst, emblem,
 * flames, embers, the posed fighter model and outlined title text. Every effect is worked out from the clock, so
 * nothing here keeps per-frame state.
 */
final class SplashRenderer {
    static final int RED = 0xFFE8283C;
    static final int DARK_RED = 0xFF7A0E1C;
    static final int GOLD = 0xFFFFD23F;
    static final int WHITE = 0xFFFFFFFF;
    static final int GREY = 0xFFA8A8B8;

    private static final float CHEST = 1.2f; // blocks above the feet; the fighter is placed and zoomed about this point

    private static Model.Simple wideModel;
    private static Model.Simple slimModel;

    private SplashRenderer() {
    }

    /** A fighter's colours, all from one hue so each character keeps its own look. Colours without alpha are RGB. */
    record Palette(int skyTop, int skyBottom, int glow, int ray, int flame, int flameCore, int pale, int ink) {
        static Palette of(float hue) {
            return new Palette(
                    ARGB.opaque(Mth.hsvToRgb(hue, 0.85f, 0.08f)),
                    ARGB.opaque(Mth.hsvToRgb(hue, 0.9f, 0.4f)),
                    Mth.hsvToRgb(hue, 0.55f, 1f),
                    Mth.hsvToRgb(hue, 0.2f, 1f),
                    Mth.hsvToRgb(hue, 0.95f, 1f),
                    Mth.hsvToRgb(Mth.frac(hue + 0.08f), 0.35f, 1f),
                    Mth.hsvToRgb(hue, 0.22f, 1f),
                    Mth.hsvToRgb(hue, 0.9f, 0.12f));
        }
    }

    // ---- Backdrop ----

    static void backdrop(GuiGraphicsExtractor context, int x0, int y0, int x1, int y1, Palette palette) {
        context.fillGradient(x0, y0, x1, y1, palette.skyTop(), palette.skyBottom());
        int edge = (x1 - x0) / 5;
        horizontalGradient(context, x0, y0, x0 + edge, y1, 0x90000000, 0x00000000);
        horizontalGradient(context, x1 - edge, y0, x1, y1, 0x00000000, 0x90000000);
    }

    /** Light pouring out from behind the fighter: a soft glowing core and a slowly turning ring of rays. */
    static void burst(GuiGraphicsExtractor context, float cx, float cy, float reach, Palette palette, float spin, float strength) {
        Matrix3x2fStack pose = context.pose();
        // Stacked squares at different angles blend into a roughly round glow, brightest in the middle
        for (int i = 5; i >= 0; i--) {
            float size = reach * (0.05f + i * 0.06f);
            pose.pushMatrix();
            pose.translate(cx, cy);
            pose.rotate(spin * 0.5f + i * 0.26f);
            pose.scale(size, size);
            context.fill(-1, -1, 1, 1, fade(strength * (0.05f + (5 - i) * 0.025f), palette.glow()));
            pose.popMatrix();
        }

        // Each ray is a fan of thin beams, so it widens and breaks into streaks as it travels outward
        int rays = 14;
        for (int r = 0; r < rays; r++) {
            boolean major = r % 2 == 0;
            float angle = spin + r * Mth.TWO_PI / rays;
            float length = reach * (major ? 1f : 0.72f);
            int colour = fade(strength * (major ? 0.2f : 0.11f), palette.ray());
            for (int f = -2; f <= 2; f++) {
                beam(context, cx, cy, angle + f * 0.016f, length, colour);
            }
        }
    }

    private static void beam(GuiGraphicsExtractor context, float cx, float cy, float angle, float length, int colour) {
        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        pose.translate(cx, cy);
        pose.rotate(angle);
        context.fillGradient(-1, 0, 2, (int) length, colour, colour & 0x00FFFFFF);
        pose.popMatrix();
    }

    /** A huge faint ring and letter behind the fighter, standing in for a series emblem. */
    static void emblem(GuiGraphicsExtractor context, Font font, String glyph, float cx, float cy, float radius, int colour) {
        Matrix3x2fStack pose = context.pose();
        int segments = 48;
        float thickness = Math.max(2f, radius * 0.06f);
        float chord = 2f * radius * Mth.sin(Mth.PI / segments) + 0.5f;
        for (int i = 0; i < segments; i++) {
            pose.pushMatrix();
            pose.translate(cx, cy);
            pose.rotate(i * Mth.TWO_PI / segments);
            pose.translate(0, -radius);
            pose.scale(chord / 2f, thickness / 2f);
            context.fill(-1, -1, 1, 1, colour);
            pose.popMatrix();
        }

        Component letter = Component.literal(glyph).withStyle(ChatFormatting.BOLD);
        float scale = radius * 1.1f / 8f;
        pose.pushMatrix();
        pose.translate(cx, cy);
        pose.rotate(-0.14f);
        pose.scale(scale, scale);
        context.text(font, letter, -font.width(letter) / 2, -4, colour, false);
        pose.popMatrix();
    }

    // ---- Fire ----

    /** A wall of flickering flame tongues standing on {@code baseY}, with a paler, shorter core in front. */
    static void flames(GuiGraphicsExtractor context, int x0, int x1, int baseY, float height, Palette palette, long now, float strength) {
        if (strength <= 0f || x1 <= x0) return;
        float t = now / 1000f;
        int step = Math.max(3, (x1 - x0) / 150);
        int flame = fade(strength * 0.9f, palette.flame());
        int core = fade(strength, palette.flameCore());
        context.fillGradient(x0, (int) (baseY - height * 0.45f), x1, baseY, palette.flame(), fade(strength * 0.55f, palette.flame()));
        for (int x = x0; x < x1; x += step) {
            int h = (int) (height * flicker(x, t));
            context.fillGradient(x, baseY - h, x + step, baseY, flame & 0x00FFFFFF, flame);
        }
        for (int x = x0; x < x1; x += step) {
            int h = (int) (height * 0.5f * flicker(x + 91, t * 1.4f));
            context.fillGradient(x, baseY - h, x + step, baseY, core & 0x00FFFFFF, core);
        }
    }

    private static float flicker(float x, float t) {
        float n = 0.5f + 0.24f * Mth.sin(x * 0.043f + t * 2.6f) + 0.16f * Mth.sin(x * 0.121f - t * 4.3f) + 0.1f * Mth.sin(x * 0.29f + t * 8.1f);
        return Mth.clamp(n, 0.06f, 1f);
    }

    /** Sparks drifting up out of the fire, cooling from white-hot to the flame colour as they rise. */
    static void embers(GuiGraphicsExtractor context, int x0, int x1, int baseY, float rise, Palette palette, long now, int count, float strength) {
        if (strength <= 0f) return;
        Matrix3x2fStack pose = context.pose();
        for (int i = 0; i < count; i++) {
            float seed = noise(i);
            float period = 1500f + seed * 2200f;
            float life = ((now + seed * 10000f) % period) / period;
            float x = x0 + noise(i + 101) * (x1 - x0) + Mth.sin(life * 7f + i) * 6f;
            float size = 1f + (i % 3 == 0 ? 1f : 0f) + (i % 7 == 0 ? 1f : 0f);
            float alpha = strength * (1f - life) * (0.65f + 0.35f * Mth.sin(now / 55f + i * 1.7f));
            pose.pushMatrix();
            pose.translate(x, baseY - life * rise);
            pose.scale(size, size);
            context.fill(0, 0, 1, 1, fade(alpha, ARGB.srgbLerp(life, 0xFFFFF4C0, palette.flame())));
            pose.popMatrix();
        }
    }

    /** A burst of sparks flying out from a point; {@code progress} runs 0 to 1 over the burst's life. */
    static void sparks(GuiGraphicsExtractor context, float cx, float cy, float reach, Palette palette, float progress, int count) {
        if (progress <= 0f || progress >= 1f) return;
        Matrix3x2fStack pose = context.pose();
        float travel = easeOut(progress);
        for (int i = 0; i < count; i++) {
            float angle = noise(i + 300) * Mth.TWO_PI;
            float distance = reach * (0.3f + 0.7f * noise(i + 400)) * travel;
            float size = 1.5f + noise(i + 500) * 2f;
            pose.pushMatrix();
            pose.translate(cx + Mth.cos(angle) * distance, cy + Mth.sin(angle) * distance + progress * progress * reach * 0.15f);
            pose.rotate(angle);
            pose.scale(size * (1f + 3f * (1f - travel)), size); // streaks while fast, dots as they slow
            context.fill(-1, 0, 1, 1, fade(1f - progress, ARGB.srgbLerp(progress, 0xFFFFF4C0, palette.flame())));
            pose.popMatrix();
        }
    }

    /** Speed lines rushing in from the screen's edges toward a point, for the moment of impact. */
    static void focusLines(GuiGraphicsExtractor context, float cx, float cy, int width, int height, float alpha) {
        if (alpha <= 0f) return;
        Matrix3x2fStack pose = context.pose();
        int lines = 30;
        for (int i = 0; i < lines; i++) {
            float angle = (i + noise(i + 600) * 0.6f) * Mth.TWO_PI / lines;
            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);
            // Distance from the point to the screen's edge this way, so the lines hug the border on any window shape
            float edgeX = cos > 0 ? (width - cx) / cos : cos < 0 ? cx / -cos : Float.MAX_VALUE;
            float edgeY = sin > 0 ? (height - cy) / sin : sin < 0 ? cy / -sin : Float.MAX_VALUE;
            float edge = Math.min(edgeX, edgeY);
            int thickness = i % 3 == 0 ? 2 : 1;
            pose.pushMatrix();
            pose.translate(cx, cy);
            pose.rotate(angle - Mth.HALF_PI); // local +y points along the angle
            context.fillGradient(-thickness, (int) (edge * (0.55f + noise(i + 700) * 0.25f)), thickness, (int) edge + 16,
                    0x00FFFFFF, fade(alpha * 0.85f, 0xFFFFFF));
            pose.popMatrix();
        }
    }

    // ---- Fighter ----

    /**
     * The fighter's full skin model, posed. Its chest goes at {@code (x, chestY)} and {@code size} is its scale in
     * GUI pixels per block. The render area is sized from {@code maxSize} so that zooming up to it doesn't resize the
     * model's off-screen texture every frame. Unlike everything else here, the model ignores the pose stack.
     */
    static void fighter(GuiGraphicsExtractor context, ResolvableProfile profile, float x, float chestY, float size, float maxSize,
                        float yaw, float victory, long now) {
        PlayerSkin skin = Minecraft.getInstance().playerSkinRenderCache().getOrDefault(profile).playerSkin();
        Model.Simple model = model(skin.model());
        pose(model.root(), victory, now);
        int x0 = Math.round(x - maxSize);
        int y1 = Math.round(chestY + (CHEST + 0.1f) * size); // vanilla stands the model 0.1 blocks above the area's bottom
        context.skin(model, skin.body().texturePath(), size, -4f, yaw, -1.0625f,
                x0, y1 - Math.round(2.6f * maxSize), x0 + Math.round(2f * maxSize), y1);
    }

    private static Model.Simple model(PlayerModelType type) {
        if (wideModel == null) {
            EntityModelSet models = Minecraft.getInstance().getEntityModels();
            wideModel = new Model.Simple(models.bakeLayer(ModelLayers.PLAYER), RenderTypes::entityTranslucent);
            slimModel = new Model.Simple(models.bakeLayer(ModelLayers.PLAYER_SLIM), RenderTypes::entityTranslucent);
        }
        return type == PlayerModelType.SLIM ? slimModel : wideModel;
    }

    /** Blends from a mid-stride ready stance (0) into a fist-raised victory pose (1), breathing a little either way. */
    private static void pose(ModelPart root, float victory, long now) {
        float breathe = Mth.sin(now / 420f) * 0.05f;
        root.getChild("head").setRotation(Mth.lerp(victory, -0.05f, -0.22f) + breathe * 0.3f, 0f, 0f);
        // Once the arm is raised overhead, a negative roll is what swings it outward, clear of the head
        root.getChild("right_arm").setRotation(Mth.lerp(victory, -0.6f, -2.75f) + breathe, 0f, Mth.lerp(victory, 0.14f, -0.4f));
        root.getChild("left_arm").setRotation(Mth.lerp(victory, 0.45f, 0.12f) - breathe, 0f, Mth.lerp(victory, -0.14f, -0.6f));
        root.getChild("right_leg").setRotation(Mth.lerp(victory, 0.3f, 0.04f), 0f, Mth.lerp(victory, 0.05f, 0.14f));
        root.getChild("left_leg").setRotation(Mth.lerp(victory, -0.34f, -0.04f), 0f, Mth.lerp(victory, -0.05f, -0.14f));
    }

    // ---- Text ----

    /**
     * Big title text: a glowing outer stroke, a dark outline, then the letters in two tones (top half, bottom half).
     * Drawn from its top-left corner; a {@code glow} of 0 skips the outer stroke. The strokes stay within about a font
     * pixel of the letters, since copies offset any further would show up as separate ghost letters at large scales.
     */
    static void title(GuiGraphicsExtractor context, Font font, Component text, float x, float y, float scale,
                      int top, int bottom, int outline, int glow, float alpha) {
        if (alpha <= 0f) return;
        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(scale, scale);
        if (glow != 0) {
            ring(context, font, text, 1.15f, fade(alpha * 0.22f, glow));
            ring(context, font, text, 0.8f, fade(alpha * 0.45f, glow));
        }
        ring(context, font, text, 0.45f, fade(alpha, outline));
        context.text(font, text, 0, 0, fade(alpha, top), false);
        context.enableScissor(-2, 4, font.width(text) + 2, 9);
        context.text(font, text, 0, 0, fade(alpha, bottom), false);
        context.disableScissor();
        pose.popMatrix();
    }

    /** The text drawn eight times around its spot, for outlines and glows. */
    private static void ring(GuiGraphicsExtractor context, Font font, Component text, float radius, int colour) {
        Matrix3x2fStack pose = context.pose();
        for (int i = 0; i < 8; i++) {
            float angle = i * Mth.TWO_PI / 8;
            pose.pushMatrix();
            pose.translate(Mth.cos(angle) * radius, Mth.sin(angle) * radius);
            context.text(font, text, 0, 0, colour, false);
            pose.popMatrix();
        }
    }

    static void scaledText(GuiGraphicsExtractor context, Font font, Component text, float x, float y, float scale, int colour) {
        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(scale, scale);
        context.text(font, text, 0, 0, colour, true);
        pose.popMatrix();
    }

    // ---- Shapes ----

    /** A parallelogram leaning right ("/"), its bottom edge at x0..x1. */
    static void skewFill(GuiGraphicsExtractor context, int x0, int y0, int x1, int y1, float skew, int top, int bottom) {
        skewFill(context, x0, y0, x1, y1, skew, top, bottom, y1);
    }

    /** As above, but slanted about {@code baseY}, so a strip inside a bigger slanted shape lines up with its edges. */
    static void skewFill(GuiGraphicsExtractor context, int x0, int y0, int x1, int y1, float skew, int top, int bottom, int baseY) {
        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        pose.translate(0, baseY);
        pose.mul(new Matrix3x2f(1, 0, -skew, 1, 0, 0));
        context.fillGradient(x0, y0 - baseY, x1, y1 - baseY, top, bottom);
        pose.popMatrix();
    }

    /** fillGradient running left to right instead of top to bottom. */
    static void horizontalGradient(GuiGraphicsExtractor context, int x0, int y0, int x1, int y1, int left, int right) {
        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        pose.rotate(-Mth.HALF_PI); // local +y now runs along screen +x, and local +x along screen -y
        context.fillGradient(-y1, x0, -y0, x1, left, right);
        pose.popMatrix();
    }

    // ---- Helpers ----

    /** An RGB colour at the given opacity, clamped to 0..1. */
    static int fade(float alpha, int rgb) {
        return ARGB.color(Mth.clamp(alpha, 0f, 1f), rgb);
    }

    /** A stable per-character hue, so each fighter keeps its colour between visits. */
    static float hue(String name) {
        return (name.hashCode() * 0x9E3779B1 >>> 16 & 0xFFFF) / 65536f;
    }

    /** The first letter or digit of a name, upper-cased, for the emblem; "?" when it has none. */
    static String initial(String name) {
        OptionalInt first = name.codePoints().filter(Character::isLetterOrDigit).findFirst();
        return first.isPresent() ? Character.toString(first.getAsInt()).toUpperCase(Locale.ROOT) : "?";
    }

    /** A stable pseudo-random value in [0, 1) for an index. */
    private static float noise(int i) {
        return ((i * 0x9E3779B1 + 0x7F4A7C15) >>> 8 & 0xFFFF) / 65536f;
    }

    /** How far {@code elapsed} is through a span starting at {@code start}, clamped to 0..1. */
    static float progress(float elapsed, float start, float duration) {
        return Mth.clamp((elapsed - start) / duration, 0f, 1f);
    }

    static float easeOut(float t) {
        float u = 1f - t;
        return 1f - u * u * u;
    }

    static float easeInOut(float t) {
        return t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    }

    static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float u = t - 1f;
        return 1f + (c1 + 1f) * u * u * u + c1 * u * u;
    }
}
