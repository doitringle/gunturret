package nowhed.ringlesgunturret.block.entity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.RotationAxis;
import nowhed.ringlesgunturret.block.ModBlocks;
import nowhed.ringlesgunturret.block.custom.GunTurretBlockTop;
import nowhed.ringlesgunturret.networking.ModMessages;

@Environment(EnvType.CLIENT)
public class GunTurretEntityRenderer implements BlockEntityRenderer<GunTurretBlockEntity> {

    private static final ItemStack stack = new ItemStack(ModBlocks.GUN_TURRET_TOP);

    private static final Block gunTurretTop = ModBlocks.GUN_TURRET_TOP;

    private final BlockRenderManager blockRenderManager;

    public GunTurretEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
    }
        @Override
        public void render(GunTurretBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                VertexConsumerProvider vertexConsumers, int light, int overlay) {

            matrices.push();

            PacketByteBuf buf = PacketByteBufs.create().writeBlockPos(blockEntity.getPos());
            ClientPlayNetworking.send(ModMessages.REQUEST_ROT_DATA_ID, buf);

            matrices.translate(0.5,0,0.5);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getClientRotation()));

            matrices.translate(-0.5,0,-0.5);

            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            //MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 0);


            // easier than using GeckoLib or some other block animation library?
            // Probably not. But it's probably not THAT inefficient...
            BlockState block = gunTurretTop.getDefaultState().with(GunTurretBlockTop.ROTATION, blockEntity.getBarrelRotation());

            blockRenderManager.renderBlockAsEntity(block, matrices, vertexConsumers,lightAbove,overlay);

            matrices.pop();

    }
}
