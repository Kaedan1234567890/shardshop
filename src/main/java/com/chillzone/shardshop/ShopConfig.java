package com.chillzone.shardshop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Economy values can be changed later without recompiling. */
public final class ShopConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public int deathTeleportCost = 25;
    public int safeTeleportRadius = 10;
    public int safeTeleportVerticalRadius = 4;
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
            // Migrate older Shard Shop configs to the new 25-Shard death teleport price.
            // Existing servers may still have 50 (old default) or 20 (brief Fix 4 default) saved on disk.
            if (cfg.deathTeleportCost == 50 || cfg.deathTeleportCost == 20) {
                cfg.deathTeleportCost = 25;
            }
            cfg.deathTeleportCost = Math.max(0, cfg.deathTeleportCost);
            cfg.safeTeleportRadius = Math.max(0, Math.min(10, cfg.safeTeleportRadius));
            cfg.safeTeleportVerticalRadius = Math.max(0, Math.min(8, cfg.safeTeleportVerticalRadius));
            Files.createDirectories(p.getParent());
            try (Writer w = Files.newBufferedWriter(p)) { GSON.toJson(cfg, w); }
        } catch (Exception e) {
            ChillZoneShardShop.LOGGER.error("Could not load/save shard shop config", e);
        }
        return cfg;
    }
}
