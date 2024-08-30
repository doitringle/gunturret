package nowhed.ringlesgunturret;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
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
		registerEvents();


		LOGGER.info("oeugh...");

	}

	public static void registerEvents() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (hitResult.getType() != BlockHitResult.Type.BLOCK) {
				return ActionResult.PASS;
			}

			BlockPos pos = hitResult.getBlockPos();
			BlockState state = world.getBlockState(pos);
			Block clickedBlock = state.getBlock();


			if (clickedBlock == ModBlocks.GUN_TURRET) {
				return ActionResult.PASS;
			}

			// Otherwise, check within a 1-block radius for placement restrictions
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						BlockPos checkPos = pos.add(x, y, z);
						if (world.getBlockState(checkPos).getBlock() == ModBlocks.GUN_TURRET) {
							return ActionResult.FAIL;
						}
					}
				}
			}
			return ActionResult.PASS;
		});
	}
}

