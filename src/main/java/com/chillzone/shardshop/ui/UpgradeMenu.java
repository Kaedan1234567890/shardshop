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

public final class UpgradeMenu extends ChestMenu {
    private static final int VEIN = 11, TREE = 15, BACK = 22;
    private final ServerPlayer player;

    private UpgradeMenu(int id, Inventory inv, ServerPlayer player) {
        super(MenuType.GENERIC_9x3, id, inv, new SimpleContainer(27), 3);
        this.player = player;
        refresh();
    }

    public static void open(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new UpgradeMenu(id, inv, player), Component.literal("Shop — Upgrades")));
    }

    private void refresh() {
        ItemStack filler = Ui.button(Ui.item("gray_stained_glass_pane"), Component.empty());
        for (int i = 0; i < 27; i++) getContainer().setItem(i, filler.copy());
        getContainer().setItem(VEIN, upgradeButton(UpgradeStore.Upgrade.VEIN_MINER, "diamond_pickaxe",
            "Mine connected ore veins at once.", "Works while using a pickaxe."));
        getContainer().setItem(TREE, upgradeButton(UpgradeStore.Upgrade.TREE_CHOPPER, "diamond_axe",
            "Chop connected logs at once.", "Works while using an axe."));
        getContainer().setItem(BACK, Ui.button(Ui.item("arrow"), Ui.name("Back", ChatFormatting.YELLOW)));
    }

    private ItemStack upgradeButton(UpgradeStore.Upgrade upgrade, String icon, String line1, String line2) {
        boolean unlocked = ChillZoneShardShop.upgrades().unlocked(player.getUUID(), upgrade);
        if (unlocked) {
            boolean enabled = ChillZoneShardShop.upgrades().enabled(player.getUUID(), upgrade);
            return Ui.button(Ui.item(icon),
                Ui.name(UpgradeService.display(upgrade), ChatFormatting.AQUA, ChatFormatting.BOLD),
                Ui.lore(line1), Ui.lore(line2),
                Ui.lore("Unlocked permanently."),
                Ui.lore("Currently: " + (enabled ? "ON" : "OFF")),
                Ui.lore("Click to change settings."));
        }
        int cost = UpgradeService.cost(upgrade);
        return Ui.button(Ui.item(icon),
            Ui.name(UpgradeService.display(upgrade), ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            Ui.lore(line1), Ui.lore(line2),
            Ui.lore("Permanent unlock: " + cost + " Shards"),
            Ui.lore("Click to review purchase."));
    }

    @Override public void clicked(int slotId, int button, ContainerInput input, Player clicker) {
        if (slotId == BACK) { MainShopMenu.open(player); return; }
        UpgradeStore.Upgrade upgrade = slotId == VEIN ? UpgradeStore.Upgrade.VEIN_MINER : slotId == TREE ? UpgradeStore.Upgrade.TREE_CHOPPER : null;
        if (upgrade == null) return;
        if (ChillZoneShardShop.upgrades().unlocked(player.getUUID(), upgrade)) {
            UpgradeToggleMenu.open(player, upgrade);
            return;
        }
        ItemStack info = getContainer().getItem(slotId).copy();
        ConfirmMenu.open(player, "Unlock — " + UpgradeService.display(upgrade), info,
            () -> { if (UpgradeService.purchase(player, upgrade)) UpgradeToggleMenu.open(player, upgrade); else UpgradeMenu.open(player); },
            () -> UpgradeMenu.open(player));
    }

    @Override public ItemStack quickMoveStack(Player clicker, int slot) { return ItemStack.EMPTY; }
    @Override public boolean canTakeItemForPickAll(ItemStack stack, net.minecraft.world.inventory.Slot slot) { return false; }
    @Override public boolean stillValid(Player clicker) { return true; }
}
