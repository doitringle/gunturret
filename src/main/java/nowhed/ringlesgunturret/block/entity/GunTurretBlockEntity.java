package nowhed.ringlesgunturret.block.entity;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
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
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.entity.custom.BulletProjectileEntity;
import nowhed.ringlesgunturret.gui.GunTurretScreenHandler;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;
import nowhed.ringlesgunturret.sound.ModSounds;
import nowhed.ringlesgunturret.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

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
        this.requestTargetSettings(playerEntity);
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

        if (livingEntities.isEmpty() || this.targetSelection.equals("disable")) {
            return;
        }
        double lowest = 999;
        LivingEntity chosen = null;
        for(LivingEntity entity : livingEntities) {
            if (!isValidTarget(entity)) continue;
            double distance = Math.sqrt(
                    Math.pow((entity.getX() - pos.getX()),2) +
                    Math.pow((entity.getZ() - pos.getZ()),2)
            );
            if (distance < lowest) {
                // now that we've selected a closest entity, fire a raycast to see if it is behind any blocks
                BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(entity.getPos().add(0,1,0), this.getPos().toCenterPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
                if (blockHitResult.getType() != HitResult.Type.BLOCK) {
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

        projectileEntity.setYaw(-rotationR);

        projectileEntity.setVelocity(xR,0.0f,zR);


        projectileEntity.setOwner(this.getOwner());
        return projectileEntity;
    }

    private boolean isValidProjectile(ItemStack item) {
        if (FabricLoader.getInstance().isModLoaded("hwg")) {
            return item.isIn(ModTags.Items.VALID_TURRET_PROJECTILE);
        }
        return item.isIn(ItemTags.ARROWS);
    }

    private void setTargetSettings(String targetSel, String players, Boolean blacklist) {
        this.targetSelection = targetSel;
        this.playerList = players;
        this.blacklist = blacklist;
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

        String[] namesList = playerList.split(",");

        System.out.println(entity.getName() + ":" + entity.getHeight());

        if(entity.isInvulnerable() || entity.getHeight() < 0.6f || (entity.isInvisible())) {
            return false;
        }

        // blacklist TRUE = attack ALL players whose names are not in namesList
        // blacklist FALSe = attack ONLY players whose names are in namesList

        if(entity.isPlayer()) {
            if(((PlayerEntity) entity).isCreative() || entity.isSpectator()) {
                return false;
            }
            if(namesList.length == 0) {
                return blacklist; // no players typed
            }
            String playerName = entity.getName().toString().toLowerCase();
            if (blacklist) {
                for(String name : namesList) {
                    if(name.equals(playerName)) return false;
                    //upon the first occurrence of the name, the
                    //player is known to be on the blacklist
                }
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


        if (entity.getGroup() == EntityGroup.UNDEAD || entity.getGroup() == EntityGroup.ILLAGER) {
            return true;
        }

        return false;
    }
}