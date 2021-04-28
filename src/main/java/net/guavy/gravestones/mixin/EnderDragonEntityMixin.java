package net.guavy.gravestones.mixin;

import net.guavy.gravestones.Gravestones;
import net.guavy.gravestones.config.GravestonesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin extends MobEntity implements Monster {

    protected EnderDragonEntityMixin(EntityType<? extends EnderDragonEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author: LapisLiozuli
     * @reason: dragon_immune tag JSON cannot be toggled by config so I decided to allow a toggle via mixin instead.
     */
    @Overwrite
    private boolean destroyBlocks(Box box) {
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.floor(box.minY);
        int k = MathHelper.floor(box.minZ);
        int l = MathHelper.floor(box.maxX);
        int m = MathHelper.floor(box.maxY);
        int n = MathHelper.floor(box.maxZ);
        boolean bl = false;
        boolean bl2 = false;

        for(int o = i; o <= l; ++o) {
            for(int p = j; p <= m; ++p) {
                for(int q = k; q <= n; ++q) {
                    BlockPos blockPos = new BlockPos(o, p, q);
                    BlockState blockState = this.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if (!blockState.isAir() && blockState.getMaterial() != Material.FIRE) {
                        // If mob griefing is True and block isn't dragon immune, then remove block.
                        if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && !BlockTags.DRAGON_IMMUNE.contains(block)) {
                            // If dragon immunity is enabled in config and the block is a Gravestone, don't break it.
                            // Kind of janky but effective. Added a nested if-else statement.
                            if (GravestonesConfig.getConfig().mainSettings.enableDragonImmunity && block == Gravestones.GRAVESTONE) {
                                bl = true;
                            }
                            else {
                                bl2 = this.world.removeBlock(blockPos, false) || bl2;
                            }
                        } else {
                            bl = true;
                        }
                    }
                }
            }
        }

        if (bl2) {
            BlockPos blockPos2 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
            this.world.syncWorldEvent(2008, blockPos2, 0);
        }

        return bl;
    }
}
