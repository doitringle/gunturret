package nowhed.ringlesgunturret.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;
import nowhed.ringlesgunturret.block.entity.ModBlockEntities;
import nowhed.ringlesgunturret.sound.ModSounds;
import org.jetbrains.annotations.Nullable;

public class GunTurretBlock extends BlockWithEntity {

    private static final VoxelShape SHAPE = Block.createCuboidShape(-8,0,-8,24,8,24);

    private PlayerEntity owner;

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer != null && placer.getType() == EntityType.PLAYER) {
            owner = (PlayerEntity) placer;
        }
    }

    public GunTurretBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState State) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient) {

            if(owner != null && !player.getUuid().equals(owner.getUuid())){
                player.sendMessage(Text.translatable("block.ringlesgunturret.no_access_message"));
                return ActionResult.FAIL;
                // anyone can access a null-owner gun turret.
                // not sure how that would be created but whatever
            }

            world.playSound(null, player.getBlockPos(), ModSounds.OPEN, SoundCategory.BLOCKS, 0.5f, 1f);
            // This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity casted to
            // a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                // With this call the server will request the client to open the appropriate Screenhandler
                player.openHandledScreen(screenHandlerFactory);
            }
        }        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GunTurretBlockEntity(pos, state, owner);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.getBlock().equals(newState.getBlock()) ) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GunTurretBlockEntity GTblockEntity) {
                ItemScatterer.spawn(world, pos, GTblockEntity.getItems());
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state,world,pos,newState,moved);
        }
    }



    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.GUN_TURRET_BLOCK_ENTITY,
                ((world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1, blockEntity)));
    }
}
