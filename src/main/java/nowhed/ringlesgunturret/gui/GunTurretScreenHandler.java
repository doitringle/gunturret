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

    private final Inventory inventory;
    private final GunTurretBlockEntity blockEntity;
    private final int rows;
    private PlayerEntity playerEntity;

    public GunTurretScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId,inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public GunTurretScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.GUN_TURRET_SCREEN_HANDLER, syncId);
        checkSize((Inventory) blockEntity, 4);
        this.inventory = ((Inventory) blockEntity);
        this.rows = 2;
        this.playerEntity = playerInventory.player;
        inventory.onOpen(playerEntity);
        this.blockEntity = ((GunTurretBlockEntity) blockEntity);

        super.updateToClient();
        this.addSlot(new Slot(inventory, 0, 72, 26));
        this.addSlot(new Slot(inventory, 1, 90, 26));
        this.addSlot(new Slot(inventory, 2, 72, 44));
        this.addSlot(new Slot(inventory, 3, 90, 44));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);


    }

    public PlayerEntity getPlayerEntity() {
        return this.playerEntity;
    }

    public GunTurretBlockEntity getBlockEntity() {
        return this.blockEntity;
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
        this.blockEntity.requestTargetSettings(player);

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

}
