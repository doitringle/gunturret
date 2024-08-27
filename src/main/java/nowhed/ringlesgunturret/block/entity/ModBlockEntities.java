package nowhed.ringlesgunturret.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.block.ModBlocks;

public class ModBlockEntities {
    public static final BlockEntityType<GunTurretBlockEntity>  GUN_TURRET_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(RinglesGunTurret.MOD_ID,"gun_turret_block_entity"),
                FabricBlockEntityTypeBuilder.create(GunTurretBlockEntity::new,
                ModBlocks.GUN_TURRET).build());
    public static void registerBlockEntities() {
        RinglesGunTurret.LOGGER.info("Registering block entities for " + RinglesGunTurret.MOD_ID);
    }
}