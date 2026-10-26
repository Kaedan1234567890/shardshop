package com.chillzone.shardshop;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

/** Extra harvest blocks only when purchased/enabled. Bounded tree search protects server TPS. */
public final class HarvestUpgradeService {
    private static final Set<UUID> BUSY = new HashSet<>();
    private static final ArrayList<Decay> LEAVES = new ArrayList<>();
    private record Decay(ServerLevel level,BlockPos pos,long due) {}
    private static long tick;
    private HarvestUpgradeService() {}

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tick++;
            Iterator<Decay> it=LEAVES.iterator();int processed=0;
            while(it.hasNext() && processed<48){Decay d=it.next();if(d.due>tick)continue;it.remove();processed++;
                BlockState state=d.level.getBlockState(d.pos);
                if(state.is(BlockTags.LEAVES))d.level.destroyBlock(d.pos,false);
            }
        });
        PlayerBlockBreakEvents.AFTER.register((world,player,pos,state,blockEntity)->{
            if(!(world instanceof ServerLevel level)||!(player instanceof ServerPlayer sp))return;
            if(BUSY.contains(sp.getUUID())||sp.isCreative()||sp.isSpectator())return;
            ItemStack tool=sp.getMainHandItem();
            if(state.is(BlockTags.LOGS)&&tool.is(net.minecraft.tags.ItemTags.AXES)
                    &&ChillZoneShardShop.upgrades().enabled(sp.getUUID(),UpgradeStore.Upgrade.TREE_CHOPPER)){
                // Only natural-looking trees: require nearby leaves, not just stacked logs.
                if(!nearLeaves(level,pos,4))return;
                harvest(level,sp,pos,tool,true);
            } else if(state.is(BlockTags.MINEABLE_WITH_PICKAXE)&&tool.is(net.minecraft.tags.ItemTags.PICKAXES)
                    &&ChillZoneShardShop.upgrades().enabled(sp.getUUID(),UpgradeStore.Upgrade.VEIN_MINER)) {
                // Exact-block matching for ore veins, never all stone/deepslate.
                String id=state.getBlock().toString().toLowerCase(Locale.ROOT);
                if(!id.contains("ore")&&!id.contains("ancient_debris"))return;
                harvest(level,sp,pos,tool,false,state);
            }
        });
    }
    private static boolean nearLeaves(ServerLevel world,BlockPos center,int radius){for(int x=-radius;x<=radius;x++)for(int y=-2;y<=radius;y++)for(int z=-radius;z<=radius;z++)if(world.getBlockState(center.offset(x,y,z)).is(BlockTags.LEAVES))return true;return false;}
    private static void harvest(ServerLevel level,ServerPlayer player,BlockPos origin,ItemStack held,boolean tree){harvest(level,player,origin,held,tree,null);}
    private static void harvest(ServerLevel level,ServerPlayer player,BlockPos origin,ItemStack held,boolean tree,BlockState type){
        UUID id=player.getUUID();if(!BUSY.add(id))return;
        try {
            ArrayDeque<BlockPos> queue=new ArrayDeque<>();Set<BlockPos> seen=new HashSet<>();queue.add(origin);seen.add(origin);
            int harvested=0,limit=tree?192:32;
            while(!queue.isEmpty()&&harvested<limit){BlockPos at=queue.removeFirst();
                for(int dx=-1;dx<=1;dx++)for(int dy=-1;dy<=1;dy++)for(int dz=-1;dz<=1;dz++){
                    if(dx==0&&dy==0&&dz==0)continue;
                    BlockPos next=at.offset(dx,dy,dz);if(!seen.add(next)||seen.size()>1024)continue;
                    if(Math.abs(next.getX()-origin.getX())>16||Math.abs(next.getZ()-origin.getZ())>16||Math.abs(next.getY()-origin.getY())>36)continue;
                    BlockState current=level.getBlockState(next);
                    if(tree?!current.is(BlockTags.LOGS):current.getBlock()!=type.getBlock())continue;
                    // Do not remove blocks if the tool is unavailable or worn out.
                    if(held.isEmpty()||held.getDamageValue()>=held.getMaxDamage()-1&&held.isDamageableItem())return;
                    if(!level.destroyBlock(next,true,player))continue;
                    harvested++;queue.add(next);
                    if(harvested>=limit)break;
                }
            }
            if(tree)for(int dx=-5;dx<=5;dx++)for(int dy=-3;dy<=20;dy++)for(int dz=-5;dz<=5;dz++){
                BlockPos leaf=origin.offset(dx,dy,dz);
                if(level.getBlockState(leaf).is(BlockTags.LEAVES)&&LEAVES.size()<4096)
                    LEAVES.add(new Decay(level,leaf,tick+20+(long)((Math.abs(dx)+Math.abs(dy)+Math.abs(dz))*6)));
            }
        }finally{BUSY.remove(id);}
    }
}
