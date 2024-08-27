package nowhed.ringlesgunturret;

import net.fabricmc.api.ModInitializer;

import nowhed.ringlesgunturret.block.ModBlocks;
import nowhed.ringlesgunturret.block.entity.ModBlockEntities;
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
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModSounds.registerSounds();
		LOGGER.info("oeugh...");

	}
}

