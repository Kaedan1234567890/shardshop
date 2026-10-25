package com.chillzone.shardshop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Persistent permanent unlocks and per-player enable/disable state. */
public final class UpgradeStore {
    public enum Upgrade { VEIN_MINER, TREE_CHOPPER }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<UUID, PlayerUpgrades>>(){}.getType();
    private final Path path;
    private final Map<UUID, PlayerUpgrades> data;

    public static final class PlayerUpgrades {
        public boolean veinMinerUnlocked;
        public boolean veinMinerEnabled;
        public boolean treeChopperUnlocked;
        public boolean treeChopperEnabled;
    }

    private UpgradeStore(Path path, Map<UUID, PlayerUpgrades> data) {
        this.path = path;
        this.data = data == null ? new HashMap<>() : data;
    }

    public static UpgradeStore load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("chill-zone-shard-shop-upgrades.json");
        try {
            if (Files.exists(path)) {
                try (Reader r = Files.newBufferedReader(path)) {
                    return new UpgradeStore(path, GSON.fromJson(r, TYPE));
                }
            }
        } catch (Exception e) {
            ChillZoneShardShop.LOGGER.error("Could not load shop upgrades", e);
        }
        return new UpgradeStore(path, new HashMap<>());
    }

    private PlayerUpgrades player(UUID id) {
        return data.computeIfAbsent(id, ignored -> new PlayerUpgrades());
    }

    public synchronized boolean unlocked(UUID id, Upgrade upgrade) {
        PlayerUpgrades p = player(id);
        return upgrade == Upgrade.VEIN_MINER ? p.veinMinerUnlocked : p.treeChopperUnlocked;
    }

    public synchronized boolean enabled(UUID id, Upgrade upgrade) {
        PlayerUpgrades p = player(id);
        if (upgrade == Upgrade.VEIN_MINER) return p.veinMinerUnlocked && p.veinMinerEnabled;
        return p.treeChopperUnlocked && p.treeChopperEnabled;
    }

    public synchronized void unlock(UUID id, Upgrade upgrade) {
        PlayerUpgrades p = player(id);
        if (upgrade == Upgrade.VEIN_MINER) {
            p.veinMinerUnlocked = true;
            p.veinMinerEnabled = false;
        } else {
            p.treeChopperUnlocked = true;
            p.treeChopperEnabled = false;
        }
        save();
    }

    public synchronized void setEnabled(UUID id, Upgrade upgrade, boolean enabled) {
        PlayerUpgrades p = player(id);
        if (upgrade == Upgrade.VEIN_MINER) {
            if (!p.veinMinerUnlocked) return;
            p.veinMinerEnabled = enabled;
        } else {
            if (!p.treeChopperUnlocked) return;
            p.treeChopperEnabled = enabled;
        }
        save();
    }

    public synchronized void save() {
        try {
            Files.createDirectories(path.getParent());
            try (Writer w = Files.newBufferedWriter(path)) { GSON.toJson(data, TYPE, w); }
        } catch (Exception e) {
            ChillZoneShardShop.LOGGER.error("Could not save shop upgrades", e);
        }
    }
}
