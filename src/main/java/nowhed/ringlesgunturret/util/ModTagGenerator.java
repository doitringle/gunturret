package nowhed.ringlesgunturret.util;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ModTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    private static final TagKey<Item> VALID_TURRET_PROJECTILE = TagKey.of(RegistryKeys.ITEM,
            new Identifier(RinglesGunTurret.MOD_ID,"valid_turret_projectile"));
    private static final TagKey<Item> WEAK_AMMO = TagKey.of(RegistryKeys.ITEM,
            new Identifier(RinglesGunTurret.MOD_ID,"weak_ammo"));
    private static final TagKey<Item> MEDIUM_AMMO = TagKey.of(RegistryKeys.ITEM,
            new Identifier(RinglesGunTurret.MOD_ID,"medium_ammo"));
    private static final TagKey<Item> STRONG_AMMO = TagKey.of(RegistryKeys.ITEM,
            new Identifier(RinglesGunTurret.MOD_ID,"strong_ammo"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {

        // https://github.com/cybercat-mods/HWG/blob/1.21.x/common/src/main/java/mod/azure/hwg/util/registry/HWGItems.java

        getOrCreateTagBuilder(WEAK_AMMO)
                .add(Items.ARROW)
                .add(Items.FIRE_CHARGE);
        getOrCreateTagBuilder(MEDIUM_AMMO)
                .add(Items.SPECTRAL_ARROW)
                .add(Items.TIPPED_ARROW)
                .add(Items.FIREWORK_ROCKET)
                .addOptional(new Identifier("hwg","bullets"))
                .addOptional(new Identifier("hwg","shotgun_shell"))
                .addOptional(new Identifier("hwg","silver_bullet"));
        getOrCreateTagBuilder(STRONG_AMMO)
                .add(ModItems.REINFORCED_ARROW)
                .addOptional(new Identifier("hwg","sniper_round"))
                .addOptional(new Identifier("hwg","rocket"));


        getOrCreateTagBuilder(VALID_TURRET_PROJECTILE)
                .addTag(WEAK_AMMO)
                .addTag(MEDIUM_AMMO)
                .addTag(STRONG_AMMO);

    }
}
