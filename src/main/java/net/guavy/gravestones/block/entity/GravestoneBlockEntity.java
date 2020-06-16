package net.guavy.gravestones.block.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.guavy.gravestones.Gravestones;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.collection.DefaultedList;

public class GravestoneBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private DefaultedList<ItemStack> items;
    private int xp;
    private GameProfile graveOwner;
    private String customName;

    public GravestoneBlockEntity() {
        super(Gravestones.GRAVESTONE_BLOCK_ENTITY);

        this.customName = "";
        this.graveOwner = null;
        this.xp = 0;
        this.items = DefaultedList.ofSize(41, ItemStack.EMPTY);
    }

    public void setItems(DefaultedList<ItemStack> items) {
        this.items = items;
        this.markDirty();
    }

    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void setGraveOwner(GameProfile gameProfile) {
        this.graveOwner = gameProfile;
        this.markDirty();
    }

    public GameProfile getGraveOwner() {
        return graveOwner;
    }

    public void setCustomName(String text) {
        this.customName = text;
        this.markDirty();
    }

    public String getCustomName() {
        return customName;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
        this.markDirty();
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        this.items = DefaultedList.ofSize(tag.getInt("ItemCount"), ItemStack.EMPTY);

        Inventories.fromTag(tag.getCompound("Items"), this.items);

        this.xp = tag.getInt("XP");

        if(tag.contains("GraveOwner"))
            this.graveOwner = NbtHelper.toGameProfile(tag.getCompound("GraveOwner"));

        if(tag.contains("CustomName"))
            this.customName = tag.getString("CustomName");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("ItemCount", this.items.size());

        tag.put("Items", Inventories.toTag(new CompoundTag(), this.items, true));

        tag.putInt("XP", xp);

        if(graveOwner != null)
            tag.put("GraveOwner", NbtHelper.fromGameProfile(new CompoundTag(), graveOwner));
        if(customName != null && !customName.isEmpty())
            tag.putString("CustomName", customName);

        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        if(compoundTag.contains("GraveOwner"))
            this.graveOwner = NbtHelper.toGameProfile(compoundTag.getCompound("GraveOwner"));
        if(compoundTag.contains("CustomName"))
            this.customName = compoundTag.getString("CustomName");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        if(graveOwner != null)
            compoundTag.put("GraveOwner", NbtHelper.fromGameProfile(new CompoundTag(), this.graveOwner));
        if(customName != null && !customName.isEmpty())
            compoundTag.putString("CustomName", customName);

        return compoundTag;
    }
}
