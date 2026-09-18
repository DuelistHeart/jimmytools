package com.duelco.managers;

import com.duelco.obj.data.DataStore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataManager {
    private static DataStore dataStore;
    private static File gameDir = MinecraftClient.getInstance().runDirectory;

    public static void saveData() {
        File path = new File(gameDir, "jimmyData/datastore.json");

        try {
            // Ensure the parent directories exist
            Files.createDirectories(Path.of(path.getParent()));

            // Convert the data object to JSON
            Gson gson = new Gson();
            String json = gson.toJson(dataStore);
            // Write it straight out
            Files.writeString(path.toPath(), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        // The base folder is .minecraft (or the custom run directory)

        // Path: .minecraft/myModData/someFile.json
        File dataFile = new File(gameDir, "jimmyData/datastore.json");

        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
        }

        try (FileReader reader = new FileReader(dataFile)) {
            JsonElement json = JsonParser.parseReader(reader);

            Gson gson = new Gson();
            dataStore = gson.fromJson(json, DataStore.class);
            dataStore.unloadData();
            // Process the JSON as needed
            System.out.println("[JimmyTools] Loaded JSON: " + json);
        } catch (IOException e) {
            dataStore = new DataStore();
            e.printStackTrace();
        }
    }

    public static DataStore getDataStore() {
        return dataStore;
    }
}
