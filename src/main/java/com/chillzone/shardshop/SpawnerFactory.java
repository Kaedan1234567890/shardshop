package com.chillzone.shardshop;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

/** Creates a spawner item already configured for the requested mob. */
public final class SpawnerFactory {
    private SpawnerFactory() {}

    public static ItemStack create(String mobPath) {
        ItemStack stack = new ItemStack(Items.SPAWNER);
        CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:" + mobPath);

        CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entity);

        CompoundTag blockEntity = new CompoundTag();
        blockEntity.putString("id", "minecraft:mob_spawner");
        blockEntity.put("SpawnData", spawnData);

        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntity));
        return stack;
    }
}
