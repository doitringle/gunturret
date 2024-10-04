package nowhed.ringlesgunturret.networking.packets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
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
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

        //System.out.println("received rotation request from " + player.getName().getString());

        BlockPos gunTurretBlockPos = buf.readBlockPos();

        if(gunTurretBlockPos == null) return;

        //System.out.println("Position is not null (" + gunTurretBlockPos + ")");

        BlockEntity blockEntity = player.getWorld().getWorldChunk(gunTurretBlockPos).getBlockEntity(gunTurretBlockPos, WorldChunk.CreationType.IMMEDIATE);

        //System.out.println(blockEntity);

        if(!(blockEntity instanceof GunTurretBlockEntity)) return;

        //System.out.println("blockEntity is instance of GunTurretBlockEntity");

        float rotation = ((GunTurretBlockEntity) blockEntity).getRotation();

        //System.out.println("Got rotation as " + rotation);

        PacketByteBuf response = PacketByteBufs.create();
        response.writeFloat(rotation);
        response.writeBlockPos(gunTurretBlockPos);
        //System.out.println("Sending response...");
        responseSender.sendPacket(ModMessages.ROT_DATA_ID,response);

    }
}
