package nowhed.ringlesgunturret.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.PlayerEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityInterface {

    @Unique
    private String targetSelection = "hostiles";

    @Override
    public String access() {
        return targetSelection;
    }

    @Inject(at = @At("TAIL"),method = "<init>")
    private void init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        this.targetSelection = "hostiles";
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void injectWrite(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString("gunturret.targetSelection", this.targetSelection);
        System.out.println("Saving TARGET SELECTION " + ((PlayerEntityInterface)this).access());
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void injectRead(NbtCompound nbt, CallbackInfo ci) {
        this.targetSelection = nbt.getString("gunturret.targetSelection");
        System.out.println("Loading TARGET SELECTION" + ((PlayerEntityInterface)this).access());
    }

}
