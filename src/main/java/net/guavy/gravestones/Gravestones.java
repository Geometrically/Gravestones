package net.guavy.gravestones;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.guavy.gravestones.api.GravestonesApi;
import net.guavy.gravestones.block.GravestoneBlock;
import net.guavy.gravestones.block.entity.GravestoneBlockEntity;
import net.guavy.gravestones.compat.TrinketsCompat;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;

public class Gravestones implements ModInitializer {

	public static final GravestoneBlock GRAVESTONE = new GravestoneBlock(FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f).build());
	public static BlockEntityType<GravestoneBlockEntity> GRAVESTONE_BLOCK_ENTITY;

	public static final ArrayList<GravestonesApi> apiMods = new ArrayList<>();

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("gravestones", "gravestone"), GRAVESTONE);
		GRAVESTONE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "gravestones:gravestone", BlockEntityType.Builder.create(GravestoneBlockEntity::new, GRAVESTONE).build(null));
		Registry.register(Registry.ITEM, new Identifier("gravestones", "gravestone"), new BlockItem(GRAVESTONE, new Item.Settings().group(ItemGroup.DECORATIONS)));

		AutoConfig.register(GravestonesConfig.class, GsonConfigSerializer::new);

		if(FabricLoader.getInstance().isModLoaded("trinkets"))
			apiMods.add(new TrinketsCompat());

		apiMods.addAll(FabricLoader.getInstance().getEntrypoints("gravestones", GravestonesApi.class));
	}

	public static void PlaceGrave(World world, Vec3d pos, PlayerEntity player) {
		if (world.isClient) return;

		BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);

		BlockState blockState = world.getBlockState(blockPos);

		if(!blockState.isSolidBlock(world, blockPos) && blockState.getBlock() != Blocks.AIR)
			blockPos = new BlockPos(pos.x, pos.y + 1, pos.z);

		Block block = blockState.getBlock();

		if(block != Blocks.AIR)  {
			block.onBreak(world, blockPos, blockState, player);

			boolean bl = world.removeBlock(blockPos, false);
			if (bl) {
				block.onBroken(world, blockPos, blockState);
			}
		}

		GravestoneBlockEntity gravestoneBlockEntity = new GravestoneBlockEntity();

		DefaultedList<ItemStack> combinedInventory = DefaultedList.of();

		combinedInventory.addAll(player.inventory.main);
		combinedInventory.addAll(player.inventory.armor);
		combinedInventory.addAll(player.inventory.offHand);

		for (GravestonesApi gravestonesApi : Gravestones.apiMods) {
			combinedInventory.addAll(gravestonesApi.getInventory(player));
		}

		gravestoneBlockEntity.setItems(combinedInventory);
		gravestoneBlockEntity.setGraveOwner(player.getGameProfile());
		gravestoneBlockEntity.setXp(player.totalExperience);

		player.totalExperience = 0;
		player.experienceProgress = 0;
		player.experienceLevel = 0;

		world.setBlockState(blockPos, Gravestones.GRAVESTONE.getDefaultState().with(Properties.HORIZONTAL_FACING, player.getHorizontalFacing()));
		world.setBlockEntity(blockPos, gravestoneBlockEntity);



		gravestoneBlockEntity.sync();
	}
}

