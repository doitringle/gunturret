package nowhed.ringlesgunturret;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import nowhed.ringlesgunturret.block.entity.GunTurretEntityRenderer;
import nowhed.ringlesgunturret.block.entity.ModBlockEntities;
import nowhed.ringlesgunturret.gui.GunTurretScreen;
import nowhed.ringlesgunturret.gui.ModScreenHandlers;

public class RinglesGunTurretClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.GUN_TURRET_BLOCK_ENTITY,GunTurretEntityRenderer::new);

        HandledScreens.register(ModScreenHandlers.GUN_TURRET_SCREEN_HANDLER, GunTurretScreen::new);
    }
}
