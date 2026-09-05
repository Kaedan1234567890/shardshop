package com.chillzone.shardshop.ui;

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

/**
 * Main /shardshop menu.
 *
 * Intentionally small for the first release: one regular chest containing only
 * the Deaths category. More shard-shop categories can be added in future
 * versions without changing the death-history system.
 */
public final class MainShopMenu extends ChestMenu {
    private static final int ROWS = 3;
    private static final int DEATHS = 13; // exact centre of a 3x9 chest
    private final ServerPlayer player;

    private MainShopMenu(int id, Inventory inv, ServerPlayer player) {
        super(MenuType.GENERIC_9x3, id, inv, new SimpleContainer(ROWS * 9), ROWS);
        this.player = player;
        refresh();
    }

    public static void open(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
            (id, inv, p) -> new MainShopMenu(id, inv, player),
            Component.literal("Chill Zone — Shard Shop")
        ));
    }

    private void refresh() {
        ItemStack filler = Ui.button(Ui.item("gray_stained_glass_pane"), Component.empty());

        // Glass border only. The five inner slots on the middle row remain empty,
        // except for the centre Deaths button.
        int[] border = {
            0,1,2,3,4,5,6,7,8,
            9,17,
            18,19,20,21,22,23,24,25,26
        };
        for (int slot : border) getContainer().setItem(slot, filler.copy());

        getContainer().setItem(DEATHS, Ui.button(
            Ui.item("skeleton_skull"),
            Ui.name("Deaths", ChatFormatting.AQUA, ChatFormatting.BOLD),
            Ui.lore("View your 28 most recent deaths."),
            Ui.lore("Click to open your death history.")
        ));
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput input, Player clicker) {
        if (slotId == DEATHS) DeathMenu.open(player);
    }

    @Override public ItemStack quickMoveStack(Player clicker, int slot) { return ItemStack.EMPTY; }
    @Override public boolean canTakeItemForPickAll(ItemStack stack, net.minecraft.world.inventory.Slot slot) { return false; }
    @Override public boolean stillValid(Player clicker) { return true; }
}
