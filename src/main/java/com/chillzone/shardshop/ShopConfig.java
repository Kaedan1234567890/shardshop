package com.chillzone.shardshop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Economy values can be changed later without recompiling. */
public final class ShopConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public int deathTeleportCost = 50;
    public int safeTeleportRadius = 10;
    public int safeTeleportVerticalRadius = 4;
    public Map<String, Integer> spawnerPrices = defaults();

    private static Map<String, Integer> defaults() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("zombie", 500);
        m.put("skeleton", 600);
        m.put("spider", 500);
        m.put("cave_spider", 650);
        m.put("creeper", 800);
        m.put("slime", 800);
        m.put("blaze", 1000);
        m.put("enderman", 1200);
        return m;
    }

    public static ShopConfig load() {
        Path p = FabricLoader.getInstance().getConfigDir().resolve("chill-zone-shard-shop.json");
        ShopConfig cfg = new ShopConfig();
        try {
            if (Files.exists(p)) {
                try (Reader r = Files.newBufferedReader(p)) {
                    ShopConfig loaded = GSON.fromJson(r, ShopConfig.class);
                    if (loaded != null) cfg = loaded;
                }
            }
            cfg.deathTeleportCost = Math.max(0, cfg.deathTeleportCost);
            cfg.safeTeleportRadius = Math.max(0, Math.min(10, cfg.safeTeleportRadius));
            cfg.safeTeleportVerticalRadius = Math.max(0, Math.min(8, cfg.safeTeleportVerticalRadius));
            if (cfg.spawnerPrices == null || cfg.spawnerPrices.isEmpty()) cfg.spawnerPrices = defaults();
            Files.createDirectories(p.getParent());
            try (Writer w = Files.newBufferedWriter(p)) { GSON.toJson(cfg, w); }
        } catch (Exception e) {
            ChillZoneShardShop.LOGGER.error("Could not load/save shard shop config", e);
        }
        return cfg;
    }
}
