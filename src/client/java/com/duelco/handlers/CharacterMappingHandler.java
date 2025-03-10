package com.duelco.handlers;

import com.duelco.managers.CharacterMapperManager;
import com.duelco.obj.general.Player;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CharacterMappingHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void mapNearbyPlayers() {
        List<Player> mappedPlayers = new ArrayList<>();
        Collection<PlayerListEntry> players = client.getNetworkHandler().getPlayerList();

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
            PlayerListEntry playerEntry = nearbyPlayerEntries.get(i);
            PlayerEntity playerEntity = nearbyPlayers.get(i);

            // Get the player's skin texture
            Identifier skinTexture = ((AbstractClientPlayerEntity) playerEntity).getSkinTextures().texture();

            Player player = new Player(playerEntity.getName().getString(),
                    playerEntry.getDisplayName().getSiblings().get(1).getString(), skinTexture);

            mappedPlayers.add(player);
        }

        CharacterMapperManager.setMappings(mappedPlayers);
    }

    public static List<PlayerEntity> getNearbyPlayers(PlayerEntity player, int count) {
        if (player == null || player.getWorld() == null) {
            return List.of();
        }

        return player.getWorld().getPlayers().stream()
                .sorted(Comparator.comparing((PlayerEntity p) -> p.squaredDistanceTo(player)))
                .limit(count)
                .sorted(Comparator.comparing((PlayerEntity p) -> p.getName().getString()))
                .collect(Collectors.toList());
    }
}
