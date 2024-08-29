package nowhed.ringlesgunturret.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.damage_type.ModDamageTypes;
import nowhed.ringlesgunturret.entity.ModEntities;

public class BulletProjectileEntity extends ProjectileEntity {
    private int age;
    public static final int removeTime = 60;
    public BulletProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.age = 0;
    }

    public BulletProjectileEntity(World world) {
        super(ModEntities.BULLET_PROJECTILE, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {
        this.age++;
        super.tick();

        if (this.age >= removeTime) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }


        this.getWorld().addParticle(ParticleTypes.CLOUD,getX(),getY(),getZ(), 0.0, 0.0, 0.0);
        setPos(getX() + getVelocity().x, getY(), getZ() + getVelocity().z);

        checkBlockCollision();
    }

    @Override
    protected boolean canHit(Entity entity) {
        return true;
        // return entity.canBeHitByProjectile() && getOwner() != null;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
         this.discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if(!entity.canBeHitByProjectile()) {
            return;
        }
        World world = entity.getWorld();

        DamageSource damageSource = ModDamageTypes.createDamageSource(world, ModDamageTypes.SHOT_BY_TURRET);
        entity.damage(damageSource,4f);

        this.discard();
    }
}
