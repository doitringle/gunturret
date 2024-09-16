package nowhed.ringlesgunturret.item;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.gui.ItemSettingsScreen;
import nowhed.ringlesgunturret.gui.ItemSettingsScreenHandler;
import nowhed.ringlesgunturret.sound.ModSounds;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TurretConfig extends Item {
    public TurretConfig(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        if (!world.isClient) {
            // This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity casted to
            // a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
            NamedScreenHandlerFactory screenHandlerFactory = createScreenHandlerFactory(user, hand);
            return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));

            if (screenHandlerFactory != null) {
                // With this call the server will request the client to open the appropriate Screenhandler
                user.openHandledScreen(screenHandlerFactory);
            }
        }
    }

    private NamedScreenHandlerFactory createScreenHandlerFactory(PlayerEntity player, Hand hand) {
        EquipmentSlot slot = switch (hand) {
            case MAIN_HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
        };
        ItemStack stack = player.getStackInHand(hand);
        return new NamedScreenHandlerFactory() {

            @Override
            public Text getDisplayName() {
                return stack.getName();
            }

            @Override
            public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ItemSettingsScreen();
            }
        };
        return null;
    }
}
