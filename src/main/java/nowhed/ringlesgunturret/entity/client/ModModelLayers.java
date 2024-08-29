package nowhed.ringlesgunturret.entity.client;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;

public class ModModelLayers {

    public static final EntityModelLayer BULLET_PROJECTILE =
            new EntityModelLayer(new Identifier(RinglesGunTurret.MOD_ID, "bullet_projectile"), "main");

}
