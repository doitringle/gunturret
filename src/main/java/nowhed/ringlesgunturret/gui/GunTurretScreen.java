package nowhed.ringlesgunturret.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;

import java.util.Optional;

public class GunTurretScreen extends HandledScreen<GunTurretScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(RinglesGunTurret.MOD_ID, "textures/gui/generic2x2.png");
    public TextFieldWidget player_name_field;
    public ButtonWidget player_name_confirm;
    public TextWidget name_field_label;
    public ButtonWidget target_hostiles;
    public ButtonWidget target_all;
    public ButtonWidget target_disable;
    public ButtonWidget target_onlyplayers;


    public GunTurretScreen(GunTurretScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler,inventory,getPositionText(handler).orElse(title));
    }
    public ButtonWidget button1;
    @Override
    protected void init() {
        super.init();

        /*button1 = ButtonWidget.builder(Text.translatable("gui.button.settings"), button -> {
                    handler.setMenu(1);
                    remove(button1);
                })
                .dimensions(width / 2 - 250, height / 2 - 50, 150, 20)
                .tooltip(Tooltip.of(Text.translatable("gui.button.settings.tooltip")))
                .build();
       addDraw*/

        //this goes x,y,w,h

        name_field_label = new TextWidget(gw(400),gh(25),gw(125),gh(20),Text.translatable("gui.text.players_settings"),textRenderer);
        addDrawableChild(name_field_label);

        player_name_field = new TextFieldWidget(textRenderer,gw(425),gh(50),gw(150),gh(20),Text.literal(""));
        player_name_field.setTooltip(Tooltip.of(Text.translatable("gui.textfield.player_names.tooltip")));
        addDrawableChild(player_name_field);

        player_name_confirm = ButtonWidget.builder(Text.literal("✓"), button -> {
                    System.out.println(player_name_field.getText());
                })
                .dimensions(gw(580),gh(50),gh(25),gh(25))
                .build();
        addDrawableChild(player_name_confirm);

        target_all = ButtonWidget.builder(Text.translatable("gui.button.target_all"), button -> {
                    updateButton("all");
                })
                .dimensions(50,75,150,20)
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_all.tooltip")))
                .build();

        target_hostiles = ButtonWidget.builder(Text.translatable("gui.button.target_hostiles"), button -> {
                    updateButton("hostiles");
                })
                .dimensions(50,100,150,20)
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_hostiles.tooltip")))
                .build();
        target_onlyplayers = ButtonWidget.builder(Text.translatable("gui.button.target_onlyplayers"), button -> {
                    updateButton("onlyplayers");
                })
                .dimensions(50,125,150,20)
                .build();
        target_disable = ButtonWidget.builder(Text.translatable("gui.button.target_disable"), button -> {
                    updateButton("disable");
                })
                .dimensions(50,150,150,20)
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_disable.tooltip")))
                .build();

        updateButton("hostiles");
        // add left side buttons
        addDrawableChild(target_all);
        addDrawableChild(target_hostiles);
        addDrawableChild(target_onlyplayers);
        addDrawableChild(target_disable);



    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0,TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE,x,y,0,0,backgroundWidth,backgroundHeight);

    }

    private static Optional<Text> getPositionText(ScreenHandler handler) {
            if (handler instanceof GunTurretScreenHandler) {
                PlayerEntity playerEntity = ((GunTurretScreenHandler) handler).blockEntity.getOwner();

                if (playerEntity == null) return Optional.empty();

                Text ownerName = playerEntity.getDisplayName();

                Text output = Text.literal("Gun Turret / Owner: ").append(ownerName.copy());

                return ownerName != null ? Optional.of(output) : Optional.empty();
            } else {
                return Optional.empty();
            }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            renderBackground(context);
            super.render(context, mouseX, mouseY, delta);
            drawMouseoverTooltip(context,mouseX,mouseY);
    }

    @Override
    public void close() {
        if(player_name_field.isFocused())
            return;

        super.close();
    }

    public void updateButton(String sel) {

        //this is really quite terrible, but I didn't feel like doing it good
        target_all.active = true;
        target_hostiles.active = true;
        target_onlyplayers.active = true;
        target_disable.active = true;

        switch(sel) {
            case "all":
                target_all.active = false;
                break;
            case "hostiles":
                target_hostiles.active = false;
                break;
            case "onlyplayers":
                target_onlyplayers.active = false;
                break;
            default:
                target_disable.active = false;
                break;
        }

    }
    public int gh(int heightVal) {
        return (int) (height * (heightVal / 345.0));
    }
    public int gw(int widthVal) {
        return (int) (width * (widthVal / 640.0));
    }
}
