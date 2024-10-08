package nowhed.ringlesgunturret.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;

public class ModTags {
    public static final TagKey<Item> VALID_TURRET_PROJECTILE = TagKey.of(RegistryKeys.ITEM,
            new Identifier(RinglesGunTurret.MOD_ID,"valid_turret_projectile"));

    public static final TagKey<Item> WEAK_AMMO = TagKey.of(RegistryKeys.ITEM,
            new Identifier(RinglesGunTurret.MOD_ID,"weak_ammo"));

    public static final TagKey<Item> MEDIUM_AMMO = TagKey.of(RegistryKeys.ITEM,
            new Identifier(RinglesGunTurret.MOD_ID,"medium_ammo"));

    public static final TagKey<Item> STRONG_AMMO = TagKey.of(RegistryKeys.ITEM,
            new Identifier(RinglesGunTurret.MOD_ID,"strong_ammo"));

}
