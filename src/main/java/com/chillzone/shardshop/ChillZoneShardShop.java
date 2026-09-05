package com.chillzone.shardshop;

import com.chillzone.shardshop.ui.MainShopMenu;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public final class ChillZoneShardShop implements ModInitializer {
    public static final String MOD_ID = "chillzoneshardshop";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static DeathStore deaths;
    private static ShopConfig config;
    private static Path transactionLog;

    public static DeathStore deaths() { return deaths; }
    public static ShopConfig config() { return config; }

    @Override
    public void onInitialize() {
        config = ShopConfig.load();
        deaths = DeathStore.load();
        transactionLog = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir()
            .resolve("chill-zone-shard-shop-transactions.log");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("shardshop")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    MainShopMenu.open(player);
                    return 1;
                }))
        );

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayer player)) return;
            String cause;
            try { cause = damageSource.getLocalizedDeathMessage(player).getString(); }
            catch (Exception ignored) { cause = "Unknown cause"; }
            DeathRecord death = new DeathRecord(
                player.level().dimension().identifier().toString(),
                player.getX(), player.getY(), player.getZ(),
                player.getYRot(), player.getXRot(),
                System.currentTimeMillis(), cause
            );
            deaths.add(player.getUUID(), death);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> deaths.save());
        LOGGER.info("Chill Zone Shard Shop initialized — /shardshop is ready.");
    }

    public static synchronized void logTransaction(ServerPlayer player, String action, int cost, String details) {
        try {
            Files.createDirectories(transactionLog.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(transactionLog,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(Instant.now() + " | " + player.getUUID() + " | " + player.getScoreboardName() +
                    " | " + action + " | " + cost + " shards | " + details);
                w.newLine();
            }
        } catch (Exception e) {
            LOGGER.error("Could not write shard shop transaction log", e);
        }
    }
}
