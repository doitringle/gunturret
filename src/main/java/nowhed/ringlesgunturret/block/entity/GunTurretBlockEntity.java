package nowhed.ringlesgunturret.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.entity.custom.BulletProjectileEntity;
import nowhed.ringlesgunturret.gui.GunTurretScreenHandler;
import nowhed.ringlesgunturret.sound.ModSounds;
import nowhed.ringlesgunturret.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GunTurretBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, BlockEntityTicker {
    public static final int INVENTORY_SIZE = 4;
    public static final double BULLET_SPEED = 1.5;
    public static int range = 12;
    private boolean canPlaySound = false;
    public Box rangeToSearch =  new Box(this.getPos().getX() - range, this.getPos().getY()-0.5,this.getPos().getZ() - range,
                                this.getPos().getX() + range, this.getPos().getY()+1.5,this.getPos().getZ() + range);
    //public static float rotationTarget = 60;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4,ItemStack.EMPTY);

    private PlayerEntity owner;
    private float rotation;
    private int shootTimer = 60;
    private int cooldown = 2;

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public float getRotation() {
        return this.rotation;
    }

    public PlayerEntity getOwner() {
        return this.owner;
    }

    public void addRotation(float value) {
        this.rotation += value;
    }

    public GunTurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUN_TURRET_BLOCK_ENTITY,pos,state);
        this.rotation = 0;
        this.owner = null;
    }

    public GunTurretBlockEntity(BlockPos pos, BlockState state, PlayerEntity playerEntity) {
        this(pos, state);
        this.owner = playerEntity;
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
    }


    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.gun_turret");
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

        /*if (world.isClient()) {return;}
        this one line of code destroyed my entire afternoon
        */


        GunTurretBlockEntity thisEntity = (GunTurretBlockEntity) blockEntity;

        List<LivingEntity> livingEntities = world.getEntitiesByClass(LivingEntity.class,rangeToSearch, e -> e.isAlive());

        if (livingEntities.isEmpty()) {
            return;
        }
        double lowest = 999;
        LivingEntity chosen = null;
        for(LivingEntity entity : livingEntities) {

            double distance = Math.sqrt(
                    Math.pow((entity.getX() - pos.getX()),2) +
                    Math.pow((entity.getZ() - pos.getZ()),2)
            );
            if (distance < lowest) {
                BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(entity.getPos().add(0,1,0), this.getPos().toCenterPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
                if (blockHitResult.getType() != HitResult.Type.BLOCK && isValidTarget(entity)) {
                    lowest = distance;
                    chosen = entity;
                }

            }



        }


        if(chosen == null) {

            shootTimer = 25;
            canPlaySound = true;
            return;

        } else {

            if (shootTimer > 0) {
                shootTimer--;
            }

            if(canPlaySound) {
                world.playSound(null, pos, ModSounds.TURRET_ROTATES, SoundCategory.BLOCKS, 0.9f, 1f);
                canPlaySound = false;
            }

        }

        //RinglesGunTurret.LOGGER.info("" + inventory.get(0).isOf(Items.ARROW));
        double z = (chosen.getPos().getZ() - 0.5) - pos.getZ();
        double x = (chosen.getPos().getX() - 0.5) - pos.getX();
        float angle = (float) (Math.atan2(z,x) * (-180.0 / Math.PI) - 90);
        float lerp = (float) ((((((angle - thisEntity.getRotation()) % 360) + 540) % 360) - 180) * 0.2);


        thisEntity.addRotation(lerp);
        //run rotation calculation on both server & client so that the client can render the top
        //and the server can detect when its time to shoot

        if (world.isClient()) {return;}


        boolean hasArrows = false;
        if(shootTimer <= 0 && Math.abs(lerp) < 3) {
            // wait until locked on target, and there has been at least 60 ticks since the target changed
            for (ItemStack item : inventory) {
                if (isValidProjectile(item)) {
                    hasArrows = true;
                }
            }
            if (hasArrows && cooldown <= 0) {
                //firing cooldown [in-between bullets]
                cooldown = 10;
                world.playSound(null, pos, ModSounds.TURRET_SHOOTS, SoundCategory.BLOCKS, 0.2f, 1f);
                for (ItemStack item : inventory) {
                    if (isValidProjectile(item)) {
                        item.increment(-1);
                        break;
                    }
                }


                // FIRE PROJECTILE
                ProjectileEntity projectileEntity = getProjectileEntity(world);

                world.spawnEntity(projectileEntity);
            } else {
                cooldown--;
            }
        }

    } //

    private ProjectileEntity getProjectileEntity(World world) {
        ProjectileEntity projectileEntity = new BulletProjectileEntity(world);


        float rotationR = (float) -((( rotation + 92) % 360) * (Math.PI / 180.0));
        float xR = (float) (BULLET_SPEED * Math.cos(rotationR));
        float zR = (float) (BULLET_SPEED * Math.sin(rotationR));

        projectileEntity.setPos(
                this.getPos().getX() + 0.5 + xR * 0.9,
                this.getPos().getY() + 1.2,
                this.getPos().getZ() + 0.5 + zR * 0.9
        );

        projectileEntity.setOwner(null);

        projectileEntity.setYaw(-rotationR);

        projectileEntity.setVelocity(xR,0.0f,zR);
        return projectileEntity;
    }

    private boolean isValidProjectile(ItemStack item) {
        if (FabricLoader.getInstance().isModLoaded("hwg")) {
            return item.isIn(ModTags.Items.VALID_TURRET_PROJECTILE);
        }
        return item.isIn(ItemTags.ARROWS);
    }

    private boolean isValidTarget(LivingEntity entity) {

        // okay SO MY IDEA FOR THIS WAS
        // store a variable inside every player (probably with mixins?)
        // that the server can access
        // which stores settings for what the gun turret should do
        // and target
        // resources/assets/ringlesgunturret/textures/gui/GUI_MOCKUP.png
        // but i couldnt figure out how 1. to store the variable
        // and 2. to modify the variable with an item (nowhed/ringlesgunturret/item/TurretConfig.java)
        // which would open a gui when right clicked (mockup file above)
        // and when i couldnt figure THAT out either i got discouraged so whatever
        // it will only target hostile mobs, never players. i wanted to let players set a list of "enemies" or a list of "friendlies"
        // where the turret will target and will not target respectively
        // but maybe someone can help me or take over the project. i dunno. im better at modeling than programming

        if(entity.isInvulnerable() || entity.isInvisible() || entity.getType().getSpawnGroup().isPeaceful()) {
            return false;
        }

        /*if(entity.isPlayer()) {
            if(entity.getName()) {
                return false;
            }
        }*/

        return true;

        /*if (entity.getGroup() == EntityGroup.UNDEAD || entity.getGroup() == EntityGroup.ILLAGER) {
            return true;
        }

        return false;*/
    }
}