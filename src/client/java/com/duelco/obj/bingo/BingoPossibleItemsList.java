package com.duelco.obj.bingo;

import io.wispforest.owo.ui.core.Component;

import java.util.*;

public class BingoPossibleItemsList {
    private static final ArrayList<BingoItem> items = new ArrayList<>();
    private static final int MAX_CARD_SIZE = 25;
    private static String filter;

    public static void init(List<BingoItem> bingoItems) {
        items.clear();
        for (BingoItem item : bingoItems) {
            items.add(new BingoItem(item));
        }
    }

    public static List<BingoItem> generateBingoItemsList() {
        List<BingoItem> itemsForBingoCard = new ArrayList<>();
        List<BingoItem> claimedItems = new ArrayList<>();
        Random random = new Random();

        while (itemsForBingoCard.size() < MAX_CARD_SIZE) {
            int randIndex = random.nextInt(items.size());
            BingoItem randItem = items.get(randIndex);

            if (!claimedItems.contains(randItem)) {
                itemsForBingoCard.add(new BingoItem(randItem));
                claimedItems.add(randItem);
            }
        }

        return itemsForBingoCard;
    }

    public static List<BingoItem> getListOfPossibleItems() {
        items.sort(Comparator.comparing(BingoItem::getName));

        if (filter != null) {
            return items.stream().filter(item -> item.getName().toLowerCase().contains(filter.toLowerCase())).toList();
        } else {
            return items;
        }
    }

    public static void setFilter(String filter) {
        BingoPossibleItemsList.filter = filter;
    }

    public static String getFilter() {
        return filter;
    }
}
