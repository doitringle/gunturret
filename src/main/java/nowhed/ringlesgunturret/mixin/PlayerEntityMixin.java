package nowhed.ringlesgunturret.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {


    // nowhed/ringlesgunturret/block/entity/GunTurretBlockEntity.java:250

    @Unique
    @Nullable
    private ArrayList<String> playerSettings;
    private NbtCompound psData;

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void injectWrite(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("ringlesgunturret.playersettings", psData);
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void injectRead(NbtCompound nbt, CallbackInfo ci) {
        psData = nbt.getCompound("ringlesgunturret.playersettings");
    }

}
