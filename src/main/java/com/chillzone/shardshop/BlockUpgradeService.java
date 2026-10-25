package com.chillzone.shardshop;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/** Native server-side Vein Miner and Tree Chopper implementations. */
public final class BlockUpgradeService {
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    private BlockUpgradeService() {}

    public static void init() {
        PlayerBlockBreakEvents.AFTER.register((level, player, origin, originalState, blockEntity) -> {
            if (ACTIVE.get()) return;
            if (!(player instanceof ServerPlayer sp)) return;
            if (sp.isCreative() || sp.isSpectator()) return;
            ItemStack tool = sp.getMainHandItem();
            String toolPath = BuiltInRegistries.ITEM.getKey(tool.getItem()).getPath();

            UpgradeStore store = ChillZoneShardShop.upgrades();
            if (store.enabled(sp.getUUID(), UpgradeStore.Upgrade.VEIN_MINER)
                && toolPath.endsWith("_pickaxe") && isVeinBlock(originalState)) {
                breakConnected(sp, origin, originalState, ChillZoneShardShop.config().veinMinerMaxBlocks, false);
            } else if (store.enabled(sp.getUUID(), UpgradeStore.Upgrade.TREE_CHOPPER)
                && toolPath.endsWith("_axe") && originalState.is(BlockTags.LOGS)) {
                breakConnected(sp, origin, originalState, ChillZoneShardShop.config().treeChopperMaxLogs, true);
            }
        });
    }

    private static boolean isVeinBlock(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        return path.endsWith("_ore") || path.equals("ancient_debris");
    }

    private static void breakConnected(ServerPlayer player, BlockPos origin, BlockState original, int max, boolean logsOnly) {
        if (max <= 1) return;
        Block target = original.getBlock();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        for (Direction d : Direction.values()) queue.add(origin.relative(d));
        seen.add(origin);
        int broken = 1;

        ACTIVE.set(true);
        try {
            while (!queue.isEmpty() && broken < max) {
                BlockPos pos = queue.removeFirst();
                if (!seen.add(pos)) continue;
                BlockState state = player.level().getBlockState(pos);
                if (state.getBlock() != target) continue;
                if (logsOnly && !state.is(BlockTags.LOGS)) continue;
                if (!logsOnly && !isVeinBlock(state)) continue;
                if (player.gameMode.destroyBlock(pos)) {
                    broken++;
                    for (Direction d : Direction.values()) queue.add(pos.relative(d));
                }
            }
        } finally {
            ACTIVE.set(false);
        }
    }
}
