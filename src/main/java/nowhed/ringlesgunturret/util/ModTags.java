package nowhed.ringlesgunturret.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;

public class ModTags {
    public static class Blocks {


        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(RinglesGunTurret.MOD_ID, name));
        }
    }
    public static class Items {
        public static final TagKey<Item> VALID_TURRET_PROJECTILE =
                createTag("valid_turret_projectile");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(RinglesGunTurret.MOD_ID, name));
        }
    }


}
