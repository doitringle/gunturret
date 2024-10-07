package nowhed.ringlesgunturret.networking.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;

public class FulfillPlayerVelocityC2SPacket {
    // server fulfills the player velocity request by writing it to the blockEntity that requested it
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        BlockPos blockPos = buf.readBlockPos();
        double x = buf.readDouble();
        double z = buf.readDouble();

        BlockEntity blockEntity = player.getWorld().getWorldChunk(blockPos).getBlockEntity(blockPos, WorldChunk.CreationType.IMMEDIATE);

        if(blockEntity instanceof GunTurretBlockEntity gunTurretBlockEntity) {

            Vec3d playerVelocity = new Vec3d(x, 0, z);

            gunTurretBlockEntity.setPlayerVelocity(playerVelocity);

        }
    }

}
