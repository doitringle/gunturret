package nowhed.ringlesgunturret.networking.packets.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import nowhed.ringlesgunturret.networking.ModMessages;

public class RequestPlayerVelocityS2CPacket {
    // serverside gun turret requests certain player's Velocity
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender response) {

        BlockPos blockPos = buf.readBlockPos();

        if(client.player == null) return;

        double x = client.player.getVelocity().getX();
        double z = client.player.getVelocity().getZ();

        PacketByteBuf responseBuf = PacketByteBufs.create();

        responseBuf.writeBlockPos(blockPos);
        responseBuf.writeDouble(x);
        responseBuf.writeDouble(z);
        //System.out.println("Sending response...");
        response.sendPacket(ModMessages.FULFILL_PLAYER_VELOCITY_ID,responseBuf);

    }
}
