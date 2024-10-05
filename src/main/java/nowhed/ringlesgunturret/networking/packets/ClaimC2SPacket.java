package nowhed.ringlesgunturret.networking.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;
import nowhed.ringlesgunturret.networking.ModMessages;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;

public class ClaimC2SPacket {
    //sending this packet with a gunTurretBlock position will allow the player who sent it
    //to claim said gunTurretBlock on the server
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        //serverside

        if(!player.isCreative() && !server.getGameRules().getBoolean(RinglesGunTurret.SURVIVAL_CLAIM_TURRET)) {
            // if player is in survival & the game rules disallow claiming in survival, do not do anything
            // this is not explicitly required but just in case I fucked up in the gui code
            // it's just a contingency
            return;
        }

        BlockPos gunTurretBlockPos = buf.readBlockPos();

        if(gunTurretBlockPos == null) return;

        BlockEntity blockEntity = player.getWorld().getWorldChunk(gunTurretBlockPos).getBlockEntity(gunTurretBlockPos, WorldChunk.CreationType.IMMEDIATE);

        if(blockEntity instanceof GunTurretBlockEntity gunTurretBlockEntity
                && gunTurretBlockEntity.getOwner() == null) {

            // allow the player to claim the unowned block entity
            gunTurretBlockEntity.setOwner(player);

            // send success message
            // cause you cant change the container block's title at will
            // for some reason... either that or I couldn't figure it out
            player.sendMessage(Text.translatable("message.ringlesgunturret.claimsuccess"),true);

            // sets owner on client so that it appears in the menu
            PacketByteBuf response2 = PacketByteBufs.create();
            response2.writeBlockPos(gunTurretBlockPos);
            responseSender.sendPacket(ModMessages.SET_OWNER_ID,response2);

            PlayerData playerState = StateSaver.getPlayerState(player,server.getWorld(World.OVERWORLD));

            if(playerState == null) return;
            //also update the player's screen to their own settings
            // sets player targeting data
            PacketByteBuf response = PacketByteBufs.create();
            response.writeString(playerState.targetSelection);
            response.writeString(playerState.playerList);
            response.writeBoolean(playerState.blacklist);
            responseSender.sendPacket(ModMessages.GET_PLAYER_DATA_ID, response);





}
        // successfully set player entity

    }
}
