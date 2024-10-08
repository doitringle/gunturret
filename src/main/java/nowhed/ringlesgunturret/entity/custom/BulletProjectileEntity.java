package nowhed.ringlesgunturret.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.world.ServerWorld;
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

import java.util.UUID;

public class BulletProjectileEntity extends ProjectileEntity {

    private int age;
    @Nullable
    private PlayerEntity playerOwner;
    @Nullable
    private UUID playerOwnerUuid;
    private float damageValue;

    public static final float BULLETDAMAGE = 6;
    public static final int removeTime = 60;
    public BulletProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.age = 0;
        this.noClip = true;
        this.damageValue = 4.0f;
    }

    public BulletProjectileEntity(World world) {
        this(ModEntities.BULLET_PROJECTILE, world);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {

        if (nbt.containsUuid("playerOwnerUuid")) {
            this.playerOwnerUuid = nbt.getUuid("playerOwnerUuid");
            this.playerOwner = this.getWorld().getPlayerByUuid(this.playerOwnerUuid);
        }
        if(nbt.contains("damageValue",NbtElement.FLOAT_TYPE)) {
            this.damageValue = nbt.getFloat("damageValue");
        }
        if(nbt.contains("age",NbtElement.INT_TYPE)) {
            this.age = nbt.getInt("age");
        }

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {

        nbt.putUuid("playerOwnerUuid",this.playerOwnerUuid);
        nbt.putFloat("damageValue",this.damageValue);
        nbt.putInt("age",this.age);

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

        if(age > 1 && this.getWorld().isClient) {
            this.getWorld().addParticle(ParticleTypes.CLOUD, getX(), getY(), getZ(), getVelocity().x * 0.1, 0.0, getVelocity().z * 0.1);
        }

        //setPos(getX() + getVelocity().x, getY(), getZ() + getVelocity().z);

        Vec3d currentPosition = this.getPos();

        Vec3d nextPosition = currentPosition.add(this.getVelocity());

        EntityHitResult entityHitResult = this.getEntityCollision(currentPosition, nextPosition);

        if (entityHitResult != null && !entityHitResult.getEntity().getType().equals(ModEntities.BULLET_PROJECTILE)) {
            this.onEntityHit(entityHitResult);
        }

        BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(currentPosition, nextPosition, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this));

        if (blockHitResult.getType() != HitResult.Type.MISS) {
            this.onBlockHit(blockHitResult);
        }

        this.setPosition(this.getX() + this.getVelocity().x, this.getY(), this.getZ() + this.getVelocity().z);

    }

    public void setDamageValue(float dmg) {
        this.damageValue = dmg;
    }

    public void setPlayerOwner(@Nullable PlayerEntity playerEntity) {
        if (playerEntity != null) {
            this.playerOwnerUuid = playerEntity.getUuid();
            this.playerOwner = playerEntity;
        } else {
            this.playerOwnerUuid = null;
            this.playerOwner = null;
        }
    }

    @Nullable
    public PlayerEntity getPlayerOwner() {
        if (this.playerOwner != null && !this.playerOwner.isRemoved()) {
            return this.playerOwner;
        } else if (this.playerOwnerUuid != null && this.getWorld() instanceof ServerWorld) {
            this.playerOwner = ((ServerWorld) this.getWorld()).getPlayerByUuid(this.playerOwnerUuid);
            return this.playerOwner;
        } else {
            return null;
        }
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d start, Vec3d end) {
        return ProjectileUtil.getEntityCollision(this.getWorld(), this, start, end, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
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

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)hitResult);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult)hitResult);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {

        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient) {

            Entity entity = entityHitResult.getEntity();

            if (!entity.canBeHitByProjectile()) {
                return;
            }

            World world = this.getWorld();

            DamageSource damageSource;

            if (this.getPlayerOwner() != null) {
                damageSource = ModDamageTypes.createDamageSource(world, ModDamageTypes.SHOT_BY_TURRET, this, this.getPlayerOwner());
            } else {
                damageSource = ModDamageTypes.createDamageSource(world, ModDamageTypes.SHOT_BY_TURRET_PASSIVE, this, null);
            }
            // result = entity.damage(damageSource,BULLETDAMAGE);

            entity.damage(damageSource, this.damageValue);

            entity.addVelocity(this.getVelocity().multiply(0.05, 0.03, 0.05));

            if (!entity.isAlive() && getPlayerOwner() != null && entity.getServer() != null) {
                entity.getServer().getPlayerManager().getPlayer(getPlayerOwner().getUuid())
                        .incrementStat(RinglesGunTurret.KILLS_WITH_GUN_TURRET);
                // got kill = increment stat
                if (entity.isPlayer() && !entity.getUuid().equals(getPlayerOwner().getUuid())) {
                    entity.getServer().getPlayerManager().getPlayer(getPlayerOwner().getUuid())
                            .incrementStat(RinglesGunTurret.PLAYER_KILLS_WITH_GUN_TURRET);
                    //got player kill = increment player stat
                }

            }

            this.discard();
        }

    }
}
