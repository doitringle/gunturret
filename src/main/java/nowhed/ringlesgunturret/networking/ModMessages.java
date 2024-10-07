package nowhed.ringlesgunturret.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.networking.packets.C2S.*;
import nowhed.ringlesgunturret.networking.packets.S2C.*;

public class ModMessages {
    public static final Identifier TARGET_SELECTION_ID = new Identifier(RinglesGunTurret.MOD_ID, "target_selection_packet");
    public static final Identifier BLACKLIST_ID = new Identifier(RinglesGunTurret.MOD_ID, "blacklist_packet");
    public static final Identifier PLAYER_LIST_ID = new Identifier(RinglesGunTurret.MOD_ID, "player_list_packet");
    public static final Identifier REQUEST_PLAYER_DATA_ID = new Identifier(RinglesGunTurret.MOD_ID,"request_player_data_packet");
    public static final Identifier REQUEST_ROT_DATA_ID = new Identifier(RinglesGunTurret.MOD_ID,"request_rotation_data_packet");
    public static final Identifier CLAIM_ID = new Identifier(RinglesGunTurret.MOD_ID,"claim_data_packet");
    public static final Identifier FULFILL_PLAYER_VELOCITY_ID = new Identifier(RinglesGunTurret.MOD_ID,"fulfill_velocity_packet");

    public static final Identifier GET_PLAYER_DATA_ID = new Identifier(RinglesGunTurret.MOD_ID,"get_player_data_packet");
    public static final Identifier ROT_DATA_ID = new Identifier(RinglesGunTurret.MOD_ID,"rotation_data_packet");
    public static final Identifier SET_OWNER_ID = new Identifier(RinglesGunTurret.MOD_ID,"set_owner_packet");
    public static final Identifier REQUEST_PLAYER_VELOCITY_ID = new Identifier(RinglesGunTurret.MOD_ID,"request_player_velocity_packet");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(TARGET_SELECTION_ID, TargetSelectionC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(PLAYER_LIST_ID, PlayerListC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(BLACKLIST_ID, BlacklistC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_PLAYER_DATA_ID, RequestPlayerDataC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_ROT_DATA_ID, RequestRotationDataC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CLAIM_ID, ClaimC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(FULFILL_PLAYER_VELOCITY_ID, FulfillPlayerVelocityC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(GET_PLAYER_DATA_ID, FulfillPlayerDataS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ROT_DATA_ID, FulfillRotationDataS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SET_OWNER_ID, SetOwnerS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(REQUEST_PLAYER_VELOCITY_ID, RequestPlayerVelocityS2CPacket::receive);
    }
}
