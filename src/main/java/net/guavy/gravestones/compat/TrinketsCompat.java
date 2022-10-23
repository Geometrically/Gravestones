package net.guavy.gravestones.compat;

import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.guavy.gravestones.api.GravestonesApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrinketsCompat implements GravestonesApi {
    @Override
    public List<ItemStack> getInventory(PlayerEntity entity) {
        List<ItemStack> itemStacks = new ArrayList<>();

        Map<String, Map<String, TrinketInventory>> inventories = TrinketsApi.getTrinketComponent(entity).get().getInventory();
        for (Map<String, TrinketInventory> map : inventories.values()) {
            for (TrinketInventory inventory : map.values()) {
                for(int i = 0; i < inventory.size(); i++) {
                    itemStacks.add(inventory.getStack(i));
                }
            }
        }
        return itemStacks;
    }

    @Override
    public void setInventory(List<ItemStack> stacks, PlayerEntity entity) {
        for(ItemStack itemStack : stacks) {
            Map<String, Map<String, TrinketInventory>> inventories = TrinketsApi.getTrinketComponent(entity).get().getInventory();
            for (Map<String, TrinketInventory> map : inventories.values()) {
                for (TrinketInventory inventory : map.values()) {
                    for(int i = 0; i < inventory.size(); i++) {
                        if (inventory.getStack(i).isEmpty()) {
                            inventory.setStack(i, itemStack);
                            continue;
                        }
                        entity.getInventory().insertStack(itemStack);

                    }
                }
            }
        }
    }

    @Override
    public int getInventorySize(PlayerEntity entity) {
        return TrinketsApi.getTrinketComponent(entity).get().getInventory().size();
    }

    public static void dropAll(PlayerEntity entity) {
        TrinketsApi.getTrinketComponent(entity).get().getInventory().clear();
    }
}
