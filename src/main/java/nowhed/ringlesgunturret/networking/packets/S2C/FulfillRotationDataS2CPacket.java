package nowhed.ringlesgunturret.networking.packets.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;

public class FulfillRotationDataS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender response) {

        //System.out.println("Received Fufill request from client " + client.getName());

        Float rotation = buf.readFloat();
        int barrelRotation = buf.readInt();
        BlockPos gunTurretBlockPos = buf.readBlockPos();

        //System.out.println("Received rotation as " + rotation + " and block position as " + gunTurretBlockPos);

        if(client.world == null || client.world.getBlockEntity(gunTurretBlockPos) == null) return;

        //System.out.println("Client world is not null. the block entity at position is not null.");

        GunTurretBlockEntity blockEntity = (GunTurretBlockEntity) (client.world.getBlockEntity(gunTurretBlockPos));

        if(blockEntity == null) return;

        blockEntity.setClientRotation(rotation);

        blockEntity.setBarrelRotation(barrelRotation);

    }
}
