package nowhed.ringlesgunturret;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import nowhed.ringlesgunturret.block.entity.GunTurretEntityRenderer;
import nowhed.ringlesgunturret.block.entity.ModBlockEntities;

public class RinglesGunTurretClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.GUN_TURRET_BLOCK_ENTITY,GunTurretEntityRenderer::new);
    }
}
