package nowhed.ringlesgunturret.gui;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import nowhed.ringlesgunturret.block.entity.GunTurretBlockEntity;
import nowhed.ringlesgunturret.sound.ModSounds;

public class GunTurretScreenHandler extends ScreenHandler {

    private static final int NUM_COLUMNS = 2;
    private final Inventory inventory;
    public final GunTurretBlockEntity blockEntity;
    private final int rows;

    public GunTurretScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId,inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public GunTurretScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.GUN_TURRET_SCREEN_HANDLER, syncId);
        checkSize((Inventory) blockEntity, 4);
        this.inventory = ((Inventory) blockEntity);
        this.rows = 2;
        inventory.onOpen(playerInventory.player);
        this.blockEntity = ((GunTurretBlockEntity) blockEntity);

        this.addSlot(new Slot(inventory,0,72,26));
        this.addSlot(new Slot(inventory,1,90,26));
        this.addSlot(new Slot(inventory,2,72,44));
        this.addSlot(new Slot(inventory,3,90,44)); // this should work...?

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < 2) {
                if (!this.insertItem(itemStack2, this.rows * 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, this.rows * 2, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
        player.getWorld().playSound(null, player.getBlockPos(), ModSounds.CLOSE, SoundCategory.BLOCKS, 1f, 1f);
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
}
