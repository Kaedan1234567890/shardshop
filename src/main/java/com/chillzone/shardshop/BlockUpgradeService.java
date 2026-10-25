package com.chillzone.shardshop;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Native server-side Vein Miner and Tree Chopper implementations. */
public final class BlockUpgradeService {
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final int LEAF_SCAN_RADIUS = 5;
    private static final int LEAF_LOG_SEARCH_DEPTH = 6;

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
                Set<BlockPos> chopped = breakConnected(sp, origin, originalState,
                    ChillZoneShardShop.config().treeChopperMaxLogs, true);
                accelerateLeafDecay((ServerLevel) sp.level(), chopped);
            }
        });
    }

    private static boolean isVeinBlock(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        return path.endsWith("_ore") || path.equals("ancient_debris");
    }

    /** Allows one ore vein to continue between stone and deepslate versions of the same ore. */
    private static boolean isSameVein(BlockState original, BlockState candidate) {
        if (!isVeinBlock(candidate)) return false;
        String a = normalizeOre(BuiltInRegistries.BLOCK.getKey(original.getBlock()).getPath());
        String b = normalizeOre(BuiltInRegistries.BLOCK.getKey(candidate.getBlock()).getPath());
        return a.equals(b);
    }

    private static String normalizeOre(String path) {
        return path.startsWith("deepslate_") ? path.substring("deepslate_".length()) : path;
    }

    /**
     * Breaks a connected group. Both upgrades use the full 3x3x3 neighborhood so diagonal
     * ore veins and sideways/diagonal tree branches are discovered instead of only six faces.
     */
    private static Set<BlockPos> breakConnected(ServerPlayer player, BlockPos origin, BlockState original, int max, boolean logsOnly) {
        Set<BlockPos> brokenPositions = new HashSet<>();
        brokenPositions.add(origin.immutable());
        if (max <= 1) return brokenPositions;

        Block target = original.getBlock();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        addNeighbors26(queue, origin);
        seen.add(origin.immutable());
        int broken = 1;

        ACTIVE.set(true);
        try {
            while (!queue.isEmpty() && broken < max) {
                BlockPos pos = queue.removeFirst();
                if (!seen.add(pos)) continue;
                BlockState state = player.level().getBlockState(pos);

                if (logsOnly) {
                    // Tree Chopper intentionally stays on the same log block type so nearby trees
                    // touching leaves/branches are not accidentally chopped together.
                    if (state.getBlock() != target || !state.is(BlockTags.LOGS)) continue;
                } else if (!isSameVein(original, state)) {
                    continue;
                }

                if (player.gameMode.destroyBlock(pos)) {
                    broken++;
                    brokenPositions.add(pos.immutable());
                    addNeighbors26(queue, pos);
                }
            }
        } finally {
            ACTIVE.set(false);
        }
        return brokenPositions;
    }

    private static void addNeighbors26(ArrayDeque<BlockPos> queue, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    queue.add(center.offset(dx, dy, dz));
                }
            }
        }
    }

    /**
     * Fast leaf cleanup after Tree Chopper finishes. Only naturally-decaying leaves are considered.
     * Leaves that can still reach another remaining log through the canopy are preserved, which
     * avoids stripping foliage from a neighboring tree whose leaves happen to touch this tree.
     */
    private static void accelerateLeafDecay(ServerLevel level, Set<BlockPos> choppedLogs) {
        if (choppedLogs.isEmpty()) return;
        Set<BlockPos> candidates = new HashSet<>();

        for (BlockPos log : choppedLogs) {
            for (int dx = -LEAF_SCAN_RADIUS; dx <= LEAF_SCAN_RADIUS; dx++) {
                for (int dy = -LEAF_SCAN_RADIUS; dy <= LEAF_SCAN_RADIUS; dy++) {
                    for (int dz = -LEAF_SCAN_RADIUS; dz <= LEAF_SCAN_RADIUS; dz++) {
                        BlockPos pos = log.offset(dx, dy, dz);
                        BlockState state = level.getBlockState(pos);
                        if (!state.is(BlockTags.LEAVES)) continue;
                        if (state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) continue;
                        candidates.add(pos.immutable());
                    }
                }
            }
        }

        ACTIVE.set(true);
        try {
            for (BlockPos leaf : candidates) {
                BlockState state = level.getBlockState(leaf);
                if (!state.is(BlockTags.LEAVES)) continue;
                if (!connectedToRemainingLog(level, leaf)) {
                    // Destroying with drops gives a rapid vanilla-like cleanup while still allowing
                    // saplings/sticks/apples to drop naturally instead of silently deleting leaves.
                    level.destroyBlock(leaf, true);
                }
            }
        } finally {
            ACTIVE.set(false);
        }
    }

    private static boolean connectedToRemainingLog(ServerLevel level, BlockPos start) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Map<BlockPos, Integer> depth = new HashMap<>();
        queue.add(start.immutable());
        depth.put(start.immutable(), 0);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            int d = depth.get(pos);
            if (d >= LEAF_LOG_SEARCH_DEPTH) continue;

            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                BlockState nextState = level.getBlockState(next);
                if (nextState.is(BlockTags.LOGS)) return true;
                if (!nextState.is(BlockTags.LEAVES)) continue;
                if (depth.containsKey(next)) continue;
                BlockPos immutable = next.immutable();
                depth.put(immutable, d + 1);
                queue.add(immutable);
            }
        }
        return false;
    }
}
