package com.duelco.obj.data;

import com.duelco.managers.BingoManager;
import com.duelco.obj.general.TabData;
import com.duelco.obj.general.UiPosition;
import com.duelco.obj.bingo.BingoCard;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
    public List<BingoCard> bingoCards;
    public TabData tabData;

    public DataStore() {
        this.bingoCards = new ArrayList<>();
        this.tabData = new TabData();
    }

    public void setBingoCards(List<BingoCard> bingoCards) {
        this.bingoCards = bingoCards;
    }

    public TabData getTabData() {
        return tabData;
    }

    public List<BingoCard> getBingoCards() {
        return bingoCards;
    }

    public void unloadData() {
        BingoManager.loadCards(bingoCards);
    }
}
