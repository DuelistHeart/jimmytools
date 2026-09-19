package com.duelco;

import com.duelco._enum.Screen;
import com.duelco.config.ModConfig;
import com.duelco.handlers.BagHandler;
import com.duelco.handlers.CharacterMappingHandler;
import com.duelco.handlers.TransformationHelperHandler;
import com.duelco.managers.CharacterMapperManager;
import com.duelco.managers.DataManager;
import com.duelco.managers.StartupCmdManager;
import com.duelco.ui.hud.tab.CharacterTabList;
import com.duelco.ui.hud.tab.DistrictTabList;
import com.duelco.ui.hud.tab.PlayerTabList;
import com.duelco.ui.hud.tab.TabListRenderer;
import com.duelco.ui.screen.ScreenHandler;
import com.duelco.util.RenderUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JimmyToolsClient implements ClientModInitializer {
	private static KeyMapping transformationToggleKeybind;
	private static KeyMapping modMenuKeybind;
	private static KeyMapping bagOneKeybind;
	private static KeyMapping bagTwoKeybind;
	private static KeyMapping bagThreeKeybind;
	private static KeyMapping bagOfHoldingKeybind;
	private static PlayerTabList playerTabList;
	private static CharacterTabList characterTabList;
	private static DistrictTabList districtTabList;

	private static List<PlayerInfo> playerListEntries = new ArrayList<>();

	public static final Logger LOGGER = LoggerFactory.getLogger("jimmytools-client");

	@Override
	public void onInitializeClient() {
		ModConfig.HANDLER.load();
		registerKeybinds();
		DataManager.loadData();

		playerTabList = new PlayerTabList("player_tab_list");
		characterTabList = new CharacterTabList("character_tab_list");
		districtTabList = new DistrictTabList("district_tab_list");

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (transformationToggleKeybind.consumeClick()) {
				if (ModConfig.areTransformationsEnabled) {
					TransformationHelperHandler.execute();
				}
			}
			while (bagOneKeybind.consumeClick()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 0);
			}
			while (bagTwoKeybind.consumeClick()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 1);
			}
			while (bagThreeKeybind.consumeClick()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 2);
			}
			while (bagOfHoldingKeybind.consumeClick()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 3);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (modMenuKeybind.consumeClick()) {
				ScreenHandler.displayScreen(Screen.CONFIG_SCREEN, client);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			districtTabList.tick();
			StartupCmdManager.tick();
			if (client.getConnection() != null) {
				// Only the custom tab list reads this, and only while it is open. The last snapshot is kept
				// for the close animation, so skip the (sort + regex per entry) rebuild otherwise.
				if (ModConfig.isCustomTablistEnabled && client.options.keyPlayerList.isDown()) {
					// Also refreshes the nearby characters in CharacterMapperManager
					playerListEntries = CharacterMappingHandler.updateFromTabList(client.getConnection());
				}
			} else {
				playerListEntries = new ArrayList<>();
				CharacterMapperManager.setMappings(new ArrayList<>());
			}

			if (DataManager.getDataStore().getTabData().getPlotInfo() != null) {
				ArrayList<Component> districtLines = new ArrayList<>();
				String district = DataManager.getDataStore().getTabData().getPlotInfo().getDistrict();
				districtTabList.setDistrictName(district); // shown in the scroll's header
				String plotOwner = DataManager.getDataStore().getTabData().getPlotInfo().getOwner() == null
						? "Unowned"
						: "Owned By " + DataManager.getDataStore().getTabData().getPlotInfo().getOwner();
				String plotName = DataManager.getDataStore().getTabData().getPlotInfo().getPlot();

				if (plotName != null) {
					districtLines.add(Component.literal(plotName));
					districtLines.add(Component.literal(plotOwner).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC).withoutShadow());
				} else {
					districtLines.add(Component.literal("Public Lands"));
				}
				districtTabList.setData(districtLines);
			} else {
				districtTabList.setData(new ArrayList<>());
			}

			playerTabList.setData(playerListEntries);
			characterTabList.setData(CharacterMapperManager.getPlayers());
		});

		// Register event to render our custom tab list
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("jimmytools", "custom_tab_list"), (drawContext, deltaTracker) -> {
			TabListRenderer.updateLayout();
			float scale = TabListRenderer.getLayoutScale();
			RenderUtils.drawWithScale(drawContext, scale, scale, scale, () -> {
				playerTabList.render(drawContext);
				characterTabList.render(drawContext);
				districtTabList.render(drawContext);
			});
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			ModConfig.HANDLER.save();
		});
	}

	private void registerKeybinds() {
		KeyMapping.Category jimmyToolsCategory = KeyMapping.Category.register(Identifier.parse("jimmytools"));

		transformationToggleKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.transform", // The translation key of the keybinding's name
				InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				InputConstants.KEY_K, // The keycode of the key
				jimmyToolsCategory // The translation key of the keybinding's category.
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

		bagOfHoldingKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.openbagofholding",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_LEFT,
				jimmyToolsCategory
		));
	}
}