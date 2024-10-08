package nowhed.ringlesgunturret.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.entity.custom.BulletProjectileEntity;
import nowhed.ringlesgunturret.gui.GunTurretScreenHandler;
import nowhed.ringlesgunturret.item.ModTags;
import nowhed.ringlesgunturret.networking.ModMessages;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;
import nowhed.ringlesgunturret.sound.ModSounds;
import nowhed.ringlesgunturret.util.ModTagGenerator;
import nowhed.ringlesgunturret.util.ModUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GunTurretBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, BlockEntityTicker {

    public static final int INVENTORY_SIZE = 4;
    public static final double BULLET_SPEED = 1.5;
    public static int range = 12;
    private boolean canPlaySound = false;
    public Box rangeToSearch =  new Box(this.getPos().getX() - range, this.getPos().getY()-0.5,this.getPos().getZ() - range,
                                this.getPos().getX() + range, this.getPos().getY()+1.5,this.getPos().getZ() + range);
    private String targetSelection = "hostiles";
    private String playerList = "";
    private boolean blacklist = true;

    //public static float rotationTarget = 60;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4,ItemStack.EMPTY);

    @Nullable private PlayerEntity owner;
    @Nullable private UUID ownerUuid;
    private float rotation;

    // used for visuals, only matters on client, though they might be set or changed on server
    private float clientRotation;
    private int barrelRotation;

    private int shootTimer = 60;
    private int cooldown = 2;
    @Nullable private LivingEntity lockOnEntity;


    public static boolean infiniteArrows = false;
    public static double predictionMultiplier = 1.5;

    private Vec3d muzzlePos;
    private Vec3d playerVelocity = new Vec3d(0,0,0);

    public GunTurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUN_TURRET_BLOCK_ENTITY,pos,state);
        this.rotation = 0;
        this.clientRotation = 0;
        this.barrelRotation = 0;
        this.owner = null;
        this.ownerUuid = null;
        this.muzzlePos = new Vec3d(this.getPos().toCenterPos().getX(),
                this.getPos().getY() + 1.2,
                this.getPos().toCenterPos().getZ());
        if(world != null) this.cooldown = (int) (world.random.nextFloat() * 5);
    }

    public GunTurretBlockEntity(BlockPos pos, BlockState state, PlayerEntity playerEntity) {
        this(pos, state);
        this.owner = playerEntity;
        this.ownerUuid = playerEntity.getUuid();
        this.requestTargetSettings(playerEntity);

    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public float getRotation() {
        return this.rotation;
    }

    public @Nullable PlayerEntity getOwner() {
        if (this.owner == null) {
            if (this.ownerUuid != null && this.getWorld() != null) {
                return this.getWorld().getPlayerByUuid(this.ownerUuid);
            }
        } else {
            return owner;
        }
        return null;
    }

    public void setPlayerVelocity(Vec3d playerV) {
        this.playerVelocity = playerV;
    }

    public void setOwner(PlayerEntity playerEntity) {
        this.owner = playerEntity;
        this.ownerUuid = playerEntity.getUuid();
        this.lockOnEntity = null;
        this.markDirty();
    }

    public void addRotation(float value) {
        this.rotation += value;

    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, this.inventory);

        this.owner = null;
        if (nbt.containsUuid("owner_uuid")) {
            this.ownerUuid = nbt.getUuid("owner_uuid");
            if(this.world != null) this.owner = this.world.getPlayerByUuid(this.ownerUuid);
        } else {
            this.ownerUuid = null;
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        if(this.getOwner() != null)
            nbt.putUuid("owner_uuid", this.getOwner().getUuid());
        else if(this.ownerUuid != null) {
            nbt.putUuid("owner_uuid", this.ownerUuid);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Environment(EnvType.CLIENT)
    public void setClientRotation(Float rot) {
        this.clientRotation = rot;
    }

    @Environment(EnvType.CLIENT)
    public float getClientRotation() {
        return this.clientRotation;
    }

    public int getBarrelRotation() {
        return this.barrelRotation % 4;
    }

    @Environment(EnvType.CLIENT)
    public void setBarrelRotation(int br) {
        this.barrelRotation = (br % 4);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.ringlesgunturret.gun_turret");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GunTurretScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {

        if (world.isClient()) return;

        if(world.getServer().getTicks() % 60 == 0) {
            this.requestTargetSettings(this.getOwner());
            //probably a bad idea
        }

        GunTurretBlockEntity thisEntity = (GunTurretBlockEntity) blockEntity;

        LivingEntity chosen = null;

        //"locking on" skips ray casts and selection... also makes the turrets more efficient

        if(!isValidTarget(lockOnEntity) || !lockOnEntity.getPos().isInRange(this.pos.toCenterPos(),range)) {

            List<LivingEntity> livingEntities = world.getEntitiesByClass(LivingEntity.class, rangeToSearch, LivingEntity::isAlive);

            if (livingEntities.isEmpty() || this.targetSelection.equals("disable")) {
                return;
            }
            double lowest = 999;

            for (LivingEntity entity : livingEntities) {
                //Long startTime = System.nanoTime();
                boolean valid = isValidTarget(entity);
                //Long estimatedTime = System.nanoTime();
                //System.out.println("Time to check: " + (estimatedTime - startTime));
                if (!valid) {
                    continue;
                }

                double distance = Math.sqrt(
                        Math.pow((entity.getX() - pos.getX()), 2) +
                                Math.pow((entity.getZ() - pos.getZ()), 2)
                );
                if (distance < lowest) {
                    // now that we've selected a closest entity, fire a raycast to see if it is behind any blocks

                    Vec3d entityRayHit = new Vec3d(
                            entity.getPos().getX(),
                            this.muzzlePos.getY(),
                            entity.getPos().getZ()
                    );

                    BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(this.muzzlePos, entityRayHit, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
                    if (blockHitResult.getType() != HitResult.Type.BLOCK) {
                        lowest = distance;
                        chosen = entity;
                    }
                }
            }
            lockOnEntity = chosen;

        } else {
            chosen = lockOnEntity;
        }

        if(chosen == null) {

            shootTimer = 25;
            canPlaySound = true;
            if(barrelRotation % 4 != 0) barrelRotation += 1;
            return;

        } else {

            if (shootTimer > 0) {
                shootTimer--;
            }

            if(canPlaySound) {
                world.playSound(null, pos, ModSounds.TURRET_ROTATES, SoundCategory.BLOCKS, 0.9f, 1f + world.random.nextFloat() * 0.2F);
                canPlaySound = false;
            }

            if(chosen.isPlayer()) {
                // request player's velocity for predictive aiming calculation
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(this.getPos());
                ServerPlayNetworking.send((ServerPlayerEntity) chosen, ModMessages.REQUEST_PLAYER_VELOCITY_ID, buf);
            }

        }

        // predictive aiming calculation


        Vec3d cPos = chosen.getBoundingBox().getCenter();
        Vec3d cVelocity = chosen.getVelocity().multiply(predictionMultiplier,1,predictionMultiplier);

        if(chosen.isPlayer()) {
            cVelocity = this.playerVelocity.multiply(predictionMultiplier,1,predictionMultiplier);
            //this might work?
            System.out.println(cVelocity);
        }

        //Vec3d unitV = new Vec3d(chosen.getHorizontalFacing().getVector());
        //System.out.println(chosen.getHorizontalFacing().getVector() + " :  " + cVelocity);

        // prediction only accurate if predictionMultiplier is 1.0
        Vec3d predictedPosition;

        this.muzzlePos = new Vec3d(this.getPos().toCenterPos().getX(),
                this.getPos().getY() + 1.2,
                this.getPos().toCenterPos().getZ());

        if(Math.abs(cVelocity.getX()) <= 0.03 && Math.abs(cVelocity.getZ()) <= 0.03 ) {
            //skip all the prediction
            predictedPosition = cPos;
        } else {

            //timeToHit = distance / (BULLET_SPEED);
            //a * x^2 + b * x + c == 0

            double distanceX = cPos.getX() - muzzlePos.getX();
            double distanceZ = cPos.getZ() - muzzlePos.getZ();

            double a = sqr(cVelocity.getX()) + sqr(cVelocity.getZ()) - sqr(BULLET_SPEED);
            double b = 2 * (cVelocity.getX() * distanceX + cVelocity.getZ() * distanceZ);
            double c = sqr(distanceX) + sqr(distanceZ);

            double s1;
            double s2;
            double solution;

            if(Math.abs(a) <= 0) {
                if(Math.abs(b) <= 0) {
                        if(Math.abs(c) <= 0) {
                            solution = 0;
                        } else {
                            //System.out.println("No chance!");
                            return;
                    }
                } else {
                    solution = -c / b;
                }
            } else {
                double discriminant = sqr(b) - 4.0 * a * c;
                if (discriminant >= 0) {
                    s1 = (-b + Math.sqrt(discriminant)) / (2.0 * a);
                    s2 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
                    solution = Math.min(s1, s2);
                    if(solution < 0) solution = Math.max(s1,s2);
                } else {
                    //if discriminant < 0, there is no chance to hit the target.

                    return;
                }
            }

            predictedPosition = new Vec3d(
                    cPos.getX() + cVelocity.getX() * solution,
                    cPos.getY(),
                    cPos.getZ() + cVelocity.getZ() * solution
            );

        }

        //https://stackoverflow.com/questions/2248876/2d-game-fire-at-a-moving-target-by-predicting-intersection-of-projectile-and-u

        //rotate and lerp towards the targets' position
        double z = (predictedPosition.getZ()) - muzzlePos.getZ();
        double x = (predictedPosition.getX()) - muzzlePos.getX();
        float angle = (float) (Math.atan2(z,x) * (-180.0 / Math.PI) - 90);
        float lerp = (float) ((((((angle - thisEntity.getRotation()) % 360) + 540) % 360) - 180) * 0.2);


        thisEntity.addRotation(lerp);

        boolean hasArrows = false;

        if(shootTimer <= 0 && Math.abs(lerp) < 3) {
            // wait until locked on target, and there has been at least 60 ticks since the target changed
            for (ItemStack item : inventory) {
                if (isValidProjectile(item)) {
                    hasArrows = true;
                }
            }

            if (hasArrows || infiniteArrows) {

                barrelRotation += 1;

                if(cooldown <= 0) {
                    //firing cooldown [in-between bullets]
                    cooldown = 8 + (int) (world.random.nextFloat() * 5);
                    world.playSound(null, pos, ModSounds.TURRET_SHOOTS, SoundCategory.BLOCKS, 0.2f, 1f + world.random.nextFloat() * 0.2F);

                    ItemStack selected = Items.ARROW.getDefaultStack();

                    for (ItemStack item : inventory) {
                        if (isValidProjectile(item)) {
                            selected = item.copyWithCount(1);
                            item.decrement(1);
                            break;
                        }
                    }

                    // create a projectile to fire
                    ProjectileEntity projectileEntity = getProjectileEntity(world, selected);

                    // FIRE PROJECTILE
                    world.spawnEntity(projectileEntity);

                } else {
                    cooldown--;
                }
            }
        }

    } //

    private BulletProjectileEntity getProjectileEntity(World world, ItemStack item) {
        BulletProjectileEntity projectileEntity = new BulletProjectileEntity(world);

        float rotationR = (float) -((( this.rotation + 90) % 360) * (Math.PI / 180.0));
        float xR = (float) (BULLET_SPEED * Math.cos(rotationR));
        float zR = (float) (BULLET_SPEED * Math.sin(rotationR));

        projectileEntity.setPosition(muzzlePos.getX(),muzzlePos.getY(),muzzlePos.getZ());

        projectileEntity.setVelocity(xR,0.0f,zR);

        projectileEntity.setPlayerOwner(this.getOwner());

        if(item.isIn(ModTags.STRONG_AMMO)) {
            projectileEntity.setDamageValue(5.0f);

        } else if (item.isIn(ModTags.MEDIUM_AMMO)) {
            projectileEntity.setDamageValue(4.0f);

        } else {
            projectileEntity.setDamageValue(3.0f);

        }


        return projectileEntity;
    }

    private boolean isValidProjectile(ItemStack item) {

        return item.isIn(ModTags.VALID_TURRET_PROJECTILE);

        // return item.isIn(ItemTags.ARROWS);
    }

    private void setTargetSettings(String targetSel, String players, Boolean blacklist) {
        this.targetSelection = targetSel;
        this.playerList = players;
        this.blacklist = blacklist;
        this.lockOnEntity = null;
    }

    public void requestTargetSettings(PlayerEntity player) {
        if(!(world == null) && !world.isClient() && player != null && this.getOwner() != null && player.equals(this.getOwner())) {
            PlayerData playerData = StateSaver.getPlayerState(player, world);
            if(playerData != null) setTargetSettings(playerData.targetSelection,
                    playerData.playerList,
                    playerData.blacklist);
        }
    }

    private boolean isValidTarget(LivingEntity entity) {

        if(entity == null || world.getEntityById(entity.getId()) == null || entity.isDead()) return false;

        // check for valid targets based on player settings

        String[] namesList = playerList.split(",");



        if(!entity.canTakeDamage() || entity.isInvisible() ||
                !(entity.getBoundingBox().minY - 0.5 < muzzlePos.getY() && entity.getBoundingBox().maxY + 0.5 > muzzlePos.getY())) {

            // if invulnerable, or invisible
            // or if the turret has no chance to hit the entity's hitbox
            return false;
        }


        // blacklist TRUE = attack ALL players whose names are not in namesList
        // blacklist FALSE = attack ONLY players whose names are in namesList

        if(entity.isPlayer()) {
            if(((PlayerEntity) entity).isCreative() || entity.isSpectator() || getOwner() == null) {
                return false;
            }
            if(namesList.length == 0) {
                return blacklist; // no players typed
            }
            String playerName = entity.getDisplayName().getString().toLowerCase();
            if (blacklist) {
                for(String name : namesList) {
                    if(name.equals(playerName)) return false;
                    //upon the first occurrence of the name, the
                    //player is known to be on the blacklist
                }
                if(playerName.equals(getOwner().getDisplayName().getString().toLowerCase())) return false;
                return true;
            } else {
                for(String name : namesList) {
                    if(name.equals(playerName)) return true;
                    //upon the first occurrence of the name,
                    //the player is known to be a target
                }
                return false;
            }

        }
        if(this.targetSelection.equals("onlyplayers")) return false;
        // this is where the check for players ends, so no need to continue

        if (entity instanceof Monster && !(entity instanceof PiglinEntity && ((PiglinEntity) entity).getTarget() != null)) {
            //attack hostile entities or charging piglins. but not passive piglins.
            return true;
        }

        if(getOwner() != null && entity instanceof WolfEntity
                && ((WolfEntity) entity).getAngryAt() != null
                && ((WolfEntity) entity).getAngryAt().equals(this.ownerUuid)) {
            // wolves attacking the owner will be shot at!
            return true;
        }
        if(this.targetSelection.equals("hostiles")) return false;

        // i dont think there is a way to check if a fox is "tamed" (has trusted Uuids) from outside the FoxEntity class without mixins (dont feel like doing that right now)
        // so sorry tamed foxes are gonna be shot

        if(entity instanceof TameableEntity pet && ((TameableEntity) entity).isTamed()) {
            if(!this.blacklist && pet.getOwner() != null) {
                String ownerName = pet.getOwner().getDisplayName().getString().toLowerCase();
                for(String name : namesList) {
                    if(name.equals(ownerName)) return true;
                    // upon the first occurrence of the name, the
                    // pet's owner is known to be on the whitelist ("enemy list")
                }
            }
            return false;
        }
        if(entity instanceof AnimalEntity && entity.isBaby()) return false;


        return true;
    }

    public double sqr(double number) {
        return number * number;
    }


}