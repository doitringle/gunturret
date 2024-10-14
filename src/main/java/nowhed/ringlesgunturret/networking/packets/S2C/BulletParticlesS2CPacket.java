package nowhed.ringlesgunturret.networking.packets.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class BulletParticlesS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender response) {

        if(client.world == null) return;

        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        double vx = buf.readDouble();
        double vz = buf.readDouble();

        float damageValue = buf.readFloat();

        double velocityModifier = damageValue / 35;

        ParticleEffect particleType = switch ((int) damageValue) {
            case 3 -> // usually 3, weakest
                    ParticleTypes.CLOUD;
            case 4 -> // Technically any damageValue between 4-5
                    ParticleTypes.SMOKE;
            case 5 -> // Ditto, but for 5-6
                    ParticleTypes.LARGE_SMOKE;
            default ->
                    ParticleTypes.SOUL_FIRE_FLAME;
        };

        client.world.addParticle(particleType, x, y+0.2, z, vx * velocityModifier, 0.0, vz * velocityModifier);

    }
}
