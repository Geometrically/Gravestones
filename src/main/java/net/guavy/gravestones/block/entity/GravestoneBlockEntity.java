package net.guavy.gravestones.block.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.guavy.gravestones.Gravestones;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        this.items = DefaultedList.ofSize(tag.getInt("ItemCount"), ItemStack.EMPTY);

        Inventories.readNbt(tag.getCompound("Items"), this.items);

        this.xp = tag.getInt("XP");

        if(tag.contains("GraveOwner"))
            this.graveOwner = NbtHelper.toGameProfile(tag.getCompound("GraveOwner"));

        if(tag.contains("CustomName"))
            this.customName = tag.getString("CustomName");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        tag.putInt("ItemCount", this.items.size());

        tag.put("Items", Inventories.writeNbt(new NbtCompound(), this.items, true));

        tag.putInt("XP", xp);

        if(graveOwner != null)
            tag.put("GraveOwner", NbtHelper.writeGameProfile(new NbtCompound(), graveOwner));
        if(customName != null && !customName.isEmpty())
            tag.putString("CustomName", customName);

        return tag;
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        if(graveOwner != null)
            tag.put("GraveOwner", NbtHelper.writeGameProfile(new NbtCompound(), this.graveOwner));
        if(customName != null && !customName.isEmpty())
            tag.putString("CustomName", customName);

        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        if(tag.contains("GraveOwner"))
            this.graveOwner = NbtHelper.toGameProfile(tag.getCompound("GraveOwner"));
        if(tag.contains("CustomName"))
            this.customName = tag.getString("CustomName");
    }
}
