package nowhed.ringlesgunturret.util;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

public class ModLootTableModifiers {
    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {


        });
    }

}
