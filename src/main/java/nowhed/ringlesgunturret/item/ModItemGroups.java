package nowhed.ringlesgunturret.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;

import net.minecraft.registry.Registry;
import nowhed.ringlesgunturret.block.ModBlocks;

public class ModItemGroups {

    public static final ItemGroup RINGLES_GUN_TURRET = Registry.register(Registries.ITEM_GROUP,
            new Identifier(RinglesGunTurret.MOD_ID, "ringlesgunturret"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.ringlesgunturret"))
                    .icon(() -> new ItemStack(ModItems.GUNBARREL)).entries((displayContext, entries) -> {
                        entries.add(ModItems.GUNBARREL);
                        entries.add(ModBlocks.GUN_TURRET);
                    }).build());

    public static void registerItemGroups() {
        RinglesGunTurret.LOGGER.info("Registering item groups for " + RinglesGunTurret.MOD_ID);
    }

}
