package nowhed.ringlesgunturret.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.damage_type.ModDamageTypes;
import nowhed.ringlesgunturret.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

public class BulletProjectileEntity extends ProjectileEntity {
   //public static final boolean canCollide
    private int age;
    public static final float BULLETDAMAGE = 6;
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
        super.tick();
        this.age++;


        if (this.age >= removeTime) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        if(age > 1) {
            this.getWorld().addParticle(ParticleTypes.CLOUD, getX(), getY(), getZ(), getVelocity().x * 0.1, 0.0, getVelocity().z * 0.1);
        }
        //setPos(getX() + getVelocity().x, getY(), getZ() + getVelocity().z);

        Vec3d currentPosition = this.getPos();

        Vec3d nextPosition = currentPosition.add(this.getVelocity());

        this.noClip = true;

        EntityHitResult entityHitResult = this.getEntityCollision(currentPosition, nextPosition);

        if (entityHitResult != null && entityHitResult.getEntity().canHit() && !entityHitResult.getEntity().getType().equals(ModEntities.BULLET_PROJECTILE)) {
            this.onEntityHit(entityHitResult);
        }

        BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(currentPosition, nextPosition, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this));

        if (blockHitResult.getType() != HitResult.Type.MISS) {
            this.onBlockHit(blockHitResult);
        }

        this.move(MovementType.SELF, this.getVelocity());


    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d start, Vec3d end) {
        return ProjectileUtil.getEntityCollision(this.getWorld(), this, start, end, this.getBoundingBox().stretch(this.getVelocity()).expand(0.5D), this::canHit);
    }

    @Override
    protected boolean canHit(Entity entity) {
        return entity.canBeHitByProjectile();
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
    protected void onCollision(HitResult hitResult) {

        System.out.println();
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)hitResult);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult)hitResult);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {

        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();

        if(!entity.canBeHitByProjectile()) {
            return;
        }

        World world = this.getWorld();

        DamageSource damageSource = ModDamageTypes.createDamageSource(world, ModDamageTypes.SHOT_BY_TURRET);

        entity.damage(damageSource,BULLETDAMAGE);

            // issue?

        entity.addVelocity(this.getVelocity().multiply(0.15, 0.1, 0.15));

        if (!entity.isAlive() && super.getOwner() != null && super.getOwner().getType().equals(EntityType.PLAYER)) {
            entity.getServer().getPlayerManager().getPlayer(super.getOwner().getUuid())
                    .incrementStat(RinglesGunTurret.KILLS_WITH_GUN_TURRET);
            // got kill = increment stat
        }

        this.discard();
    }


}
