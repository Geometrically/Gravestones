package net.guavy.gravestones.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.guavy.gravestones.Gravestones;
import net.guavy.gravestones.compat.TrinketsCompat;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity{

    @Shadow @Final private PlayerInventory inventory;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerInventory.dropAll()V"))
    private void dropAll(PlayerInventory inventory) {
        if (!GravestonesConfig.getConfig().mainSettings.enableGraves) {
            this.inventory.dropAll();
            return;
        }

        Gravestones.placeGrave(this.getWorld(), this.getPos(), this.inventory.player);

        if(FabricLoader.getInstance().isModLoaded("trinkets"))
            TrinketsCompat.dropAll((PlayerEntity) (Object) this);
    }
}
