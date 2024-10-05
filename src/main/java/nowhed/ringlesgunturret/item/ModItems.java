package nowhed.ringlesgunturret.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import nowhed.ringlesgunturret.RinglesGunTurret;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ModItems {
/*    public static final Item GUNBARREL = registerItem("gunbarrel",new Item(new FabricItemSettings()));
    public static final Item TURRETSETTINGS = registerItem("turretsettings",
            new TurretConfig(new FabricItemSettings()));
    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(GUNBARREL);
        entries.add(TURRETSETTINGS);
    }*/

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(RinglesGunTurret.MOD_ID, name), item);
    }


    public static void registerModItems() {
        //RinglesGunTurret.LOGGER.info("Registering items for " + RinglesGunTurret.MOD_ID);

        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }

}
