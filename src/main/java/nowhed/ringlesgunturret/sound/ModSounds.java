package nowhed.ringlesgunturret.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;

public class ModSounds {
    public static final SoundEvent TURRET_ROTATES = registerSoundEvent("turret_rotates");
    public static final SoundEvent TURRET_SHOOTS = registerSoundEvent("turret_shoot");

    public static final SoundEvent OPEN = registerSoundEvent("open");
    public static final SoundEvent CLOSE = registerSoundEvent("close");

    private static SoundEvent registerSoundEvent(String name) {

        Identifier id = new Identifier(RinglesGunTurret.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    public static void registerSounds() {
        RinglesGunTurret.LOGGER.info("registering sounds for " + RinglesGunTurret.MOD_ID);
    }
}
