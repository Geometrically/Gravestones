package net.guavy.gravestones.mixin;

import net.guavy.gravestones.block.entity.GravestoneBlockEntity;
import net.guavy.gravestones.config.GravestoneRetrievalType;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Inject(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, World world, BlockState blockState, Block block) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if(blockEntity instanceof GravestoneBlockEntity) {
            GravestoneBlockEntity gravestoneBlockEntity = (GravestoneBlockEntity) blockEntity;

            if(GravestonesConfig.getConfig().mainSettings.retrievalType != GravestoneRetrievalType.ON_BREAK && gravestoneBlockEntity.getGraveOwner() != null)
                cir.setReturnValue(false);

            if(gravestoneBlockEntity.getGraveOwner() != null && GravestonesConfig.getConfig().mainSettings.retrievalType == GravestoneRetrievalType.ON_BREAK)
                if(!gravestoneBlockEntity.getGraveOwner().getId().equals(this.networkHandler.getProfile().getId()) && !GravestonesConfig.getConfig().mainSettings.enableGraveLooting)
                    cir.setReturnValue(false);
        }
    }
}
