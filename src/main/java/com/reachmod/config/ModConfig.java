package com.reachmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static ModConfig instance;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("reachmod.json").toFile();

    public boolean enabled = false;
    public double reachDistance = 4.5;

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
            instance.load();
        }
        return instance;
    }

    // دوال استدعاء مساعدة (Static Helpers) لحل مشكلة التجميع
    public static double getReachDistance() {
        return getInstance().reachDistance;
    }

    public static boolean isEnabled() {
        return getInstance().enabled;
    }

    public void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    this.enabled = loaded.enabled;
                    this.reachDistance = loaded.reachDistance;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
