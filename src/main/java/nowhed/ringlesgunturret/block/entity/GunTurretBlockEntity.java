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
import nowhed.ringlesgunturret.sound.ModSounds;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GunTurretBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, BlockEntityTicker {
    public static final int INVENTORY_SIZE = 4;

    public static int range = 12;
    public static boolean canPlaySound = false;
    public Box rangeToSearch =  new Box(this.getPos().getX() - range, this.getPos().getY()-0.5,this.getPos().getZ() - range,
                                this.getPos().getX() + range, this.getPos().getY()+1.5,this.getPos().getZ() + range);
    //public static float rotationTarget = 60;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4,ItemStack.EMPTY);

    private static float rotation = 32;


    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public float getRotation() {
        return rotation;
    }

    public GunTurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUN_TURRET_BLOCK_ENTITY,pos,state);

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




    /*@Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GunTurretBlockEntity) {
                ItemScatterer.spawn(world, pos, inventory);
                world.updateComparators(pos, this);
            }
        }
    }*/

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
        return null;
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (world.isClient()) {
            return;
        }

        List<LivingEntity> livingEntities = world.getEntitiesByClass(LivingEntity.class,rangeToSearch, e -> e.isAlive());
        //RinglesGunTurret.LOGGER.info(livingEntities.toString());
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


        if (chosen == null ) {
            return;
        }



        double z = (chosen.getPos().getZ() - 0.5) - pos.getZ();
        double x = (chosen.getPos().getX() - 0.5) - pos.getX();
        float angle = (float) (Math.atan2(z,x) * (-180.0 / Math.PI) - 90);
        float lerp = (float)((((((angle - rotation) % 360) + 540) % 360) - 180) * 0.1);
        rotation += lerp; // lerp
        //RinglesGunTurret.LOGGER.info("" + lerp);
        if(Math.abs(lerp) > 3) {
            if(canPlaySound) {
                world.playSound(null, pos, ModSounds.TURRET_ROTATES, SoundCategory.BLOCKS, 1f, 1f);
                canPlaySound = false;
            }
        } else {
            canPlaySound = true;
        }

    }
}