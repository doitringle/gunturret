package nowhed.ringlesgunturret.block.entity;

import com.mojang.datafixers.types.templates.Tag;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.damage_type.ModDamageTypes;
import nowhed.ringlesgunturret.entity.custom.BulletProjectileEntity;
import nowhed.ringlesgunturret.gui.GunTurretScreenHandler;
import nowhed.ringlesgunturret.sound.ModSounds;
import nowhed.ringlesgunturret.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GunTurretBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, BlockEntityTicker {
    public static final int INVENTORY_SIZE = 4;

    public static int range = 12;
    private boolean canPlaySound = false;
    public Box rangeToSearch =  new Box(this.getPos().getX() - range, this.getPos().getY()-0.5,this.getPos().getZ() - range,
                                this.getPos().getX() + range, this.getPos().getY()+1.5,this.getPos().getZ() + range);
    //public static float rotationTarget = 60;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4,ItemStack.EMPTY);

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


    public void addRotation(float value) {
        this.rotation += value;
    }

    public GunTurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUN_TURRET_BLOCK_ENTITY,pos,state);
        this.rotation = 0;
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
        return new GunTurretScreenHandler(syncId, playerInventory,this);
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
        LivingEntity chosen = livingEntities.get(0);
        for(LivingEntity entity : livingEntities) {

            double distance = Math.sqrt(
                    Math.pow((entity.getX() - pos.getX()),2) +
                    Math.pow((entity.getZ() - pos.getZ()),2)
            );
            if (distance < lowest) {
                lowest = distance;
                chosen = entity;
            }
        }


        if(chosen == null) {

            shootTimer = 60;
            canPlaySound = true;
            return;

        } else {

            if (shootTimer > 0) {
                shootTimer--;
            }

            if(canPlaySound) {
                world.playSound(null, pos, ModSounds.TURRET_ROTATES, SoundCategory.BLOCKS, 0.8f, 1f);
                canPlaySound = false;
            }

        }

        //RinglesGunTurret.LOGGER.info("" + inventory.get(0).isOf(Items.ARROW));
        double z = (chosen.getPos().getZ() - 0.5) - pos.getZ();
        double x = (chosen.getPos().getX() - 0.5) - pos.getX();
        float angle = (float) (Math.atan2(z,x) * (-180.0 / Math.PI) - 90);
        float lerp = (float) ((((((angle - thisEntity.getRotation()) % 360) + 540) % 360) - 180) * 0.1);


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
                cooldown = 3;
                world.playSound(null, pos, ModSounds.TURRET_SHOOTS, SoundCategory.BLOCKS, 0.2f, 1f);
                for (ItemStack item : inventory) {
                    if (isValidProjectile(item)) {
                        item.increment(-1);
                        break;
                    }
                }


                // FIRE PROJECTILE
                ProjectileEntity projectileEntity = new BulletProjectileEntity(world);
                projectileEntity.setPos(this.getPos().getX()+0.5,this.getPos().getY()+1,this.getPos().getZ()+0.5);
                // IN RADIANS:
                float rotationR = (float) -((( rotation + 90) % 360) * (Math.PI / 180.0));
                float xR = (float) (Math.cos(rotationR));
                float yR = (float) (Math.sin(rotationR));
                // roll is always 0

                projectileEntity.setOwner(null);

                projectileEntity.setYaw(rotationR);

                projectileEntity.setVelocity(xR,0.0f,yR);

                world.spawnEntity(projectileEntity);
            } else {
                cooldown--;
            }
        }

    } //

    private boolean isValidProjectile(ItemStack item) {
        if (FabricLoader.getInstance().isModLoaded("hwg")) {
            return item.isIn(ModTags.Items.VALID_TURRET_PROJECTILE);
        }
        return item.isIn(ItemTags.ARROWS);
    }
}