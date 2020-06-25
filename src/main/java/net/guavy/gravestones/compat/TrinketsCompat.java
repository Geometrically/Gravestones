package net.guavy.gravestones.compat;

import dev.emi.trinkets.api.TrinketsApi;
import net.guavy.gravestones.api.GravestonesApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TrinketsCompat implements GravestonesApi {
    @Override
    public List<ItemStack> getInventory(PlayerEntity entity) {
        Inventory inventory = TrinketsApi.getTrinketsInventory(entity);
        List<ItemStack> itemStacks = new ArrayList<>();

        for(int i = 0; i < inventory.size(); i++) {
            itemStacks.add(inventory.getStack(i));
        }

        return itemStacks;
    }

    @Override
    public List<ItemStack> setInventory(List<ItemStack> inventory, PlayerEntity entity) {
        for(ItemStack itemStack : inventory) {
            TrinketsApi.getTrinketComponent(entity).equip(itemStack);
        }
        return null;
    }

    @Override
    public int getInventorySize(PlayerEntity entity) {
        return TrinketsApi.getTrinketsInventory(entity).size();
    }
}
