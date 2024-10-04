package nowhed.ringlesgunturret.gui;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;
import nowhed.ringlesgunturret.player.PlayerData;
import nowhed.ringlesgunturret.player.StateSaver;
import nowhed.ringlesgunturret.sound.ModSounds;

import javax.swing.plaf.nimbus.State;
import java.util.Optional;

public class GunTurretScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    public final GunTurretBlockEntity blockEntity;
    private final int rows;
    private PlayerEntity playerEntity;
    private final ScreenHandlerContext context;


    public GunTurretScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId,inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()), ScreenHandlerContext.EMPTY);
    }


    public GunTurretScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, ScreenHandlerContext context) {
        super(ModScreenHandlers.GUN_TURRET_SCREEN_HANDLER, syncId);
        checkSize((Inventory) blockEntity, 4);
        this.inventory = ((Inventory) blockEntity);
        this.rows = 2;
        this.playerEntity = playerInventory.player;
        this.context = context;
        inventory.onOpen(playerEntity);
        this.blockEntity = ((GunTurretBlockEntity) blockEntity);

        this.addSlot(new Slot(inventory, 0, 72, 26));
        this.addSlot(new Slot(inventory, 1, 90, 26));
        this.addSlot(new Slot(inventory, 2, 72, 44));
        this.addSlot(new Slot(inventory, 3, 90, 44));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);


    }



    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
        player.getWorld().playSound(null, player.getBlockPos(), ModSounds.CLOSE, SoundCategory.BLOCKS, 0.5f, 1f);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0;i<3;++i) {
            for (int l = 0;l < 9;++l) {
                this.addSlot(new Slot(playerInventory, l+i*9+9,8+l*18,84+i*18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0;i<9;++i) {
            this.addSlot(new Slot(playerInventory, i,8+i*18,142));
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getRows() {
        return this.rows;
    }

    public PlayerData getPlayerData() {
        Optional<PlayerData> result = this.context.get((world, pos) -> {
            return StateSaver.getPlayerState(playerEntity, world);
        });
        return result.orElse(null);
    }

    public void contextTest() {
        this.context.run((world,pos) -> {
           System.out.println("success! " + world + "|" + pos);
        });
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        this.context.run((world, pos) -> {
            PlayerData playerState = StateSaver.getPlayerState(player,world);
            switch(id) {
                case 0: // "all" button
                    playerState.targetSelection = "all";
                    break;
                case 1: // "hostiles" button
                    playerState.targetSelection = "hostiles";
                    break;
                case 2: // "onlyplayers" button
                    playerState.targetSelection = "onlyplayers";
                    break;
                case 3: // "disable" button
                    playerState.targetSelection = "disable";
                    break;
                case 4: // blacklist button
                    playerState.blacklist = true;
                    break;
                case 5: // whitelist button
                    playerState.blacklist = false;
                    break;
            }
        });
        return true;
    }


}
