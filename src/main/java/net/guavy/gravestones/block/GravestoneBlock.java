package net.guavy.gravestones.block;

import net.guavy.gravestones.Gravestones;
import net.guavy.gravestones.api.GravestonesApi;
import net.guavy.gravestones.block.entity.GravestoneBlockEntity;
import net.guavy.gravestones.config.GravestoneDropType;
import net.guavy.gravestones.config.GravestoneRetrievalType;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GravestoneBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public GravestoneBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if(GravestonesConfig.getConfig().mainSettings.retrievalType == GravestoneRetrievalType.ON_STEP && entity instanceof PlayerEntity playerEntity) {
            RetrieveGrave(playerEntity, world, pos);
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(GravestonesConfig.getConfig().mainSettings.retrievalType == GravestoneRetrievalType.ON_USE)
            RetrieveGrave(player, world, pos);

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(GravestonesConfig.getConfig().mainSettings.retrievalType == GravestoneRetrievalType.ON_BREAK)
            if(RetrieveGrave(player, world, pos))
                return;

        super.onBreak(world, pos, state, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ct) {
        return VoxelShapes.cuboid(0.1f, 0f, 0.1f, 0.9f, 0.3f, 0.9f);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GravestoneBlockEntity(pos, state);
    }

    private boolean RetrieveGrave(PlayerEntity playerEntity, World world, BlockPos pos) {
        if(world.isClient) return false;

        BlockEntity be = world.getBlockEntity(pos);

        if(!(be instanceof GravestoneBlockEntity)) return false;
        GravestoneBlockEntity blockEntity = (GravestoneBlockEntity) be;

        blockEntity.markDirty();

        if(blockEntity.getItems() == null) return false;
        if(blockEntity.getGraveOwner() == null) return false;

        if(!GravestonesConfig.getConfig().mainSettings.enableGraveLooting) {
            if (!playerEntity.getGameProfile().getId().equals(blockEntity.getGraveOwner().getId())) {
                return false;
            }
        }

        DefaultedList<ItemStack> items = blockEntity.getItems();

        DefaultedList<ItemStack> retrievalInventory = DefaultedList.of();

        retrievalInventory.addAll(playerEntity.getInventory().main);
        retrievalInventory.addAll(playerEntity.getInventory().armor);
        retrievalInventory.addAll(playerEntity.getInventory().offHand);

        for (GravestonesApi gravestonesApi : Gravestones.apiMods) {
            retrievalInventory.addAll(gravestonesApi.getInventory(playerEntity));
        }

        playerEntity.getInventory().clear();

        if(GravestonesConfig.getConfig().mainSettings.dropType == GravestoneDropType.PUT_IN_INVENTORY) {
            List<ItemStack> armor = items.subList(36, 40);

            for (ItemStack itemStack : armor) {
                EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);

                playerEntity.equipStack(equipmentSlot, itemStack);
            }

            playerEntity.equipStack(EquipmentSlot.OFFHAND, items.get(40));

            List<ItemStack> mainInventory = items.subList(0, 36);

            for (int i = 0; i < mainInventory.size(); i++) {
                playerEntity.getInventory().insertStack(i, mainInventory.get(i));
            }

            DefaultedList<ItemStack> extraItems = DefaultedList.of();

            List<Integer> openArmorSlots = getInventoryOpenSlots(playerEntity.getInventory().armor);

            for(int i = 0; i < 4; i++) {
                if(openArmorSlots.contains(i)) {
                    playerEntity.equipStack(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i), retrievalInventory.subList(36, 40).get(i));
                }
                else
                    extraItems.add(retrievalInventory.subList(36, 40).get(i));
            }

            if(playerEntity.getInventory().offHand.get(0) == ItemStack.EMPTY)
                playerEntity.equipStack(EquipmentSlot.OFFHAND, retrievalInventory.get(40));
            else
                extraItems.add(retrievalInventory.get(40));

            extraItems.addAll(retrievalInventory.subList(0, 36));
            if (retrievalInventory.size() > 41)
                extraItems.addAll(retrievalInventory.subList(41, retrievalInventory.size()));

            List<Integer> openSlots = getInventoryOpenSlots(playerEntity.getInventory().main);

            for(int i = 0; i < openSlots.size(); i++) {
                playerEntity.getInventory().insertStack(openSlots.get(i), extraItems.get(i));
            }

            DefaultedList<ItemStack> dropItems = DefaultedList.of();
            dropItems.addAll(extraItems.subList(openSlots.size(), extraItems.size()));

            int inventoryOffset = 41;

            for (GravestonesApi gravestonesApi : Gravestones.apiMods) {
                gravestonesApi.setInventory(items.subList(inventoryOffset, inventoryOffset + gravestonesApi.getInventorySize(playerEntity)), playerEntity);
                inventoryOffset += gravestonesApi.getInventorySize(playerEntity);
            }

            ItemScatterer.spawn(world, pos, dropItems);
        }
        else if (GravestonesConfig.getConfig().mainSettings.dropType == GravestoneDropType.DROP_ITEMS) {
            ItemScatterer.spawn(world, pos, blockEntity.getItems());
        }

        playerEntity.addExperience((int) (GravestonesConfig.getConfig().mainSettings.xpPercentage * blockEntity.getXp()));


        world.removeBlock(pos, false);
        return true;
    }

    private List<Integer> getInventoryOpenSlots(DefaultedList<ItemStack> inventory) {
        List<Integer> openSlots = new ArrayList<>();

        for (int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i) == ItemStack.EMPTY)
                openSlots.add(i);
        }

        return openSlots;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if(!(blockEntity instanceof GravestoneBlockEntity) || !itemStack.hasCustomName()) {
            super.onPlaced(world, pos, state, placer, itemStack);
            return;
        }

        GravestoneBlockEntity gravestoneBlockEntity = (GravestoneBlockEntity) blockEntity;

        gravestoneBlockEntity.setCustomName(itemStack.getOrCreateSubNbt("display").getString("Name"));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }
}
