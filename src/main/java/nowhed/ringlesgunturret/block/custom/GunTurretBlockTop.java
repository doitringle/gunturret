package nowhed.ringlesgunturret.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;

public class GunTurretBlockTop extends Block {
    public static final IntProperty ROTATION = IntProperty.of("rotation",0,3);
    public GunTurretBlockTop(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ROTATION,0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

}
