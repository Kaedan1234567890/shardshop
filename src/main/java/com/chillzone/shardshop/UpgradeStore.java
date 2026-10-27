package com.chillzone.shardshop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

/** Shard Shop upgrades: one-time purchase, permanent ownership and persistent ON/OFF. */
public final class UpgradeStore {
    public enum Upgrade { VEIN_MINER, TREE_CHOPPER }
    private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE=new TypeToken<Map<UUID,EnumMap<Upgrade,Entry>>>(){}.getType();
    private static final class Entry { boolean purchased; boolean enabled; }
    private final Path path=FabricLoader.getInstance().getConfigDir().resolve("chill-zone-shop-upgrades.json");
    private Map<UUID,EnumMap<Upgrade,Entry>> data=new HashMap<>();
    public static UpgradeStore load(){
        UpgradeStore s=new UpgradeStore();
        if(Files.exists(s.path)) try(Reader r=Files.newBufferedReader(s.path)){
            Map<UUID,EnumMap<Upgrade,Entry>> loaded=GSON.fromJson(r,TYPE);
            if(loaded!=null)s.data=loaded;
        }catch(Exception e){ChillZoneShardShop.LOGGER.error("Cannot read upgrades",e);}
        return s;
    }
    private Entry get(UUID player,Upgrade upgrade){return data.computeIfAbsent(player,x->new EnumMap<>(Upgrade.class)).computeIfAbsent(upgrade,x->new Entry());}
    public synchronized boolean purchased(UUID id,Upgrade u){return get(id,u).purchased;}
    public synchronized boolean enabled(UUID id,Upgrade u){Entry e=get(id,u);return e.purchased&&e.enabled;}
    public synchronized boolean purchase(UUID id,Upgrade u){Entry e=get(id,u);if(e.purchased)return false;e.purchased=true;e.enabled=true;save();return true;}
    public synchronized void setEnabled(UUID id,Upgrade u,boolean enabled){Entry e=get(id,u);if(!e.purchased)return;e.enabled=enabled;save();}
    public synchronized void save(){
        try {Files.createDirectories(path.getParent());Path tmp=path.resolveSibling(path.getFileName()+".tmp");
            try(Writer w=Files.newBufferedWriter(tmp)){GSON.toJson(data,TYPE,w);}
            try{Files.move(tmp,path,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException ignored){Files.move(tmp,path,StandardCopyOption.REPLACE_EXISTING);}
        }catch(Exception e){ChillZoneShardShop.LOGGER.error("Cannot save upgrades",e);}
    }
}
