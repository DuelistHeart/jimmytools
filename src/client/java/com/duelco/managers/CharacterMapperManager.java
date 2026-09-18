package com.duelco.managers;

import com.duelco.obj.general.Player;

import java.util.ArrayList;
import java.util.List;

public class CharacterMapperManager {
    private static List<Player> players = new ArrayList<>();

    public static void setMappings(List<Player> players) {
        CharacterMapperManager.players = players;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static Player getPlayer(String playerName) {
        return players.stream().filter(player -> player.getPlayerName().equals(playerName)).findFirst().orElse(null);
    }
}
