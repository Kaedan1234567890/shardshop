package com.chillzone.shardshop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/** Permanent one-time purchases; toggles are separately persisted. */
public final class UpgradeStore {
    public enum Upgrade { VEIN_MINER, TREE_CHOPPER }
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<UUID, EnumMap<Upgrade, Entry>>>(){}.getType();
    public static class Entry { boolean purchased; boolean enabled; }
    private final Path path = FabricLoader.getInstance().getConfigDir().resolve("chill-zone-shop-upgrades.json");
    private Map<UUID, EnumMap<Upgrade, Entry>> data = new HashMap<>();
    public static UpgradeStore load() { UpgradeStore s=new UpgradeStore(); try {if(Files.exists(s.path))try(Reader r=Files.newBufferedReader(s.path)){Map<UUID,EnumMap<Upgrade,Entry>> a=GSON.fromJson(r,TYPE);if(a!=null)s.data=a;}}catch(Exception e){ChillZoneShardShop.LOGGER.error("Failed to load upgrades",e);}return s; }
    private Entry entry(UUID id, Upgrade u){return data.computeIfAbsent(id,x->new EnumMap<>(Upgrade.class)).computeIfAbsent(u,x->new Entry());}
    public synchronized boolean purchased(UUID id,Upgrade u){return entry(id,u).purchased;}
    public synchronized boolean enabled(UUID id,Upgrade u){Entry e=entry(id,u);return e.purchased&&e.enabled;}
    public synchronized boolean purchase(UUID id,Upgrade u){Entry e=entry(id,u);if(e.purchased)return false;e.purchased=true;e.enabled=true;save();return true;}
    public synchronized void toggle(UUID id,Upgrade u){Entry e=entry(id,u);if(e.purchased){e.enabled=!e.enabled;save();}}
    public synchronized void save(){try{Files.createDirectories(path.getParent());Path tmp=path.resolveSibling(path.getFileName()+".tmp");try(Writer w=Files.newBufferedWriter(tmp)){GSON.toJson(data,TYPE,w);}try{Files.move(tmp,path,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException e){Files.move(tmp,path,StandardCopyOption.REPLACE_EXISTING);}}catch(Exception e){ChillZoneShardShop.LOGGER.error("Failed saving upgrades",e);}}
}
