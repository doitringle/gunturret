package nowhed.ringlesgunturret.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract String getEntityName();

    @Unique
    private String targetSelection;

    @Inject(at = @At("TAIL"),method = "<init>")
    private void init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        this.targetSelection = "hostiles" + Math.random();
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void injectWrite(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString("mymod.targetSelection", this.targetSelection);
        System.out.println("Saving TARGETSELECTION in player " + getEntityName() + " as " + this.targetSelection);
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void injectRead(NbtCompound nbt, CallbackInfo ci) {
        this.targetSelection = nbt.getString("mymod.targetSelection");
        System.out.println("Loading TARGETSELECTION in player " + getEntityName() + " as " + this.targetSelection);
    }

}
