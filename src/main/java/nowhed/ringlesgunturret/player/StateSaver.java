package nowhed.ringlesgunturret.player;


import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.RinglesGunTurret;
import java.util.HashMap;
import java.util.UUID;

public class StateSaver extends PersistentState {
    //https://fabricmc.net/wiki/tutorial:persistent_states


    public HashMap<UUID, PlayerData> players = new HashMap<>();

    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaver serverState = getServerState(player.getWorld().getServer());

        // Either get the player by the uuid, or we don't have data for them yet, make a new player state
        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());

        return playerState;
    }

    public static PlayerData getPlayerState(LivingEntity player, World world) {
        StateSaver serverState = getServerState(world.getServer());
        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
        return playerState;
    }

    public static StateSaver createFromNbt(NbtCompound tag) {
        StateSaver state = new StateSaver();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            playerData.targetSelection = playersNbt.getCompound(key).getString("targetSelection");
            playerData.playerList = playersNbt.getCompound(key).getString("playerList");
            playerData.blacklist = playersNbt.getCompound(key).getBoolean("blacklist");


            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public static StateSaver getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        StateSaver state = persistentStateManager.getOrCreate(
                StateSaver::createFromNbt,
                StateSaver::new,
                RinglesGunTurret.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
       NbtCompound playersNbt = new NbtCompound();

       players.forEach((uuid, playerData) -> {
           NbtCompound playerNbt = new NbtCompound();
           playerNbt.putString("targetSelection",playerData.targetSelection);
           playerNbt.putString("playerList",playerData.playerList);
           playerNbt.putBoolean("blacklist",playerData.blacklist);

           System.out.println(playerData.targetSelection);

           playersNbt.put(uuid.toString(), playerNbt);
       });
       nbt.put("players",playersNbt);
       return nbt;
    }
}
