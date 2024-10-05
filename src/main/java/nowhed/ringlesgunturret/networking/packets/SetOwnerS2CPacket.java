package nowhed.ringlesgunturret.networking.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;
import nowhed.ringlesgunturret.gui.GunTurretScreen;

public class SetOwnerS2CPacket {
    // sets the GunTurretBlockEntity on the Client's world to the correct Owner
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender response) {

        BlockPos blockPos = buf.readBlockPos();

        assert client.world != null;

        GunTurretBlockEntity blockEntity = (GunTurretBlockEntity) (client.world.getBlockEntity(blockPos));

        if (blockEntity != null) blockEntity.setOwner(client.player);

        GunTurretScreen clientScreen = (GunTurretScreen) client.currentScreen;
        if(clientScreen != null) clientScreen.claim.visible = false;
    }
}
