package nowhed.ringlesgunturret.damage_type;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.RinglesGunTurret;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> SHOT_BY_TURRET = register("shot_by_turret");


    //thank you り月 from the fabric modding discord

    public static DamageSource createDamageSource(World world, RegistryKey<DamageType> damageTypeRegistryKey) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(damageTypeRegistryKey));
    }

    private static RegistryKey<DamageType> register(String name) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(RinglesGunTurret.MOD_ID, name));
    }

    public static void registerModDamageTypes() {
        RinglesGunTurret.LOGGER.info("registering damage types for " + RinglesGunTurret.MOD_ID);
    }

}
