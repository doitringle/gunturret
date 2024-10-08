package nowhed.ringlesgunturret.networking.packets.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;


public class TargetSelectionC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        //serverside
        String selection = buf.readString();
        PlayerData playerState = StateSaver.getPlayerState(player,server.getWorld(World.OVERWORLD));
        playerState.targetSelection = selection;
        //player.sendMessage(Text.literal("Set turret aim selection to: " + selection));

        //String[] list = msg.split(",");
        //for (String i : list) {
        //    player.sendMessage(Text.literal(i));
        //}

    }
}
