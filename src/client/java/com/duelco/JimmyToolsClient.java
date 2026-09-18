package com.duelco;

import com.duelco._enum.Screen;
import com.duelco.config.ModConfig;
import com.duelco.handlers.BagHandler;
import com.duelco.handlers.TransformationHelperHandler;
import com.duelco.managers.BingoListener;
import com.duelco.managers.DataManager;
import com.duelco.ui.screen.ScreenHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JimmyToolsClient implements ClientModInitializer {
	private static KeyMapping transformationToggleKeybind;
	private static KeyMapping bingoScreenKeybind;
	private static KeyMapping modMenuKeybind;
	private static KeyMapping bagOneKeybind;
	private static KeyMapping bagTwoKeybind;
	private static KeyMapping bagThreeKeybind;
	private static KeyMapping bagFourKeybind;

	public static final Logger LOGGER = LoggerFactory.getLogger("jimmytools-client");

	@Override
	public void onInitializeClient() {
		ModConfig.HANDLER.load();
		registerKeybinds();
		DataManager.loadData();
//		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new BingoListener()); TODO: Check

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (transformationToggleKeybind.isDown()) {
				if (ModConfig.areTransformationsEnabled) {
					TransformationHelperHandler.execute();
				}
			}
			if (bingoScreenKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, client);
			}
			if (bagOneKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(Minecraft.getInstance(), 0);
			}
			if (bagTwoKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 1);
			}
			if (bagThreeKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 2);
			}
			if (bagFourKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 3);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (modMenuKeybind.isDown()) {
				client.gui.setScreen(ModConfig.build().generateScreen(client.gui.screen()));
			}
		});
	}

	private void registerKeybinds() {
		KeyMapping.Category jimmyToolsCategory = KeyMapping.Category.register(
				Identifier.parse("keybinds.category.jimmytools")
		);

		transformationToggleKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.transform", // The translation key of the keybinding's name
				InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				InputConstants.KEY_K, // The keycode of the key
				jimmyToolsCategory // The translation key of the keybinding's category.
		));

		bingoScreenKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.bingo_screen",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_B,
				jimmyToolsCategory
		));

		modMenuKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.modmenu",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_M,
				jimmyToolsCategory
		));

		bagOneKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.openbagone",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_UP,
				jimmyToolsCategory
		));

		bagTwoKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.openbagtwo",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_RIGHT,
				jimmyToolsCategory
		));

		bagThreeKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.openbagthree",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_DOWN,
				jimmyToolsCategory
		));

		bagFourKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.openbagfour",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_LEFT,
				jimmyToolsCategory
		));
	}
}