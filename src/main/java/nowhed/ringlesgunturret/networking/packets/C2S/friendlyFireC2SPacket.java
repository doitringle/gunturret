package nowhed.ringlesgunturret.networking.packets.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;

public class friendlyFireC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        //serverside
        boolean avoidFriendlyFire = buf.readBoolean();
        PlayerData playerState = StateSaver.getPlayerState(player, server.getWorld(World.OVERWORLD));
        playerState.avoidFriendlyFire = avoidFriendlyFire;
    }
}
