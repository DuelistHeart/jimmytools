package com.duelco.managers;
import com.duelco.obj.BingoItem;
import com.duelco.obj.BingoPossibleItemsList;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BingoListener implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = Identifier.parse("jimmytools:bingo_listener");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
        // Load your JSON here
        Identifier jsonId = Identifier.parse("jimmytools:data/bingo_card.json");

        try {
            Optional<Resource> resource = currentReload.resourceManager().getResource(jsonId);
            if (resource.isPresent()) {
                List<BingoItem> bingoItems = new ArrayList<>();

                try (var stream = resource.get().open()) {
                    // Parse the JSON list, then copy it into a new ArrayList.
                    JsonArray jsonElement = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();
                    for (JsonElement element : jsonElement) {
                        bingoItems.add(gson.fromJson(element, BingoItem.class));
                    }

                    BingoPossibleItemsList.init(bingoItems);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onResourceManagerReload(net.minecraft.server.packs.resources.ResourceManager resourceManager) {

    }
}