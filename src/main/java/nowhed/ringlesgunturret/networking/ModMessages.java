package nowhed.ringlesgunturret.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.networking.packets.TargetSelectionC2SPacket;

public class ModMessages {
    public static final Identifier TARGET_SELECTION_ID = new Identifier(RinglesGunTurret.MOD_ID, "target_selection_packet");
    public static final Identifier BLACKLIST_ID = new Identifier(RinglesGunTurret.MOD_ID, "blacklist_packet");
    public static final Identifier PLAYER_LIST_ID = new Identifier(RinglesGunTurret.MOD_ID, "player_list_packet");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(TARGET_SELECTION_ID, TargetSelectionC2SPacket::receive);
    }

    public static void registerS2CPackets() {

    }
}
