package nowhed.ringlesgunturret.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import org.jetbrains.annotations.Nullable;

public class ItemSettingsScreenHandler extends ScreenHandler {
    private static final Identifier TEXTURE = new Identifier(RinglesGunTurret.MOD_ID, "textures/gui/generic2x2.png");
    // REPLACE LATER WITH ACTUAL GUI

    public ItemSettingsScreenHandler(int syncId) {
        super(ModScreenHandlers.ITEM_SETTINGS_SCREEN_HANDLER, syncId);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
