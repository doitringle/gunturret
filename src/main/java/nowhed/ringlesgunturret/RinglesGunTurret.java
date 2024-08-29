package nowhed.ringlesgunturret;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import nowhed.ringlesgunturret.block.ModBlocks;
import nowhed.ringlesgunturret.block.entity.ModBlockEntities;
import nowhed.ringlesgunturret.damage_type.ModDamageTypes;
import nowhed.ringlesgunturret.entity.ModEntities;
import nowhed.ringlesgunturret.gui.ModScreenHandlers;
import nowhed.ringlesgunturret.item.ModItemGroups;
import nowhed.ringlesgunturret.item.ModItems;
import nowhed.ringlesgunturret.sound.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RinglesGunTurret implements ModInitializer {
	public static final String MOD_ID = "ringlesgunturret";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().isModLoaded("holy-reminder")) {
			RinglesGunTurret.LOGGER.info("Holy Reminder is installed! Thanks for using my friends' mod!!");
		}
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModSounds.registerSounds();
		ModScreenHandlers.registerScreenHandlers();
		ModDamageTypes.registerModDamageTypes();
		ModEntities.registerModEntities();
		LOGGER.info("oeugh...");

	}
}

