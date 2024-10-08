package nowhed.ringlesgunturret.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;

import nowhed.ringlesgunturret.block.ModBlocks;

public class ModItemGroups {

    public static final ItemGroup RINGLES_GUN_TURRET = Registry.register(Registries.ITEM_GROUP,
            new Identifier(RinglesGunTurret.MOD_ID, "ringlesgunturret"),
            FabricItemGroup.builder().displayName(Text.translatable("item_group.ringlesgunturret"))
                    .icon(() -> new ItemStack(ModBlocks.GUN_TURRET.asItem())).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.GUN_TURRET);
                        entries.add(ModItems.REINFORCED_ARROW);
                    }).build());

    public static void registerItemGroups() {
        //RinglesGunTurret.LOGGER.info("Registering item groups for " + RinglesGunTurret.MOD_ID);
    }

}
