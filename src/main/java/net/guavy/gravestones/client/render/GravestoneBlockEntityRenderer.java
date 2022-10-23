package net.guavy.gravestones.client.render;

import net.guavy.gravestones.block.entity.GravestoneBlockEntity;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class GravestoneBlockEntityRenderer implements BlockEntityRenderer<GravestoneBlockEntity> {

    public GravestoneBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public BlockEntityRenderDispatcher dispatcher;

    public GravestoneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
         this.dispatcher = ctx.getRenderDispatcher();
    }


    @Override
    public void render(GravestoneBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        Direction direction = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);

        matrices.push();

        matrices.scale(0.75f, 0.75f, 0.75f);
        matrices.translate(0, 0, 0);

        switch (direction) {
            case NORTH:
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                matrices.translate(-1.2, 0.6, -0.9);
                break;
            case SOUTH:
                matrices.translate(0.15, 0.6, 0.4);
                break;
            case EAST:
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
                matrices.translate(-1.2, 0.6, 0.4);
                break;
            case WEST:
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270));
                matrices.translate(0.15, 0.6, -0.9);
                break;
        }

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));

        if(blockEntity.getGraveOwner() != null) {
            SkullBlockEntityRenderer.renderSkull(null, 0f, 0f, matrices, vertexConsumers, light, SkullBlockEntityRenderer.getModels(MinecraftClient.getInstance().getEntityModelLoader()).get(SkullBlock.Type.PLAYER), SkullBlockEntityRenderer.getRenderLayer(SkullBlock.Type.PLAYER, blockEntity.getGraveOwner()));
        }


        matrices.pop();
        //Outline
        String text= "";
        if(blockEntity.getGraveOwner() != null) {
            text = blockEntity.getGraveOwner().getName();
        }
        else if(blockEntity.getCustomName() != null) {
            if( !blockEntity.getCustomName().isEmpty()) {
                text = blockEntity.getCustomName().substring(9);
                text = text.substring(0, text.length() - 2);
            }
        }


        //Main Text
        matrices.push();

        int width = MinecraftClient.getInstance().textRenderer.getWidth(text);

        float scale = 0.7F / width;

        switch (direction) {
            case NORTH:
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                matrices.translate(-1, 0, -1);
                break;
            case SOUTH:
                break;
            case EAST:
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
                matrices.translate(-1, 0, 0);
                break;
            case WEST:
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270));
                matrices.translate(0, 0, -1);
                break;
        }

        matrices.translate(0.5, 0, 0.5);
        matrices.translate(0, 0.6, 0.42);
        matrices.scale(-1, -1, 0);

        matrices.scale(scale, scale, scale);
        matrices.translate(-width / 2.0, -4.5, 0);

        MinecraftClient.getInstance().textRenderer.draw(text, 0, 0, 0xFFFFFF, true, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
        matrices.pop();
    }
}
