package com.chillzone.shardshop.ui;

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

/** Deliberately empty placeholder for the fourth main-shop category. No purchases or mining hooks. */
public final class UpgradePlaceholderMenu extends ChestMenu {
    private UpgradePlaceholderMenu(int id, Inventory inv) {
        super(MenuType.GENERIC_9x3, id, inv, new SimpleContainer(27), 3);
    }
    public static void open(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
            (id, inv, p) -> new UpgradePlaceholderMenu(id, inv),
            Component.literal("Shop — Upgrades (Coming Soon)")));
    }
    @Override public void clicked(int slot, int button, ContainerInput input, Player player) { }
    @Override public ItemStack quickMoveStack(Player player, int slot) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player player) { return true; }
}
