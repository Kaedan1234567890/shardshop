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

/** The fourth category in the EXISTING Shard Shop: choose -> buy -> toggle. */
public final class UpgradeMenu extends ChestMenu {
    private static final int PICKAXE=11, AXE=15, CANCEL=11, CONFIRM=15, OFF=11, ON=15, BACK=22;
    private final ServerPlayer player;
    private final Upgrade selected;
    private UpgradeMenu(int id,Inventory inventory,ServerPlayer player,Upgrade selected) {
        super(MenuType.GENERIC_9x3,id,inventory,new SimpleContainer(27),3);
        this.player=player;this.selected=selected;refresh();
    }
    public static void open(ServerPlayer p){open(p,null);}
    private static void open(ServerPlayer p,Upgrade u){
        p.openMenu(new SimpleMenuProvider((id,inv,who)->new UpgradeMenu(id,inv,p,u),
            Component.literal(u==null?"Shop — Upgrades":name(u)+" — "+(ChillZoneShardShop.upgrades().purchased(p.getUUID(),u)?"Settings":"Purchase"))));
    }
    private static String name(Upgrade u){return u==Upgrade.VEIN_MINER?"Vein Miner":"Tree Chopper";}
    private int price(Upgrade u){return u==Upgrade.VEIN_MINER?ChillZoneShardShop.config().veinMinerUnlockCost:ChillZoneShardShop.config().treeChopperUnlockCost;}
    private static String icon(Upgrade u){return u==Upgrade.VEIN_MINER?"diamond_pickaxe":"diamond_axe";}
    private void item(int slot,String item,String title,ChatFormatting color,String... description){
        Component[] lore=new Component[description.length];
        for(int i=0;i<description.length;i++)lore[i]=Ui.lore(description[i]);
        getContainer().setItem(slot,Ui.button(Ui.item(item),Ui.name(title,color,ChatFormatting.BOLD),lore));
    }
    private void refresh(){
        ItemStack filler=Ui.button(Ui.item("gray_stained_glass_pane"),Component.empty());
        for(int i=0;i<27;i++)getContainer().setItem(i,filler.copy());
        if(selected==null){
            for(Upgrade upgrade:Upgrade.values()){
                boolean owned=ChillZoneShardShop.upgrades().purchased(player.getUUID(),upgrade);
                boolean enabled=ChillZoneShardShop.upgrades().enabled(player.getUUID(),upgrade);
                item(upgrade==Upgrade.VEIN_MINER?PICKAXE:AXE,icon(upgrade),name(upgrade),ChatFormatting.GREEN,
                    upgrade==Upgrade.VEIN_MINER?"Unlock mining connected ore veins.":"Unlock chopping connected tree logs.",
                    owned?(enabled?"Purchased — ON":"Purchased — OFF"):"Buy permanently for "+price(upgrade)+" Shard(s).",
                    owned?"Click to change ON/OFF settings.":"Click to purchase once; automatically enabled.");
            }
            item(BACK,"arrow","Back to Shop",ChatFormatting.YELLOW,"Return to Deaths, Bundles, Services and Upgrades.");
            return;
        }
        boolean owned=ChillZoneShardShop.upgrades().purchased(player.getUUID(),selected);
        item(13,icon(selected),name(selected),ChatFormatting.YELLOW,
            owned?"Already purchased — no further Shard costs.":"One-time price: "+price(selected)+" Shard(s).");
        if(!owned){
            item(CANCEL,"red_wool","Cancel",ChatFormatting.RED,"Cancel — do not purchase.","Return to the Upgrades menu.");
            item(CONFIRM,"green_wool","Confirm Purchase",ChatFormatting.GREEN,
                "Are you sure you want to buy "+name(selected)+"?",
                "Clicking confirms a purchase for "+price(selected)+" Shard(s).",
                "The upgrade will automatically be turned ON.");
        }else{
            boolean enabled=ChillZoneShardShop.upgrades().enabled(player.getUUID(),selected);
            item(OFF,"gray_dye","OFF",ChatFormatting.RED,
                "Click to turn "+name(selected)+" OFF.",enabled?"Currently ON":"Currently OFF");
            item(ON,"lime_dye","ON",ChatFormatting.GREEN,
                "Click to turn "+name(selected)+" ON.",enabled?"Currently ON":"Currently OFF");
            item(BACK,"arrow","Back to Upgrades",ChatFormatting.YELLOW,"No additional cost to change settings.");
        }
    }
    @Override public void clicked(int slot,int button,ContainerInput input,Player clicker){
        if(selected==null){
            if(slot==PICKAXE)open(player,Upgrade.VEIN_MINER);
            else if(slot==AXE)open(player,Upgrade.TREE_CHOPPER);
            else if(slot==BACK)MainShopMenu.open(player);
            return;
        }
        UpgradeStore store=ChillZoneShardShop.upgrades();
        boolean owned=store.purchased(player.getUUID(),selected);
        if(slot==BACK&&owned){open(player);return;}
        if(!owned){
            if(slot==CANCEL){open(player);return;}
            if(slot!=CONFIRM)return;
            // Recheck ownership and balance server-side, never trust the UI preview.
            if(!store.purchased(player.getUUID(),selected)){
                int cost=price(selected);
                if(!ShardBridge.take(player,cost)){
                    player.sendSystemMessage(Component.literal("Not enough Shards: "+name(selected)+" costs "+cost+" Shard(s)."));
                    open(player);return;
                }
                // Never double-charge: purchase only if still unowned, and refund if persistence fails.
                try {
                    if (store.purchase(player.getUUID(),selected)) {
                        ChillZoneShardShop.logTransaction(player,"UPGRADE_PURCHASE",cost,name(selected));
                        player.sendSystemMessage(Component.literal("Unlocked " + name(selected) + "! It is now ON. Open Upgrades again to toggle it."));
                    } else {
                        ShardBridge.refund(player,cost);
                    }
                } catch (RuntimeException ex) {
                    ShardBridge.refund(player,cost);
                    ChillZoneShardShop.LOGGER.error("Upgrade purchase failed; refunded {} Shards",cost,ex);
                    player.sendSystemMessage(Component.literal("Upgrade failed. Your Shards were refunded."));
                }
            }
            open(player);return;
        }
        if(slot==OFF)store.setEnabled(player.getUUID(),selected,false);
        else if(slot==ON)store.setEnabled(player.getUUID(),selected,true);
        else return;
        open(player,selected);
    }
    @Override public ItemStack quickMoveStack(Player p,int slot){return ItemStack.EMPTY;}
    @Override public boolean canTakeItemForPickAll(ItemStack stack,net.minecraft.world.inventory.Slot slot){return false;}
    @Override public boolean stillValid(Player p){return true;}
}
