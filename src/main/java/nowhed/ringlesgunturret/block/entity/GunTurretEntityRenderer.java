package nowhed.ringlesgunturret.block.entity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import nowhed.ringlesgunturret.block.ModBlocks;
import nowhed.ringlesgunturret.block.custom.GunTurretBlockTop;
import nowhed.ringlesgunturret.networking.ModMessages;
import org.joml.Matrix4f;

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

            PacketByteBuf buf = PacketByteBufs.create().writeBlockPos(blockEntity.getPos());
            ClientPlayNetworking.send(ModMessages.REQUEST_ROT_DATA_ID, buf);

            matrices.push();

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

            if(!MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getPos().isInRange(blockEntity.getPos().toCenterPos(), 24)) return;

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            Text text = (blockEntity.getOwner() == null) ? Text.translatable("block_entity.ringlesgunturret.no_owner") : blockEntity.getOwner().getDisplayName();

            drawText(2.4, 0.03f, text, matrices, textRenderer, vertexConsumers, light);

    }

    private void drawText(double y, float size, Text text, MatrixStack matrices,TextRenderer textRenderer, VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();

        matrices.translate(0.5,y,0.5);

        matrices.scale(-size,-size,-size); // 0.03f

        matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        textRenderer.draw(text, -textRenderer.getWidth(text) / 2.0f,0, 16777215, true, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL,5592405,light);

        matrices.pop();

    }

}
