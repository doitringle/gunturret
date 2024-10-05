package nowhed.ringlesgunturret;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import nowhed.ringlesgunturret.block.ModBlocks;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;
import nowhed.ringlesgunturret.block.entity.ModBlockEntities;
import nowhed.ringlesgunturret.damage_type.ModDamageTypes;
import nowhed.ringlesgunturret.entity.ModEntities;
import nowhed.ringlesgunturret.gui.ModScreenHandlers;
import nowhed.ringlesgunturret.item.ModItemGroups;
import nowhed.ringlesgunturret.item.ModItems;
import nowhed.ringlesgunturret.networking.ModMessages;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;
import nowhed.ringlesgunturret.sound.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.nimbus.State;

public class RinglesGunTurret implements ModInitializer {
	public static final String MOD_ID = "ringlesgunturret";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier KILLS_WITH_GUN_TURRET = new Identifier(MOD_ID,"kills_with_gun_turret");

	public static boolean HWG_INSTALLED;

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().isModLoaded("holy-reminder")) {
			RinglesGunTurret.LOGGER.info("Holy Reminder is installed! Remember: The bell tolls for thee.");
		}
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModSounds.registerSounds();
		ModScreenHandlers.registerScreenHandlers();
		ModDamageTypes.registerModDamageTypes();
		ModEntities.registerModEntities();
		ModMessages.registerC2SPackets();
		ModMessages.registerS2CPackets();
		registerEvents();

		Registry.register(Registries.CUSTOM_STAT, "kills_with_gun_turret", KILLS_WITH_GUN_TURRET);
		Stats.CUSTOM.getOrCreateStat(KILLS_WITH_GUN_TURRET, StatFormatter.DEFAULT);

		HWG_INSTALLED = FabricLoader.getInstance().isModLoaded("hwg");

		LOGGER.info("oeugh...");
	}




	public static void registerEvents() {

		/*UseItemCallback.EVENT.register(((player, world, hand) -> {
			if (!world.isClient() && player.getStackInHand(hand).isOf(ModItems.TURRETSETTINGS)) {
				PlayerData playerState = StateSaver.getPlayerState(player);
				MinecraftServer server = world.getServer();
				server.execute(() -> {
					player.sendMessage(Text.literal(playerState.targetSelection));
				});
			}
			if (!world.isClient() && player.getStackInHand(hand).isOf(ModItems.GUNBARREL)) {

				StateSaver serverState = StateSaver.getServerState(world.getServer());
				PlayerData playerState = StateSaver.getPlayerState(player);
				playerState.targetSelection = "TEST" + Math.random();
				MinecraftServer server = world.getServer();

				PacketByteBuf data = PacketByteBufs.create();
				data.writeString(playerState.targetSelection);
				ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
				server.execute(() -> {
					ServerPlayNetworking.send(playerEntity, TARGET_SELECTION, data);
					player.sendMessage(Text.literal(playerState.targetSelection));
				});

			}
			return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
		}));*/

		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(((blockEntity, world) -> {
			if(blockEntity instanceof GunTurretBlockEntity) {
				GunTurretBlockEntity gunTurretBlockEntity = (GunTurretBlockEntity) blockEntity;
                (gunTurretBlockEntity).requestTargetSettings((gunTurretBlockEntity.getOwner()));
			}
		}));

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

