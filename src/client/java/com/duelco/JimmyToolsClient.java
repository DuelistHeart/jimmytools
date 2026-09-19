package com.duelco;

import com.duelco._enum.Screen;
import com.duelco.config.ModConfig;
import com.duelco.handlers.BagHandler;
import com.duelco.handlers.FeatureFlagHandler;
import com.duelco.handlers.TransformationHelperHandler;
import com.duelco.listeners.BingoListener;
import com.duelco.managers.CharacterMapperManager;
import com.duelco.managers.DataManager;
import com.duelco.ui.hud.tab.CharacterTabList;
import com.duelco.ui.hud.tab.DistrictTabList;
import com.duelco.ui.hud.tab.PlayerTabList;
import com.duelco.ui.screen.ScreenHandler;
import com.duelco.util.RenderUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JimmyToolsClient implements ClientModInitializer {
	private static KeyMapping transformationToggleKeybind;
	private static KeyMapping bingoScreenKeybind;
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
//		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new BingoListener());

		playerTabList = new PlayerTabList("player_tab_list");
		characterTabList = new CharacterTabList("character_tab_list");
		districtTabList = new DistrictTabList("district_tab_list");

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (transformationToggleKeybind.isDown()) {
				if (ModConfig.areTransformationsEnabled) {
					TransformationHelperHandler.execute();
				}
			}
			while (bingoScreenKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.BINGO_CARDS_SCREEN, client);
			}
			while (bagOneKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 0);
			}
			while (bagTwoKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 1);
			}
			while (bagThreeKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 2);
			}
			while (bagOfHoldingKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.INVENTORY_SCREEN, client);
				BagHandler.clickCraftingSlot(client, 3);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (modMenuKeybind.isDown()) {
				ScreenHandler.displayScreen(Screen.CONFIG_SCREEN, client);
			}
		});

		if (FeatureFlagHandler.isCustomTablistEnabled()) {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (client.getConnection() != null) {
					playerListEntries = client.getConnection().getOnlinePlayers().stream().filter(entry -> {
						return entry.getTabListDisplayName() != null && !entry.getTabListDisplayName().getString().isEmpty()
								&& entry.getTabListDisplayName().getSiblings().size() == 1
								&& !List.of("Nearby", "Build Server", "Server").contains(entry.getTabListDisplayName().getString());
					}).toList();
				}

				if (DataManager.getDataStore().getTabData().getPlotInfo() != null) {
					ArrayList<String> testData = new ArrayList<>();
					String district = DataManager.getDataStore().getTabData().getPlotInfo().getDistrict();
					districtTabList.setDistrictName(district);
					testData.add("District: " + district);
					String plotOwner = DataManager.getDataStore().getTabData().getPlotInfo().getOwner() == null
							? "Unowned"
							: DataManager.getDataStore().getTabData().getPlotInfo().getOwner();
					String plotName = DataManager.getDataStore().getTabData().getPlotInfo().getPlot();

					if (plotName != null) {
						testData.add("Plot: " + plotName);
						testData.add("Owned by: " + plotOwner);
					}
					districtTabList.setData(testData);
				} else {
					districtTabList.setData(new ArrayList<>());
				}

				playerTabList.setData(playerListEntries);
				characterTabList.setData(CharacterMapperManager.getPlayers());
			});

			// Register event to render our custom tab list
			HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("jimmytools", "custom_tab_list"), (drawContext, deltaTracker) -> {
				RenderUtils.drawWithScale(drawContext, 0.95f, 0.95f, 0.95f, () -> {
					playerTabList.render(drawContext);
					characterTabList.render(drawContext);
					districtTabList.render(drawContext);
				});
			});
		}

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

		bagOfHoldingKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"keybinds.key.jimmytools.openbagofholding",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_LEFT,
				jimmyToolsCategory
		));
	}
}