package nowhed.ringlesgunturret.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.entity.custom.BulletProjectileEntity;

public class BulletProjectileRenderer extends EntityRenderer<BulletProjectileEntity> {

    private static final Identifier TEXTURE = new Identifier(RinglesGunTurret.MOD_ID,"textures/entity/projectile.png");

    public BulletProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(BulletProjectileEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BulletProjectileEntity bulletProjectileEntity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        super.render(bulletProjectileEntity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
