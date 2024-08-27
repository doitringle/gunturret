package nowhed.ringlesgunturret;

import net.fabricmc.api.ModInitializer;

import nowhed.ringlesgunturret.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RinglesGunTurret implements ModInitializer {
	public static final String MOD_ID = "ringlesgunturret";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();

		LOGGER.info("oeugh...");

	}
}