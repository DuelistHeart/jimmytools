package com.duelco.handlers;

import com.duelco.managers.CharacterMapperManager;
import com.duelco.obj.general.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class CharacterMappingHandler {
    private static final Minecraft client = Minecraft.getInstance();

    public static void mapNearbyPlayers() {
        List<Player> mappedPlayers = new ArrayList<>();
        Collection<PlayerInfo> players = client.getConnection().getOnlinePlayers();

        List<PlayerInfo> nearbyPlayerEntries = players.stream().toList().stream().filter(entry -> {
            if (entry.getTabListDisplayName() != null) {
                return entry.getTabListDisplayName().getSiblings().size() == 2;
            } else {
                return false;
            }
        }).toList();

        List<net.minecraft.world.entity.player.Player> nearbyPlayers = getNearbyPlayers(client.player, nearbyPlayerEntries.size());

        // Loop through the player list and draw custom tab names
        for (int i = 0; i < nearbyPlayers.size(); i++) {
            PlayerInfo playerEntry = nearbyPlayerEntries.get(i);
            net.minecraft.world.entity.player.Player playerEntity = nearbyPlayers.get(i);

            // Get the player's skin texture
            Identifier skinTexture = ((AbstractClientPlayer) playerEntity).getSkin().body().texturePath();

            Player player = new Player(playerEntity.getName().getString(),
                    playerEntry.getTabListDisplayName().getSiblings().get(1).getString(), skinTexture);

            mappedPlayers.add(player);
        }

        CharacterMapperManager.setMappings(mappedPlayers);
    }

    public static List<net.minecraft.world.entity.player.Player> getNearbyPlayers(net.minecraft.world.entity.player.Player player, int count) {
        if (player == null || !(player.level() instanceof ClientLevel level)) {
            return List.of();
        }

        Set<String> seenNames = new LinkedHashSet<>();

        return level.players().stream()
                .sorted(Comparator.comparing((AbstractClientPlayer p) -> p.distanceToSqr(player)))
                .filter(p -> seenNames.add(p.getName().getString())) // Only add if name is not already in the set
                .limit(count)
                .sorted(Comparator.comparing((AbstractClientPlayer p) -> p.getName().getString()))
                .<net.minecraft.world.entity.player.Player>map(p -> p)
                .collect(Collectors.toList());

    }
}
