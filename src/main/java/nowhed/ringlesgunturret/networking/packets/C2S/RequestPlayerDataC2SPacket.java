package nowhed.ringlesgunturret.networking.packets.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.networking.ModMessages;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;

public class RequestPlayerDataC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        PlayerData playerState = StateSaver.getPlayerState(player,server.getWorld(World.OVERWORLD));

        if(playerState == null) return;

        PacketByteBuf response = PacketByteBufs.create();
        response.writeString(playerState.targetSelection);
        response.writeString(playerState.playerList);
        response.writeBoolean(playerState.blacklist);
        responseSender.sendPacket(ModMessages.GET_PLAYER_DATA_ID, response);

    }
}
