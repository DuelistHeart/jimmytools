package com.duelco;

import com.duelco._enum.Screen;
import com.duelco.config.ModConfig;
import com.duelco.handlers.BagHandler;
import com.duelco.handlers.TransformationHelperHandler;
import com.duelco.listeners.BingoListener;
import com.duelco.managers.DataManager;
import com.duelco.ui.screen.ScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JimmyToolsClient implements ClientModInitializer {
	private static KeyBinding transformationToggleKeybind;
	private static KeyBinding bingoScreenKeybind;
	private static KeyBinding modMenuKeybind;
	private static KeyBinding bagOneKeybind;
	private static KeyBinding bagTwoKeybind;
	private static KeyBinding bagThreeKeybind;
	private static KeyBinding bagFourKeybind;

	private static int windowWidth = 300;
	private static int windowHeight = 450;

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
		int tabWidth = 100;
		int tabHeight = 150;

		if (client == null || client.player == null || client.getNetworkHandler() == null) {
			return;
		}

		Collection<PlayerListEntry> players = client.getNetworkHandler().getPlayerList();

		int x = client.getWindow().getScaledWidth() / 2 - tabWidth / 2;
		int y = (client.getWindow().getScaledHeight() / 2 - tabHeight / 2) - 5;
		int index = 0;
		int lineHeight = 20;

		// Execute code if the TAB key is currently being pressed
		if (client.options.playerListKey.isPressed()) {
			// Render a background for the custom tab list
			context.fillGradient(x, y, x + tabWidth, y + tabHeight, 0x80000000, 0x80000000);

			List<PlayerListEntry> nearbyPlayerEntries = players.stream().toList().stream().filter(entry -> {
						if (entry.getDisplayName() != null) {
							return entry.getDisplayName().getSiblings().size() == 2;
						} else {
							return false;
						}
					}).toList();

			List<PlayerEntity> nearbyPlayers = getNearbyPlayers(client.player, nearbyPlayerEntries.size());

			// Loop through the player list and draw custom tab names
			for (int i = 0; i < nearbyPlayers.size(); i++) {
				PlayerListEntry entry = nearbyPlayerEntries.get(i);
				PlayerEntity player = nearbyPlayers.get(i);

				// Get the player's skin texture
				Identifier skinTexture = ((AbstractClientPlayerEntity) player).getSkinTextures().texture();

				// Draw the player's head (size: 16x16 pixels)
				PlayerSkinDrawer.draw(context, skinTexture, x, y + (index * lineHeight) + 4, 16, entry.shouldShowHat(), false, -1);

				// Draw the player's name next to their head
				context.drawText(client.textRenderer, entry.getDisplayName().getSiblings().get(1).getString(), x + 24, y + (index * lineHeight) + 4, 0xFFFFFF, false);

				index++;
			}
//			for (PlayerListEntry entry : players) {
////				if (!Objects.equals(playerName, "")) {
//				if (entry.getDisplayName() != null && entry.getDisplayName().getSiblings().size() == 2) {
//
//					// Get the player's skin texture
//					Identifier skinTexture = entry.getSkinTextures().texture();
//
//					// Draw the player's head (size: 16x16 pixels)
//					PlayerSkinDrawer.draw(context, skinTexture, x, y + (index * lineHeight) + 4, 16, entry.shouldShowHat(), false, -1);
//
//					// Draw the player's name next to their head
//					context.drawText(client.textRenderer, entry.getDisplayName().getSiblings().get(1).getString(), x + 24, y + (index * lineHeight) + 4, 0xFFFFFF, false);
//
//					index++;
//				}
//			}
		}
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