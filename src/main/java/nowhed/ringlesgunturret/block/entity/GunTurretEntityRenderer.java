package nowhed.ringlesgunturret.block.entity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.RotationAxis;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.block.ModBlocks;
import nowhed.ringlesgunturret.networking.ModMessages;
import org.joml.Matrix4f;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class GunTurretEntityRenderer implements BlockEntityRenderer<GunTurretBlockEntity> {
   private static final ItemStack stack = new ItemStack(ModBlocks.GUN_TURRET_TOP);
    public GunTurretEntityRenderer(BlockEntityRendererFactory.Context ctx) {}
        @Override
        public void render(GunTurretBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                VertexConsumerProvider vertexConsumers, int light, int overlay) {


            System.out.println(tickDelta);

            PacketByteBuf buf = PacketByteBufs.create().writeBlockPos(blockEntity.getPos());
            ClientPlayNetworking.send(ModMessages.REQUEST_ROT_DATA_ID,buf);



            matrices.translate(0.5,0.5,0.5);
            //RinglesGunTurret.LOGGER.info(blockEntity + " / " + ((GunTurretBlockEntity) blockEntity).getRotation() + " B");
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getClientRotation()));
            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 0);

            if(blockEntity.getOwner() == null) return;

            matrices.translate(0.5,0.5,0.5);
            matrices.scale(-0.03f,0.03f,-0.03f);
            matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getRotation());

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            String display = blockEntity.getOwner().getDisplayName().getString();
            int textWidth = textRenderer.getWidth(display);

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();

            textRenderer.draw(display, -textWidth / 2.0f, 3.0f, 16777215, true, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

    }
}
