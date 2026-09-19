package com.duelco.handlers;

import com.duelco.managers.CharacterMapperManager;
import com.duelco.mixin.client.PlayerTabOverlayAccessor;
import com.duelco.obj.general.Player;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
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
            } else if (section == Section.NEARBY) {
                characters.add(new Player(entry.getProfile().name(), name, entry.getSkin().body().texturePath()));
            }
        }

        CharacterMapperManager.setMappings(characters);
        return serverPlayers;
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
