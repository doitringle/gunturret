package nowhed.ringlesgunturret.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.entity.custom.BulletProjectileEntity;

public class ModEntities {
    public static final EntityType<BulletProjectileEntity> BULLET_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(RinglesGunTurret.MOD_ID,"bullet_projectile"), FabricEntityTypeBuilder.<BulletProjectileEntity>create(SpawnGroup.MISC, BulletProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.3f,0.25f)).build());

    public static void registerModEntities() {
        return;
    }
}
