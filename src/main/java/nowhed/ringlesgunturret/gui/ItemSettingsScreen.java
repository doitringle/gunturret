package nowhed.ringlesgunturret.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
/*
@Environment(EnvType.CLIENT)
public class ItemSettingsScreen extends HandledScreen<ItemSettingsScreenHandler> {
    PlayerEntity player;
    Hand hand;
    ItemStack itemStack;
    public ItemSettingsScreen(ItemSettingsScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler,inventory,title);
    }

    public ButtonWidget button1;
    public ButtonWidget button2;

    @Override
    protected void init() {
        button1 = ButtonWidget.builder(Text.literal("Button 1"), button -> {
                    System.out.println("You clicked button1!");
                })
                .dimensions(width / 2 - 205, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button1")))
                .build();
        button2 = ButtonWidget.builder(Text.literal("Button 2"), button -> {
                    System.out.println("You clicked button2!");
                })
                .dimensions(width / 2 + 5, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button2")))
                .build();

        addDrawableChild(button1);
        addDrawableChild(button2);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

}*/