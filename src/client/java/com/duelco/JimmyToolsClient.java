package com.duelco;

import com.duelco._enum.Screen;
import com.duelco.config.ModConfig;
import com.duelco.handlers.BagHandler;
import com.duelco.handlers.CharacterMappingHandler;
import com.duelco.handlers.TransformationHelperHandler;
import com.duelco.listeners.BingoListener;
import com.duelco.managers.CharacterMapperManager;
import com.duelco.managers.DataManager;
import com.duelco.obj.general.Player;
import com.duelco.ui.screen.ScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class JimmyToolsClient implements ClientModInitializer {
	private static KeyBinding transformationToggleKeybind;
	private static KeyBinding bingoScreenKeybind;
	private static KeyBinding modMenuKeybind;
	private static KeyBinding bagOneKeybind;
	private static KeyBinding bagTwoKeybind;
	private static KeyBinding bagThreeKeybind;
	private static KeyBinding bagFourKeybind;

	private static int animatedHeight = 0;
	private static final float ANIMATION_SPEED = 5.0f; // Adjust this for faster/slower animation

	private static List<PlayerListEntry> playerListEntries = new ArrayList<>();

	public static final Logger LOGGER = LoggerFactory.getLogger("jimmytools-client");

	@Override
	public void onInitializeClient() {
		ModConfig.HANDLER.load();
		registerKeybinds();
		DataManager.loadData();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new BingoListener());

		// Register event to render our custom tab list
		HudRenderCallback.EVENT.register(this::onRender);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (transformationToggleKeybind.wasPressed()) {
				if (ModConfig.areTransformationsEnabled) {
					TransformationHelperHandler.execute();
				}
			}
			while (bingoScreenKeybind.wasPressed()) {
				ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, client);
			}
			while (bagOneKeybind.wasPressed()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 0);
			}
			while (bagTwoKeybind.wasPressed()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 1);
			}
			while (bagThreeKeybind.wasPressed()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 2);
			}
			while (bagFourKeybind.wasPressed()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 3);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (modMenuKeybind.wasPressed()) {
				ScreenHandler.displayScreen(Screen.CONFIG_SCREEN, client);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.getNetworkHandler() != null) {
				playerListEntries = client.getNetworkHandler().getPlayerList().stream().filter(entry -> {
                    return entry.getDisplayName() != null && !entry.getDisplayName().getString().isEmpty();
				}).toList();
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			ModConfig.HANDLER.save();
		});
	}

	private void registerKeybinds() {
		transformationToggleKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"keybinds.key.jimmytools.transform", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_K, // The keycode of the key
				"keybinds.category.jimmytools" // The translation key of the keybinding's category.
		));

		bingoScreenKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"keybinds.key.jimmytools.bingo_screen",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				"keybinds.category.jimmytools"
		));

		modMenuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"keybinds.key.jimmytools.modmenu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_M,
				"keybinds.category.jimmytools"
		));

		bagOneKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"keybinds.key.jimmytools.openbagone",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UP,
				"keybinds.category.jimmytools"
		));

		bagTwoKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"keybinds.key.jimmytools.openbagtwo",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT,
				"keybinds.category.jimmytools"
		));

		bagThreeKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"keybinds.key.jimmytools.openbagthree",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_DOWN,
				"keybinds.category.jimmytools"
		));

		bagFourKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"keybinds.key.jimmytools.openbagfour",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_LEFT,
				"keybinds.category.jimmytools"
		));
	}

	// Render Custom Tab List (HUD)
	private void onRender(DrawContext context, RenderTickCounter tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		int tabWidth = 200;
		int tabHeight = 150;
		int rightTabPadding = -200;

		if (client == null || client.player == null || client.getNetworkHandler() == null) {
			return;
		}

		CharacterMappingHandler.mapNearbyPlayers();

		int padding = 5;
		int x = client.getWindow().getScaledWidth() / 2 - tabWidth / 2;
		int y = (client.getWindow().getScaledHeight() / 2 - tabHeight / 2) - 5;
		int index = 0;
		int lineHeight = 10;

		if (client.options.playerListKey.isPressed()) {
			// Animate height increase (expands downwards)
			animatedHeight += ANIMATION_SPEED;
			animatedHeight = Math.min(animatedHeight, tabHeight); // Clamp to max height
		} else {
			// Animate height decrease (collapses smoothly)
			animatedHeight -= 8.0f;
			animatedHeight = Math.max(animatedHeight, 0); // Ensure it doesn't go negative
		}

		// Render a background for the custom tab list
		context.fillGradient(x + rightTabPadding, y, x + tabWidth + rightTabPadding, y + animatedHeight, 0xFFFFEBB5, 0xFFFFBD90);

		// Loop through the player list and draw custom tab names with iterator
		for (int i = 0; i < playerListEntries.size(); i++) {
			PlayerListEntry player = playerListEntries.get(i);
            if (y + animatedHeight > y + ((i % 15) * lineHeight) + 4) {
				if (player.getDisplayName() != null && !player.getDisplayName().getString().isEmpty()) {
					// Draw the player's head (size: 16x16 pixels)
					// TODO: Fix the head rendering for hats
					PlayerSkinDrawer.draw(context, player.getSkinTextures().texture(), x + padding + rightTabPadding + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, 8, true, false, -1);

					// Draw the player's name next to their head
					context.drawText(client.textRenderer, player.getDisplayName().getString(), x + padding + 12 + rightTabPadding + (100 * (i / 15)), y + ((i % 15) * lineHeight) + 4, Colors.BLACK, false);
					System.out.println(player.getDisplayName().getString());
					index++;
				}
            }
        }
//		MinecraftClient client = MinecraftClient.getInstance();
//		int tabWidth = 100;
//		int tabHeight = 150;
//		int rightTabPadding = 100;
//
//		if (client == null || client.player == null || client.getNetworkHandler() == null) {
//			return;
//		}
//
//		CharacterMappingHandler.mapNearbyPlayers();
//
//		int padding = 5;
//		int x = client.getWindow().getScaledWidth() / 2 - tabWidth / 2;
//		int y = (client.getWindow().getScaledHeight() / 2 - tabHeight / 2) - 5;
//		int index = 0;
//		int lineHeight = 10;
//
//		if (client.options.playerListKey.isPressed()) {
//			// Animate height increase (expands downwards)
//			animatedHeight += ANIMATION_SPEED;
//			animatedHeight = Math.min(animatedHeight, tabHeight); // Clamp to max height
//		} else {
//			// Animate height decrease (collapses smoothly)
//			animatedHeight -= 8.0f;
//			animatedHeight = Math.max(animatedHeight, 0); // Ensure it doesn't go negative
//		}
//
//		// Render a background for the custom tab list
//		context.fillGradient(x + rightTabPadding, y, x + tabWidth + rightTabPadding, y + animatedHeight, 0xFFFFEBB5, 0xFFFFBD90);
//
//		List<Player> players = CharacterMapperManager.getPlayers();
//
//		// Loop through the player list and draw custom tab names
//		for (int i = 0; i < players.size(); i++) {
//
//			if (animatedHeight > y + (index * lineHeight) + 4) {
//				// Draw the player's head (size: 16x16 pixels)
//				// TODO: Fix the head rendering for hats
//				PlayerSkinDrawer.draw(context, players.get(i).getSkinTexture(), x + padding + rightTabPadding, y + (index * lineHeight) + 4, 8, true, false, -1);
//
//				// Draw the player's name next to their head
//				context.drawText(client.textRenderer, players.get(i).getCharacterName(), x + padding + 12 + rightTabPadding, y + (index * lineHeight) + 4, Colors.BLACK, false);
//
//				index++;
//			}
//		}
	}

	public static List<PlayerEntity> getNearbyPlayers(PlayerEntity player, int count) {
		if (player == null || player.getWorld() == null) {
			return List.of();
		}

		Vec3d playerPos = player.getPos(); // Get player's current position

        // Return a list of players sorted by their username to the player
		return player.getWorld().getPlayers().stream()
				.sorted(Comparator.comparing((PlayerEntity p) -> p.getName().getString()))
				.limit(count) // Get the top X closest players (including self)
				.collect(Collectors.toList());
	}
}