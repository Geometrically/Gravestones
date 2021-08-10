package net.guavy.gravestones.mixin;

import net.guavy.gravestones.Gravestones;
import net.guavy.gravestones.block.entity.GravestoneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Explosion.class)
public class ExplosionMixin  {
    @Shadow @Final private World world;
    private BlockPos lastPos;

    @ModifyVariable(method = "affectWorld", at = @At(value = "STORE" , ordinal = 0), ordinal=0)
    private BlockPos modifyAffectedBlocks(BlockPos old) {
        lastPos = old;
        return old;
    }

    @ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal=0)
    private BlockState modifyAffectedBlocks(BlockState old) {
        if(old.getBlock() == Gravestones.GRAVESTONE) {
            BlockEntity blockEntity = world.getBlockEntity(lastPos);

            if(blockEntity instanceof GravestoneBlockEntity gravestoneBlockEntity) {
                if(gravestoneBlockEntity.getGraveOwner() != null)
                    return Blocks.AIR.getDefaultState();
            }
        }

        return old;
    }
}
