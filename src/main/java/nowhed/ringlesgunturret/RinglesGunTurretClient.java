package nowhed.ringlesgunturret;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import nowhed.ringlesgunturret.block.entity.GunTurretEntityRenderer;
import nowhed.ringlesgunturret.block.entity.ModBlockEntities;
import nowhed.ringlesgunturret.entity.ModEntities;
import nowhed.ringlesgunturret.entity.client.BulletProjectileRenderer;
import nowhed.ringlesgunturret.gui.GunTurretScreen;
import nowhed.ringlesgunturret.gui.ModScreenHandlers;
import nowhed.ringlesgunturret.networking.ModMessages;

public class RinglesGunTurretClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.GUN_TURRET_BLOCK_ENTITY,GunTurretEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.GUN_TURRET_SCREEN_HANDLER, GunTurretScreen::new);
        //HandledScreens.register(ModScreenHandlers.ITEM_SETTINGS_SCREEN_HANDLER, ItemSettingsScreen::new);

        // THIS IS SO IMPORTANT v
        EntityRendererRegistry.register(ModEntities.BULLET_PROJECTILE, BulletProjectileRenderer::new);
        ModMessages.registerS2CPackets();
    }
}
