package com.chillzone.shardshop.ui;

import com.chillzone.shardshop.ChillZoneShardShop;
import com.chillzone.shardshop.UpgradeService;
import com.chillzone.shardshop.UpgradeStore;
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

public final class UpgradeToggleMenu extends ChestMenu {
    private static final int ON = 11, OFF = 15, BACK = 22;
    private final ServerPlayer player;
    private final UpgradeStore.Upgrade upgrade;

    private UpgradeToggleMenu(int id, Inventory inv, ServerPlayer player, UpgradeStore.Upgrade upgrade) {
        super(MenuType.GENERIC_9x3, id, inv, new SimpleContainer(27), 3);
        this.player = player;
        this.upgrade = upgrade;
        refresh();
    }

    public static void open(ServerPlayer player, UpgradeStore.Upgrade upgrade) {
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new UpgradeToggleMenu(id, inv, player, upgrade),
            Component.literal(UpgradeService.display(upgrade) + " — Settings")));
    }

    private void refresh() {
        ItemStack filler = Ui.button(Ui.item("gray_stained_glass_pane"), Component.empty());
        for (int i = 0; i < 27; i++) getContainer().setItem(i, filler.copy());
        boolean enabled = ChillZoneShardShop.upgrades().enabled(player.getUUID(), upgrade);
        getContainer().setItem(ON, Ui.button(Ui.item("green_stained_glass_pane"),
            Ui.name("Turn ON", ChatFormatting.GREEN, ChatFormatting.BOLD),
            Ui.lore(enabled ? "This upgrade is already enabled." : "Click to enable this upgrade.")));
        getContainer().setItem(OFF, Ui.button(Ui.item("red_stained_glass_pane"),
            Ui.name("Turn OFF", ChatFormatting.RED, ChatFormatting.BOLD),
            Ui.lore(enabled ? "Click to disable this upgrade." : "This upgrade is already disabled.")));
        getContainer().setItem(13, Ui.button(Ui.item(enabled ? "lime_dye" : "gray_dye"),
            Ui.name("Current: " + (enabled ? "ON" : "OFF"), enabled ? ChatFormatting.GREEN : ChatFormatting.GRAY, ChatFormatting.BOLD)));
        getContainer().setItem(BACK, Ui.button(Ui.item("arrow"), Ui.name("Back", ChatFormatting.YELLOW)));
    }

    @Override public void clicked(int slotId, int button, ContainerInput input, Player clicker) {
        if (slotId == BACK) { UpgradeMenu.open(player); return; }
        if (slotId == ON) ChillZoneShardShop.upgrades().setEnabled(player.getUUID(), upgrade, true);
        else if (slotId == OFF) ChillZoneShardShop.upgrades().setEnabled(player.getUUID(), upgrade, false);
        else return;
        refresh();
    }

    @Override public ItemStack quickMoveStack(Player clicker, int slot) { return ItemStack.EMPTY; }
    @Override public boolean canTakeItemForPickAll(ItemStack stack, net.minecraft.world.inventory.Slot slot) { return false; }
    @Override public boolean stillValid(Player clicker) { return true; }
}
