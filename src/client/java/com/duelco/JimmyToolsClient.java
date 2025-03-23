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
import com.duelco.ui.hud.tab.CharacterTabList;
import com.duelco.ui.hud.tab.PlayerTabList;
import com.duelco.ui.screen.ScreenHandler;
import com.duelco.util.RenderUtils;
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
	private static PlayerTabList playerTabList;
	private static CharacterTabList characterTabList;

	private static List<PlayerListEntry> playerListEntries = new ArrayList<>();

	public static final Logger LOGGER = LoggerFactory.getLogger("jimmytools-client");

	@Override
	public void onInitializeClient() {
		ModConfig.HANDLER.load();
		registerKeybinds();
		DataManager.loadData();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new BingoListener());

		playerTabList = new PlayerTabList("player_tab_list");
		characterTabList = new CharacterTabList("character_tab_list");

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
                    return entry.getDisplayName() != null && !entry.getDisplayName().getString().isEmpty()
							&& entry.getDisplayName().getSiblings().size() == 1
							&& !List.of("Nearby", "Build Server", "Server").contains(entry.getDisplayName().getString());
				}).toList();
			}
			playerTabList.setData(playerListEntries);
			characterTabList.setData(CharacterMapperManager.getPlayers());
		});

		// Register event to render our custom tab list
		HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
			RenderUtils.drawWithScale(drawContext, 0.95f, 0.95f, 0.95f, () -> {
				playerTabList.render(drawContext);
			});
		});
		HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
			RenderUtils.drawWithScale(drawContext, 0.95f, 0.95f, 0.95f, () -> {
				characterTabList.render(drawContext);
			});
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
}