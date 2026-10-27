package com.chillzone.shardshop;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

/** A bounded connected harvest; applies durability for every extra log/ore. */
public final class HarvestUpgradeService {
    private static final Set<UUID> ACTIVE=new HashSet<>();
    private record LeafDecay(ServerLevel level,BlockPos position,long atTick){}
    private static final List<LeafDecay> DECAY=new ArrayList<>();
    private static long ticks;
    private HarvestUpgradeService(){}
    public static void init(){
        ServerTickEvents.END_SERVER_TICK.register(server->{
            ticks++;
            Iterator<LeafDecay> it=DECAY.iterator();int count=0;
            while(it.hasNext()&&count<64){
                LeafDecay d=it.next();if(d.atTick()>ticks)continue;it.remove();count++;
                BlockState state=d.level().getBlockState(d.position());
                if(state.is(BlockTags.LEAVES)&&state.hasProperty(LeavesBlock.PERSISTENT)&&!state.getValue(LeavesBlock.PERSISTENT)
                    &&state.hasProperty(LeavesBlock.DISTANCE)&&state.getValue(LeavesBlock.DISTANCE)>=7){
                    // Drops from vanilla loot tables, including apples and saplings where applicable.
                    d.level().destroyBlock(d.position(),true);
                }
            }
        });
        PlayerBlockBreakEvents.AFTER.register((world,player,pos,state,entity)->{
            if(!(world instanceof ServerLevel level)||!(player instanceof ServerPlayer sp))return;
            if(ACTIVE.contains(sp.getUUID())||sp.isCreative()||sp.isSpectator())return;
            ItemStack tool=sp.getMainHandItem();
            if(state.is(BlockTags.LOGS)&&tool.is(ItemTags.AXES)
                &&ChillZoneShardShop.upgrades().enabled(sp.getUUID(),UpgradeStore.Upgrade.TREE_CHOPPER)){
                if(nearLeaves(level,pos,4))harvest(level,sp,pos,state,true);
            }else if(tool.is(ItemTags.PICKAXES)&&isOre(state)
                &&ChillZoneShardShop.upgrades().enabled(sp.getUUID(),UpgradeStore.Upgrade.VEIN_MINER)){
                harvest(level,sp,pos,state,false);
            }
        });
    }
    private static boolean isOre(BlockState state){
        String key=state.getBlock().toString().toLowerCase(Locale.ROOT);
        return key.contains("ore")||key.contains("ancient_debris");
    }
    private static boolean nearLeaves(ServerLevel world,BlockPos pos,int radius){
        for(int dx=-radius;dx<=radius;dx++)for(int dy=-2;dy<=radius;dy++)for(int dz=-radius;dz<=radius;dz++)
            if(world.getBlockState(pos.offset(dx,dy,dz)).is(BlockTags.LEAVES))return true;
        return false;
    }
    private static void harvest(ServerLevel level,ServerPlayer sp,BlockPos origin,BlockState original,boolean tree){
        if(!ACTIVE.add(sp.getUUID()))return;
        try{
            ItemStack held=sp.getMainHandItem();int limit=tree?192:100;
            ArrayDeque<BlockPos> queue=new ArrayDeque<>();Set<BlockPos> visited=new HashSet<>();
            queue.add(origin);visited.add(origin);int broken=0;
            while(!queue.isEmpty()&&broken<limit){
                BlockPos at=queue.removeFirst();
                for(int dx=-1;dx<=1&&broken<limit;dx++)for(int dy=-1;dy<=1&&broken<limit;dy++)for(int dz=-1;dz<=1&&broken<limit;dz++){
                    if(dx==0&&dy==0&&dz==0)continue;
                    BlockPos p=at.offset(dx,dy,dz);
                    if(!visited.add(p)||visited.size()>2048)continue;
                    if(Math.abs(p.getX()-origin.getX())>18||Math.abs(p.getZ()-origin.getZ())>18||Math.abs(p.getY()-origin.getY())>40)continue;
                    BlockState target=level.getBlockState(p);
                    if(tree?!target.is(BlockTags.LOGS):target.getBlock()!=original.getBlock())continue;
                    if(held.isEmpty()||(held.isDamageableItem()&&held.getDamageValue()>=held.getMaxDamage()-1))return;
                    if(!level.destroyBlock(p,true,sp))continue;
                    if(held.isDamageableItem())held.hurtAndBreak(1,sp,EquipmentSlot.MAINHAND);
                    broken++;queue.add(p);
                }
            }
            if(tree){
                for(int dx=-6;dx<=6;dx++)for(int dy=-4;dy<=24;dy++)for(int dz=-6;dz<=6;dz++){
                    BlockPos leaf=origin.offset(dx,dy,dz);
                    if(level.getBlockState(leaf).is(BlockTags.LEAVES)&&DECAY.size()<4096)
                        DECAY.add(new LeafDecay(level,leaf,ticks+20L+Math.abs(dx)*4L+Math.abs(dy)*3L+Math.abs(dz)*4L));
                }
            }
        }finally{ACTIVE.remove(sp.getUUID());}
    }
}
