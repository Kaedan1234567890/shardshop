package com.chillzone.shardshop.ui;

import com.chillzone.shardshop.*;
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
import static com.chillzone.shardshop.UpgradeStore.Upgrade;

/** Purchase and toggle menus; green confirm / red cancel with descriptive hover. */
public final class UpgradeMenu extends ChestMenu {
    private final ServerPlayer player;
    private final Upgrade selected;
    private UpgradeMenu(int id,Inventory inv,ServerPlayer player,Upgrade selected){super(MenuType.GENERIC_9x3,id,inv,new SimpleContainer(27),3);this.player=player;this.selected=selected;refresh();}
    public static void open(ServerPlayer p){open(p,null);}
    private static void open(ServerPlayer p,Upgrade selected){p.openMenu(new SimpleMenuProvider((id,inv,pl)->new UpgradeMenu(id,inv,p,selected),Component.literal(selected==null?"Chill Zone — Upgrades":"Upgrade: "+selected)));}
    private void refresh(){for(int i=0;i<27;i++)getContainer().setItem(i,Ui.button(Ui.item("gray_stained_glass_pane"),Component.empty()));
        if(selected==null){draw(11,Upgrade.VEIN_MINER,"diamond_pickaxe","Vein Miner");draw(15,Upgrade.TREE_CHOPPER,"diamond_axe","Tree Chopper");}
        else {boolean bought=ChillZoneShardShop.upgrades().purchased(player.getUUID(),selected);
            getContainer().setItem(11,Ui.button(Ui.item("red_wool"),Ui.name("Cancel",ChatFormatting.RED),Ui.lore("Go back without making changes.")));
            getContainer().setItem(15,Ui.button(Ui.item("green_wool"),Ui.name(bought?"Toggle ON/OFF":"Confirm — 1 Shard",ChatFormatting.GREEN),Ui.lore(bought?"Enable or disable this purchased upgrade.":"Are you sure you want to buy this for 1 Shard?")));
            getContainer().setItem(13,Ui.button(Ui.item(selected==Upgrade.VEIN_MINER?"diamond_pickaxe":"diamond_axe"),Ui.name(selected==Upgrade.VEIN_MINER?"Vein Miner":"Tree Chopper",ChatFormatting.YELLOW),Ui.lore(bought?"Already owned":"One-time cost: 1 Shard")));
        }
    }
    private void draw(int slot,Upgrade u,String item,String label){var upgrades=ChillZoneShardShop.upgrades();boolean bought=upgrades.purchased(player.getUUID(),u);getContainer().setItem(slot,Ui.button(Ui.item(item),Ui.name(label,ChatFormatting.GREEN),Ui.lore(bought?(upgrades.enabled(player.getUUID(),u)?"Owned — ON":"Owned — OFF"):"Purchase for 1 Shard"),Ui.lore("Click to open.")));}
    @Override public void clicked(int slot,int button,ContainerInput input,Player clicker){if(selected==null){if(slot==11)open(player,Upgrade.VEIN_MINER);else if(slot==15)open(player,Upgrade.TREE_CHOPPER);return;}if(slot==11){open(player);return;}if(slot!=15)return;var store=ChillZoneShardShop.upgrades();if(store.purchased(player.getUUID(),selected))store.toggle(player.getUUID(),selected);else if(ShardBridge.take(player,1))store.purchase(player.getUUID(),selected);else player.sendSystemMessage(Component.literal("Not enough Shards (need 1)."));open(player);}
    @Override public ItemStack quickMoveStack(Player p,int slot){return ItemStack.EMPTY;}
    @Override public boolean canTakeItemForPickAll(ItemStack stack,net.minecraft.world.inventory.Slot slot){return false;}
    @Override public boolean stillValid(Player p){return true;}
}
