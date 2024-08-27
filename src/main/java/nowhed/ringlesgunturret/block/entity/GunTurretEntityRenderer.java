package nowhed.ringlesgunturret.block.entity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.block.ModBlocks;

@Environment(EnvType.CLIENT)
public class GunTurretEntityRenderer implements BlockEntityRenderer<GunTurretBlockEntity> {
   private static final ItemStack stack = new ItemStack(ModBlocks.GUN_TURRET_TOP);
    public GunTurretEntityRenderer(BlockEntityRendererFactory.Context ctx) {}
        @Override
        public void render(GunTurretBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                VertexConsumerProvider vertexConsumers, int light, int overlay) {
            matrices.translate(0.5,0.5,0.5);
            //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.getWorld().getTime() + tickDelta) * 4));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getRotation()));
            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 0);
    }

}
