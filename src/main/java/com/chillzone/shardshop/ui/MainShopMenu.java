package com.chillzone.shardshop.ui;

import com.chillzone.shardshop.ShardBridge;
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

public final class MainShopMenu extends ChestMenu {
    private static final int ROWS = 6;
    private static final int DEATHS = 20;
    private static final int SPAWNERS = 24;
    private static final int BALANCE = 49;
    private final ServerPlayer player;

    private MainShopMenu(int id, Inventory inv, ServerPlayer player) {
        super(MenuType.GENERIC_9x6, id, inv, new SimpleContainer(ROWS * 9), ROWS);
        this.player = player;
        refresh();
    }

    public static void open(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new MainShopMenu(id, inv, player),
            Component.literal("Chill Zone — Shard Shop")));
    }

    private void refresh() {
        ItemStack filler = Ui.button(Ui.item("gray_stained_glass_pane"), Component.empty());
        for (int i = 0; i < 54; i++) getContainer().setItem(i, filler.copy());
        getContainer().setItem(DEATHS, Ui.button(Ui.item("recovery_compass"),
            Ui.name("Deaths", ChatFormatting.AQUA, ChatFormatting.BOLD),
            Ui.lore("View your 28 most recent deaths."),
            Ui.lore("Teleport back using Shards.")));
        getContainer().setItem(SPAWNERS, Ui.button(Ui.item("spawner"),
            Ui.name("Spawners", ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            Ui.lore("Buy preconfigured mob spawners.")));
        int balance = ShardBridge.balance(player.getUUID());
        getContainer().setItem(BALANCE, Ui.button(Ui.item("amethyst_shard"),
            Ui.name("Your Shards: " + Math.max(0, balance), ChatFormatting.YELLOW, ChatFormatting.BOLD),
            Ui.lore("Shared with Chill Zone Homes.")));
    }

    @Override public void clicked(int slotId, int button, ContainerInput input, Player clicker) {
        if (slotId == DEATHS) DeathMenu.open(player);
        else if (slotId == SPAWNERS) SpawnerMenu.open(player);
    }
    @Override public ItemStack quickMoveStack(Player clicker, int slot) { return ItemStack.EMPTY; }
    @Override public boolean canTakeItemForPickAll(ItemStack stack, net.minecraft.world.inventory.Slot slot) { return false; }
    @Override public boolean stillValid(Player clicker) { return true; }
}
