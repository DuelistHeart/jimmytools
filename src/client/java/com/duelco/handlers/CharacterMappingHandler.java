package com.duelco.handlers;

import com.duelco.managers.CharacterMapperManager;
import com.duelco.mixin.client.PlayerTabOverlayAccessor;
import com.duelco.obj.general.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

/**
 * Splits the server's tab list into its sections. The server lays the list out in vanilla order as:
 * <pre>
 *   Server   (fake entry)  -> online players (columns 1 and 2, padded with blank entries)
 *   Nearby   (fake entry)  -> characters near the local player (column 3)
 * </pre>
 */
public class CharacterMappingHandler {
    private static final int MAX_TAB_ENTRIES = 80; // vanilla's own cap
    private static final Set<String> SERVER_MARKERS = Set.of("server", "build server");
    private static final String NEARBY_MARKER = "nearby";

    private enum Section {NONE, SERVER, NEARBY}

    /**
     * Re-reads the tab list, stores the nearby characters in {@link CharacterMapperManager}
     * and returns the online players.
     */
    public static List<PlayerInfo> updateFromTabList(ClientPacketListener connection) {
        List<PlayerInfo> serverPlayers = new ArrayList<>();
        List<Player> characters = new ArrayList<>();
        Section section = Section.NONE;
        Map<Identifier, List<String>> accountsBySkin = new HashMap<>();

        List<PlayerInfo> entries = connection.getListedOnlinePlayers().stream()
                .sorted(PlayerTabOverlayAccessor.getPlayerComparator())
                .limit(MAX_TAB_ENTRIES)
                .toList();

        for (PlayerInfo entry : entries) {
            String name = getDisplayName(entry);
            if (name.isEmpty()) continue; // blank padding entries

            String key = name.toLowerCase(Locale.ROOT);
            if (SERVER_MARKERS.contains(key)) {
                section = Section.SERVER;
            } else if (key.equals(NEARBY_MARKER)) {
                section = Section.NEARBY;
            } else if (section == Section.SERVER) {
                serverPlayers.add(entry);
                accountsBySkin.computeIfAbsent(entry.getSkin().body().texturePath(), skin -> new ArrayList<>()).add(name);
            } else if (section == Section.NEARBY) {
                characters.add(new Player("", name, getCharacterSkin(entry, accountsBySkin)));
            }
        }

        logSkinDebug(entries);
        CharacterMapperManager.setMappings(characters);
        return serverPlayers;
    }

    /**
     * The skin the character is shown with in the world. The tab entries are fake (empty profile name) and only
     * carry the player's Minecraft skin, so the account is found by matching that skin against the Server
     * section's entries (whose names are account names), and the character skin is then read from the
     * in-world player of that account. Falls back to the tab entry's own skin if the player isn't loaded
     * (e.g. outside render distance).
     */
    private static Identifier getCharacterSkin(PlayerInfo entry, Map<Identifier, List<String>> accountsBySkin) {
        Identifier tabSkin = entry.getSkin().body().texturePath();
        ClientLevel level = Minecraft.getInstance().level;
        List<String> accounts = accountsBySkin.get(tabSkin);

        if (level != null && accounts != null) {
            for (AbstractClientPlayer player : level.players()) {
                for (String account : accounts) {
                    if (player.getGameProfile().name().equalsIgnoreCase(account)) {
                        return player.getSkin().body().texturePath();
                    }
                }
            }
        }

        return tabSkin;
    }

    // TEMPORARY: skin debugging. Remove once the character skin lookup is sorted out.
    private static final org.slf4j.Logger DEBUG_LOGGER = org.slf4j.LoggerFactory.getLogger("jimmytools-skin-debug");
    private static long lastDebugLog = 0;

    private static void logSkinDebug(List<PlayerInfo> entries) {
        long now = System.currentTimeMillis();
        if (now - lastDebugLog < 10_000 || !Minecraft.getInstance().options.keyPlayerList.isDown()) return;
        lastDebugLog = now;

        DEBUG_LOGGER.info("[JT-DEBUG] ---- tab entries ----");
        for (PlayerInfo e : entries) {
            DEBUG_LOGGER.info("[JT-DEBUG] tab uuid={} profileName='{}' display='{}' siblings={} skin={}",
                    e.getProfile().id(), e.getProfile().name(),
                    e.getTabListDisplayName() == null ? null : e.getTabListDisplayName().getString(),
                    e.getTabListDisplayName() == null ? -1 : e.getTabListDisplayName().getSiblings().size(),
                    e.getSkin().body().texturePath());
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            DEBUG_LOGGER.info("[JT-DEBUG] ---- world players ----");
            for (AbstractClientPlayer p : level.players()) {
                DEBUG_LOGGER.info("[JT-DEBUG] world uuid={} name='{}' skin={}",
                        p.getUUID(), p.getGameProfile().name(), p.getSkin().body().texturePath());
            }
        }
    }

    /**
     * The name the tab list shows for this entry, without the leading colour/status glyph.
     * Real entries are laid out as [glyph, name]; the name is always the last sibling.
     */
    public static String getDisplayName(PlayerInfo entry) {
        Component display = entry.getTabListDisplayName();
        if (display == null) return "";

        List<Component> siblings = display.getSiblings();
        String raw = siblings.isEmpty() ? display.getString() : siblings.get(siblings.size() - 1).getString();
        return raw.replaceFirst("^[^\\p{L}\\p{N}_]+", "").trim();
    }
}
