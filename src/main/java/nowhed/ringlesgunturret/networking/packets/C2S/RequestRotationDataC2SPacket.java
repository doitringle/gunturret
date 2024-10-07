package nowhed.ringlesgunturret.networking.packets.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;
import nowhed.ringlesgunturret.networking.ModMessages;

public class RequestRotationDataC2SPacket {
    // From the client, tells the server that a GunTurretBlockEntity is rendering and to request the top part rotation value and the barrel animation value
    // for that specific blockEntity
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        BlockPos gunTurretBlockPos = buf.readBlockPos();

        if(gunTurretBlockPos == null) return;

        BlockEntity blockEntity = player.getWorld().getWorldChunk(gunTurretBlockPos).getBlockEntity(gunTurretBlockPos, WorldChunk.CreationType.IMMEDIATE);

        if(!(blockEntity instanceof GunTurretBlockEntity)) return;

        float rotation = ((GunTurretBlockEntity) blockEntity).getRotation();
        int barrelRotation = ((GunTurretBlockEntity) blockEntity).getBarrelRotation();

        PacketByteBuf response = PacketByteBufs.create();

        response.writeFloat(rotation);
        response.writeInt(barrelRotation);
        response.writeBlockPos(gunTurretBlockPos);

        responseSender.sendPacket(ModMessages.ROT_DATA_ID,response);

    }
}
