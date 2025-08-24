package com.duelco.obj.general;

import net.minecraft.util.Identifier;

public class Player {
    private String playerName;
    private String characterName;
    private Identifier skinTexture;

    public Player(String playerName, String characterName, Identifier skinTexture) {
        this.playerName = playerName;
        this.characterName = characterName;
        this.skinTexture = skinTexture;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Identifier getSkinTexture() {
        return skinTexture;
    }

    public void setSkinTexture(Identifier skinTexture) {
        this.skinTexture = skinTexture;
    }
}
