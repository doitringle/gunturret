package nowhed.ringlesgunturret.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ModUtils {

    public static String getOfflinePlayerName(MinecraftServer server, @Nullable UUID uuid) {

        UserCache userCache;
        String name = null;
        Optional<GameProfile> gameProfile;

        if(server != null && uuid != null) {

            userCache = server.getUserCache();

            if (userCache != null) {

                gameProfile = userCache.getByUuid(uuid);

                if (gameProfile.isPresent()) {

                    name = gameProfile.get().getName();

                }
            }
        }

        return name;

    }
}
