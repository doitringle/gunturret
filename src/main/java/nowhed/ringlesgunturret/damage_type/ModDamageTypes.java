package nowhed.ringlesgunturret.damage_type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.RinglesGunTurret;
import org.jetbrains.annotations.Nullable;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> SHOT_BY_TURRET = register("shot_by_turret");
    public static final RegistryKey<DamageType> SHOT_BY_TURRET_PASSIVE = register("shot_by_turret_passive");


    public static DamageSource createDamageSource(World world, RegistryKey<DamageType> damageTypeRegistryKey, @Nullable Entity source, @Nullable Entity owner) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(damageTypeRegistryKey),source,owner);
    }

    private static RegistryKey<DamageType> register(String name) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(RinglesGunTurret.MOD_ID, name));
    }

    public static void registerModDamageTypes() {
        //RinglesGunTurret.LOGGER.info("registering damage types for " + RinglesGunTurret.MOD_ID);
    }

}

//thank you り月 from the fabric modding discord