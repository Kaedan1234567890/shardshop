package com.chillzone.shardshop.ui;

import com.chillzone.shardshop.ChillZoneShardShop;
import com.chillzone.shardshop.ShardBridge;
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
import static com.chillzone.shardshop.UpgradeStore.Upgrade;

/** Entirely rebuilt upgrade chest menu; never registers any command. */
public final class UpgradeMenu extends ChestMenu {
    private static final int PICKAXE=11,AXE=15,CANCEL=11,CONFIRM=15,OFF=11,ON=15,BACK=22;
    private final ServerPlayer player;
    private final Upgrade selected;
    private UpgradeMenu(int id,Inventory inv,ServerPlayer player,Upgrade selected){
        super(MenuType.GENERIC_9x3,id,inv,new SimpleContainer(27),3);
        this.player=player;this.selected=selected;fill();
    }
    public static void open(ServerPlayer p){open(p,null);}
    private static void open(ServerPlayer p,Upgrade u){
        p.openMenu(new SimpleMenuProvider((id,inv,who)->new UpgradeMenu(id,inv,p,u),
            Component.literal(u==null?"Shop — Upgrades":label(u)+(ChillZoneShardShop.upgrades().purchased(p.getUUID(),u)?" — Settings":" — Buy for 1 Shard"))));
    }
    private static String label(Upgrade u){return u==Upgrade.VEIN_MINER?"Vein Miner":"Tree Chopper";}
    private static String itemName(Upgrade u){return u==Upgrade.VEIN_MINER?"diamond_pickaxe":"diamond_axe";}
    private void button(int slot,String icon,String title,ChatFormatting color,String... text){
        Component[] lore=new Component[text.length];for(int i=0;i<text.length;i++)lore[i]=Ui.lore(text[i]);
        getContainer().setItem(slot,Ui.button(Ui.item(icon),Ui.name(title,color,ChatFormatting.BOLD),lore));
    }
    private void fill(){
        ItemStack pane=Ui.button(Ui.item("gray_stained_glass_pane"),Component.empty());
        for(int i=0;i<27;i++)getContainer().setItem(i,pane.copy());
        UpgradeStore store=ChillZoneShardShop.upgrades();
        if(selected==null){
            for(Upgrade u:Upgrade.values()){
                boolean owned=store.purchased(player.getUUID(),u);
                button(u==Upgrade.VEIN_MINER?PICKAXE:AXE,itemName(u),label(u),ChatFormatting.GREEN,
                    u==Upgrade.VEIN_MINER?"Mine connected ores (up to 100).":"Chop connected trees and decay their leaves.",
                    owned?(store.enabled(player.getUUID(),u)?"Purchased — ON":"Purchased — OFF"):"Purchase once for 1 Shard.",
                    owned?"Click to change ON/OFF settings.":"Click to buy. Confirm or cancel on the next screen.");
            }
            button(BACK,"arrow","Back to Shop",ChatFormatting.YELLOW,"Return to the four Shop categories.");
            return;
        }
        boolean owned=store.purchased(player.getUUID(),selected);
        button(13,itemName(selected),label(selected),ChatFormatting.YELLOW,owned?"Purchased permanently.":"One-time purchase costs 1 Shard.");
        if(!owned){
            button(CANCEL,"red_wool","Cancel",ChatFormatting.RED,"Cancel — do not buy this upgrade.");
            button(CONFIRM,"green_wool","Confirm",ChatFormatting.GREEN,"Confirm purchase for 1 Shard.","Unlock and automatically switch ON.");
        }else{
            button(OFF,"gray_dye","Turn OFF",ChatFormatting.GRAY,"Disable this upgrade at no cost.");
            button(ON,"lime_dye","Turn ON",ChatFormatting.GREEN,"Enable this upgrade at no cost.");
            button(BACK,"arrow","Back to Upgrades",ChatFormatting.YELLOW,"Return to axe and pickaxe.");
        }
    }
    @Override public void clicked(int slot,int mouseButton,ContainerInput input,Player clicker){
        if(selected==null){
            if(slot==PICKAXE)open(player,Upgrade.VEIN_MINER);
            else if(slot==AXE)open(player,Upgrade.TREE_CHOPPER);
            else if(slot==BACK)MainShopMenu.open(player);
            return;
        }
        UpgradeStore store=ChillZoneShardShop.upgrades();
        if(!store.purchased(player.getUUID(),selected)){
            if(slot==CANCEL){open(player);return;}
            if(slot!=CONFIRM)return;
            if(!ShardBridge.take(player,1)){
                player.sendSystemMessage(Component.literal("You need 1 Shard to unlock "+label(selected)+"."));open(player);return;
            }
            if(!store.purchase(player.getUUID(),selected)){
                ShardBridge.refund(player,1);
            }else{
                ChillZoneShardShop.logTransaction(player,"UPGRADE_PURCHASE",1,label(selected));
                player.sendSystemMessage(Component.literal(label(selected)+" unlocked and ON!"));
            }
            open(player);return;
        }
        if(slot==BACK){open(player);return;}
        if(slot==ON)store.setEnabled(player.getUUID(),selected,true);
        else if(slot==OFF)store.setEnabled(player.getUUID(),selected,false);
        else return;
        open(player,selected);
    }
    @Override public ItemStack quickMoveStack(Player p,int slot){return ItemStack.EMPTY;}
    @Override public boolean canTakeItemForPickAll(ItemStack stack,net.minecraft.world.inventory.Slot slot){return false;}
    @Override public boolean stillValid(Player p){return true;}
}
