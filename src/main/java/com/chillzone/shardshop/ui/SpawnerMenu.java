package com.chillzone.shardshop.ui;

import com.chillzone.shardshop.ChillZoneShardShop;
import com.chillzone.shardshop.ShardBridge;
import com.chillzone.shardshop.SpawnerFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SpawnerMenu extends ChestMenu {
    private static final int[] SLOTS = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
    private static final int BACK = 49;
    private final ServerPlayer player;
    private List<Map.Entry<String,Integer>> products;

    private SpawnerMenu(int id, Inventory inv, ServerPlayer player) {
        super(MenuType.GENERIC_9x6, id, inv, new SimpleContainer(54), 6);
        this.player = player;
        refresh();
    }

    public static void open(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new SpawnerMenu(id, inv, player),
            Component.literal("Shard Shop — Spawners")));
    }

    private void refresh() {
        ItemStack filler = Ui.button(Ui.item("gray_stained_glass_pane"), Component.empty());
        for (int i = 0; i < 54; i++) getContainer().setItem(i, filler.copy());
        products = new ArrayList<>(ChillZoneShardShop.config().spawnerPrices.entrySet());
        for (int i = 0; i < Math.min(products.size(), SLOTS.length); i++) {
            var p = products.get(i);
            getContainer().setItem(SLOTS[i], Ui.button(Ui.item("spawner"),
                Ui.name(pretty(p.getKey()) + " Spawner", ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                Ui.lore("Cost: " + p.getValue() + " Shards"),
                Ui.lore("Already configured for " + pretty(p.getKey()) + "."),
                Ui.lore("Click to buy.")));
        }
        getContainer().setItem(BACK, Ui.button(Ui.item("arrow"), Ui.name("Back", ChatFormatting.YELLOW)));
    }

    @Override public void clicked(int slotId, int button, ContainerInput input, Player clicker) {
        if (slotId == BACK) { MainShopMenu.open(player); return; }
        for (int i = 0; i < SLOTS.length && i < products.size(); i++) {
            if (slotId != SLOTS[i]) continue;
            var product = products.get(i);
            int cost = product.getValue();
            int balance = ShardBridge.balance(player.getUUID());
            if (balance < cost) {
                player.sendSystemMessage(Component.literal("You need " + cost + " Shards. You have " + Math.max(0, balance) + ".")
                    .withStyle(ChatFormatting.RED));
                return;
            }
            if (!ShardBridge.take(player, cost)) {
                player.sendSystemMessage(Component.literal("Purchase could not be completed.").withStyle(ChatFormatting.RED));
                return;
            }
            try {
                ItemStack spawner = SpawnerFactory.create(product.getKey());
                if (!player.getInventory().add(spawner)) player.drop(spawner, false);
                player.sendSystemMessage(Component.literal("Purchased a " + pretty(product.getKey()) + " Spawner for " + cost + " Shards.")
                    .withStyle(ChatFormatting.GREEN));
                ChillZoneShardShop.logTransaction(player, "SPAWNER_PURCHASE", cost, product.getKey());
                refresh();
            } catch (Exception e) {
                ShardBridge.refund(player, cost);
                ChillZoneShardShop.LOGGER.error("Spawner purchase failed; refunded {} shards to {}", cost, player.getScoreboardName(), e);
                player.sendSystemMessage(Component.literal("Spawner purchase failed. Your Shards were refunded.").withStyle(ChatFormatting.RED));
            }
            return;
        }
    }

    private static String pretty(String s) {
        String[] words = s.split("_");
        StringBuilder out = new StringBuilder();
        for (String w : words) {
            if (!out.isEmpty()) out.append(' ');
            out.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
        }
        return out.toString();
    }
    @Override public ItemStack quickMoveStack(Player clicker, int slot) { return ItemStack.EMPTY; }
    @Override public boolean canTakeItemForPickAll(ItemStack stack, net.minecraft.world.inventory.Slot slot) { return false; }
    @Override public boolean stillValid(Player clicker) { return true; }
}
