package nowhed.ringlesgunturret.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.gui.GunTurretScreenHandler;
import nowhed.ringlesgunturret.sound.ModSounds;
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
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
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


        double z = (chosen.getPos().getZ() - 0.5) - pos.getZ();
        double x = (chosen.getPos().getX() - 0.5) - pos.getX();
        float angle = (float) (Math.atan2(z,x) * (-180.0 / Math.PI) - 90);
        float lerp = (float) ((((((angle - thisEntity.getRotation()) % 360) + 540) % 360) - 180) * 0.1);


        thisEntity.addRotation(lerp);



    } //
}