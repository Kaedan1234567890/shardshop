package com.chillzone.shardshop;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class UpgradeService {
    private UpgradeService() {}

    public static int cost(UpgradeStore.Upgrade upgrade) {
        return upgrade == UpgradeStore.Upgrade.VEIN_MINER
            ? ChillZoneShardShop.config().veinMinerUnlockCost
            : ChillZoneShardShop.config().treeChopperUnlockCost;
    }

    public static String display(UpgradeStore.Upgrade upgrade) {
        return upgrade == UpgradeStore.Upgrade.VEIN_MINER ? "Vein Miner" : "Tree Chopper";
    }

    public static boolean purchase(ServerPlayer player, UpgradeStore.Upgrade upgrade) {
        if (ChillZoneShardShop.upgrades().unlocked(player.getUUID(), upgrade)) return true;
        int cost = cost(upgrade);
        int balance = ShardBridge.balance(player.getUUID());
        if (balance < cost || !ShardBridge.take(player, cost)) {
            player.sendSystemMessage(Component.literal("You need " + cost + " Shards to unlock " + display(upgrade) + ".")
                .withStyle(ChatFormatting.RED));
            return false;
        }
        ChillZoneShardShop.upgrades().unlock(player.getUUID(), upgrade);
        ChillZoneShardShop.logTransaction(player, "UPGRADE_UNLOCK", cost, display(upgrade));
        player.sendSystemMessage(Component.literal("Unlocked " + display(upgrade) + "! It starts OFF — use the settings menu to turn it on.")
            .withStyle(ChatFormatting.GREEN));
        return true;
    }
}
