package nowhed.ringlesgunturret.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.block.custom.GunTurretBlock;

public class ModBlocks {
    public static final Block GUN_TURRET = registerBlock("gun_turret",
            new GunTurretBlock(FabricBlockSettings.create()
                    .mapColor(MapColor.ORANGE)
                    .strength(3.0F, 6.0F)
                    .pistonBehavior(PistonBehavior.BLOCK)
                    .sounds(BlockSoundGroup.NETHERITE)
                    .nonOpaque()
                    .noBlockBreakParticles() // maybe remove this ill see how it looks
            ));

    public static final Block GUN_TURRET_TOP = registerBlock("gun_turret_top",
            new Block(FabricBlockSettings.copyOf(GUN_TURRET)));

    private static Block registerBlock(String name,Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(RinglesGunTurret.MOD_ID,name),block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(RinglesGunTurret.MOD_ID,name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        RinglesGunTurret.LOGGER.info("Registering blocks for " + RinglesGunTurret.MOD_ID);
    }
}
