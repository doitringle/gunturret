package nowhed.ringlesgunturret.networking.packets.S2C;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import nowhed.ringlesgunturret.gui.GunTurretScreen;

public class FulfillPlayerDataS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender response) {
        if(client.currentScreen == null || !client.currentScreen.getClass().equals(GunTurretScreen.class)) return;
        String targetSelection = buf.readString();
        String playerList = buf.readString();
        Boolean blacklist = buf.readBoolean();
        Boolean friendlyFire = buf.readBoolean();

        GunTurretScreen clientScreen = (GunTurretScreen) client.currentScreen;

        clientScreen.setPlayerData(targetSelection,playerList,blacklist,friendlyFire);


    }
}
