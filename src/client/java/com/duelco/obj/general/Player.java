package com.duelco.obj.general;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class Player {
    private String playerName;
    private String characterName;
    private Component displayLabel;
    private Identifier skinTexture;

    public Player(String playerName, String characterName, Identifier skinTexture) {
        this(playerName, characterName, Component.literal(characterName), skinTexture);
    }

    /** @param displayLabel what the tab list draws: the name with its leading glyph(s), as the server styled them */
    public Player(String playerName, String characterName, Component displayLabel, Identifier skinTexture) {
        this.playerName = playerName;
        this.characterName = characterName;
        this.displayLabel = displayLabel;
        this.skinTexture = skinTexture;
    }

    public Component getDisplayLabel() {
        return displayLabel;
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
