package com.duelco.gametest;

import com.duelco.ui.screen.CharacterSelectScreen;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Records the custom /char screen with a 60-character roster: the screen opening, the cursor hovering across the
 * roster, then a pick and its reveal. Frames land in {@code <run dir>/recording} with their timestamps in
 * {@code frames.txt}, ready to be stitched into a video.
 *
 * <p>The game clock is slowed down while recording so frame captures keep up with the animations; each frame's
 * timestamp is read on the client right before it is rendered, so playback at those timestamps runs at real speed.
 */
public class CharacterSelectRecording implements FabricClientGameTest {
    private static final int CHARACTERS = 60;
    private static final int WINDOW_WIDTH = 1706; // a 853x456 GUI at scale 2, the same layout as 2560x1368 at scale 3
    private static final int WINDOW_HEIGHT = 912;
    private static final int GUI_SCALE = 2;
    private static final double SLOWDOWN = 0.15;

    private static final int INTRO_MS = 2200;
    private static final int MOVE_MS = 300;
    private static final int DWELL_MS = 550;
    private static final int AFTER_CLICK_MS = 4600;
    private static final int[] VISITS = {0, 6, 13, 21, 29, 35, 42, 49, 59, 24}; // last one gets picked

    private static final String[] NAMES = {
            "Roxi", "Aldric Thorne", "Brynn", "Cedric Vale", "Dahlia", "Edmund Blackwood", "Fenna", "Gareth", "Helewise",
            "Isolde Marrow", "Jory", "Kestrel", "Lucan Ashford", "Maren", "Nim", "Osric the Bold", "Perrin", "Quill",
            "Rowena Bramble", "Silas", "Tamsin", "Ulric Stonehand", "Vesper", "Wren", "Xander", "Ysolde", "Zephyrine",
            "Agnes", "Bartholomew Ironwood-Ashcombe", "Corwin", "Delphine", "Elric", "Faye", "Godric", "Hollis",
            "Ingrid", "Jasper Quell", "Kael", "Lysandra", "Morwen", "Nessa", "Orin", "Pip", "Rhiannon", "Sorrel",
            "Thaddeus", "Una", "Valen", "Wilhelmina", "Yara", "Zoltan", "Ansel", "Briar", "Calla", "Dorian Grey",
            "Elowen", "Fitz", "Gwendolyn", "Hale", "Ivy"
    };

    @Override
    public void runTest(ClientGameTestContext context) {
        context.getInput().resizeWindow(WINDOW_WIDTH, WINDOW_HEIGHT);
        context.runOnClient(client -> {
            client.options.guiScale().set(GUI_SCALE);
            client.resizeGui();
        });

        try (TestSingleplayerContext ignored = context.worldBuilder().create()) {
            context.waitTicks(40);
            context.setScreen(CharacterSelectRecording::createScreen);
            Path output = FabricLoader.getInstance().getGameDir().resolve("recording");
            try {
                record(context, output);
            } finally {
                Util.setTimeSource(System::nanoTime);
            }
            context.setScreen(() -> null);
        }
    }

    private static void record(ClientGameTestContext context, Path output) {
        long base = System.nanoTime();
        Util.setTimeSource(() -> base + (long) ((System.nanoTime() - base) * SLOWDOWN));

        // Where each visited tile's centre is, once the screen has laid the roster out
        context.waitTicks(2);
        List<double[]> stops = context.computeOnClient(client -> tileCentres(client));
        double restX = 853 * 0.42;
        double restY = 456 * 0.4;
        int end = INTRO_MS + VISITS.length * (MOVE_MS + DWELL_MS) + AFTER_CLICK_MS;
        int clickAt = INTRO_MS + VISITS.length * (MOVE_MS + DWELL_MS) - DWELL_MS / 3;

        List<String> timestamps = new ArrayList<>();
        long start = context.computeOnClient(client -> Util.getMillis());
        boolean clicked = false;
        int frame = 0;
        while (true) {
            long t = context.computeOnClient(client -> Util.getMillis()) - start;
            if (t > end) break;

            double[] cursor = cursorAt(t, stops, restX, restY);
            context.getInput().setCursorPos(cursor[0] * GUI_SCALE, cursor[1] * GUI_SCALE);
            if (!clicked && t >= clickAt) {
                context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
                clicked = true;
            }

            long renderedAt = context.computeOnClient(client -> Util.getMillis()) - start;
            context.takeScreenshot(TestScreenshotOptions.of(String.format(Locale.ROOT, "frame_%05d", frame))
                    .disableCounterPrefix()
                    .withDestinationDir(output));
            timestamps.add(String.format(Locale.ROOT, "frame_%05d.png %d", frame, renderedAt));
            frame++;
        }

        try {
            Files.write(output.resolve("frames.txt"), timestamps, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Rests during the intro, then glides from tile to tile with a pause on each. */
    private static double[] cursorAt(long t, List<double[]> stops, double restX, double restY) {
        double[] from = {restX, restY};
        long local = t - INTRO_MS;
        if (local < 0) return from;
        for (double[] stop : stops) {
            if (local < MOVE_MS) {
                float p = local / (float) MOVE_MS;
                float eased = p < 0.5f ? 2 * p * p : 1 - (float) Math.pow(-2 * p + 2, 2) / 2;
                return new double[]{Mth.lerp(eased, from[0], stop[0]), Mth.lerp(eased, from[1], stop[1])};
            }
            local -= MOVE_MS;
            if (local < DWELL_MS) return stop;
            local -= DWELL_MS;
            from = stop;
        }
        return from;
    }

    private static List<double[]> tileCentres(Minecraft client) {
        try {
            CharacterSelectScreen screen = (CharacterSelectScreen) client.gui.screen();
            Field tileSize = CharacterSelectScreen.class.getDeclaredField("tileSize");
            Field plateHeight = CharacterSelectScreen.class.getDeclaredField("plateHeight");
            tileSize.setAccessible(true);
            plateHeight.setAccessible(true);
            int size = tileSize.getInt(screen);
            int plate = plateHeight.getInt(screen);
            List<double[]> centres = new ArrayList<>();
            for (int index : VISITS) {
                Slot slot = screen.getMenu().slots.get(index);
                centres.add(new double[]{slot.x + size / 2.0, slot.y + (size + plate) / 2.0});
            }
            return centres;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /** The roster screen over a client-side chest of player heads, like the one the server sends for /char. */
    private static CharacterSelectScreen createScreen() {
        Minecraft client = Minecraft.getInstance();
        SimpleContainer container = new SimpleContainer(63);
        for (int i = 0; i < CHARACTERS; i++) {
            container.setItem(i, head(NAMES[i], i));
        }
        Inventory inventory = client.player.getInventory();
        ChestMenu menu = new ChestMenu(MenuType.GENERIC_9x6, 1, inventory, container, 7);
        try {
            Constructor<CharacterSelectScreen> constructor =
                    CharacterSelectScreen.class.getDeclaredConstructor(ChestMenu.class, Inventory.class, Component.class);
            constructor.setAccessible(true);
            return constructor.newInstance(menu, inventory, Component.literal("DuelistHeart's Characters"));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /** A character head with the server's name style and hints; skins fall back to the default skin for its UUID. */
    private static ItemStack head(String name, int index) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        UUID id = UUID.nameUUIDFromBytes(("jimmytools-recording-" + name).getBytes(StandardCharsets.UTF_8));
        stack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(new GameProfile(id, "Character" + index)));
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name).withStyle(style -> style.withItalic(false).withColor(ChatFormatting.YELLOW)));
        stack.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal("L-Click: Select character.").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.AQUA)),
                Component.literal("F: Begin character deletion.").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.AQUA)))));
        return stack;
    }
}
