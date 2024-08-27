package nowhed.ringlesgunturret.gui;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;

public class ModScreenHandlers {
    public static final ScreenHandlerType<GunTurretScreenHandler> GUN_TURRET_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(RinglesGunTurret.MOD_ID,"gun_turret"),
                    new ExtendedScreenHandlerType<>(GunTurretScreenHandler::new));
    public static void registerScreenHandlers() {
        RinglesGunTurret.LOGGER.info("Registering screen handlers for " + RinglesGunTurret.MOD_ID);
    }
}
