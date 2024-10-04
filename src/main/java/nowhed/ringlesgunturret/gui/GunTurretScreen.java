package nowhed.ringlesgunturret.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.networking.ModMessages;
import nowhed.ringlesgunturret.player.PlayerData;

import java.util.Optional;

public class GunTurretScreen extends HandledScreen<GunTurretScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(RinglesGunTurret.MOD_ID, "textures/gui/generic2x2.png");
    public TextFieldWidget player_name_field;
    public ButtonWidget player_name_confirm;
    public TextWidget name_field_label;
    public TextWidget main_label;
    public ButtonWidget target_hostiles;
    public ButtonWidget target_all;
    public ButtonWidget target_disable;
    public ButtonWidget target_onlyplayers;

    public ButtonWidget whitelist;
    public ButtonWidget blacklist;


    public GunTurretScreen(GunTurretScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler,inventory,getPositionText(handler).orElse(title));
    }

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

        // insane gui code, ringle!

        name_field_label = new TextWidget(gw(410),gh(50),gw(125),gh(25),Text.translatable("gui.text.players_settings"),textRenderer);
        addDrawableChild(name_field_label);

        main_label = new TextWidget(gw(75),gh(50),gw(100),gh(25),Text.translatable("gui.text.main_label"),textRenderer);
        addDrawableChild(main_label);

        player_name_field = new TextFieldWidget(textRenderer,gw(425),gh(75),gw(150),gh(20),Text.literal(""));
        player_name_field.setTooltip(Tooltip.of(Text.translatable("gui.textfield.player_names.tooltip")));
        addDrawableChild(player_name_field);

        whitelist = ButtonWidget.builder(Text.translatable("gui.button.whitelist"), button -> {
                    updateButtonPlayers(false);
                })
                .dimensions(gw(425),gh(100),gw(150),gh(20))
                .build();
        addDrawableChild(whitelist);

        blacklist = ButtonWidget.builder(Text.translatable("gui.button.blacklist"), button -> {
                    updateButtonPlayers(true);
                })
                .dimensions(gw(425),gh(125),gw(150),gh(20))
                .build();
        addDrawableChild(blacklist);


        player_name_confirm = ButtonWidget.builder(Text.literal("✓"), button -> {
                    //this.handler.onButtonClick(this.client.player, 6);
                    PacketByteBuf packet = PacketByteBufs.create();
                    packet.writeString(player_name_field.getText());
                    ClientPlayNetworking.send(ModMessages.TARGET_SELECTION_ID, packet);

                })
                .dimensions(player_name_field.getWidth()+player_name_field.getX()+5,player_name_field.getY()-2,gh(23),gh(23))
                .build();

        addDrawableChild(player_name_confirm);

        target_all = ButtonWidget.builder(Text.translatable("gui.button.target_all"), button -> {
                    updateButton("all");
                })
                .dimensions(gw(50),gh(75),gw(150),gh(20))
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_all.tooltip")))
                .build();

        target_hostiles = ButtonWidget.builder(Text.translatable("gui.button.target_hostiles"), button -> {
                    updateButton("hostiles");
                })
                .dimensions(gw(50),gh(100),gw(150),gh(20))
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_hostiles.tooltip")))
                .build();
        target_onlyplayers = ButtonWidget.builder(Text.translatable("gui.button.target_onlyplayers"), button -> {
                    updateButton("onlyplayers");
                })
                .dimensions(gw(50),gh(125),gw(150),gh(20))
                .build();
        target_disable = ButtonWidget.builder(Text.translatable("gui.button.target_disable"), button -> {

                    updateButton("disable");
                })
                .dimensions(gw(50),gh(150),gw(150),gh(20))
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_disable.tooltip")))
                .build();

        initialPlayerData();

        // add left side buttons
        addDrawableChild(target_all);
        addDrawableChild(target_hostiles);
        addDrawableChild(target_onlyplayers);
        addDrawableChild(target_disable);



    }

    private void initialPlayerData() {
        PlayerData playerData = this.handler.getPlayerData();
        if (playerData == null) {
            System.out.println("Player data not found.");
            updateButton("hostiles");
            updateButtonPlayers(false);
        } else {
            System.out.println("Player data found! Loading...");
            updateButton(playerData.targetSelection,false);
            updateButtonPlayers(playerData.blacklist,false);
            player_name_field.setText(playerData.playerList);
        }
    }

    public void updateButton(String sel){
        updateButton(sel, true);
    }
    public void updateButton(String sel,Boolean press) {

        //this is really quite terrible, but I didn't feel like doing it good
        target_all.active = true;
        target_hostiles.active = true;
        target_onlyplayers.active = true;
        target_disable.active = true;
        int id = 0;
        switch(sel) {
            case "all":
                id=0;
                target_all.active = false;
                break;
            case "hostiles":
                id=1;
                target_hostiles.active = false;
                break;
            case "onlyplayers":
                id=2;
                target_onlyplayers.active = false;
                break;
            default: // "disable"
                id=3;
                target_disable.active = false;
                break;
        }

        if(press) {
            this.handler.onButtonClick(this.client.player, id);
            this.client.interactionManager.clickButton(this.handler.syncId, id);

        }


    }
    public void updateButtonPlayers(Boolean isBlacklist){
        updateButtonPlayers(isBlacklist, true);
    }
    public void updateButtonPlayers(Boolean isBlacklist,Boolean press) {
        // true = blacklist false = whitelist
        int id;
        if(isBlacklist) {
            blacklist.active = false;
            whitelist.active = true;
            id=4;
        } else {
            blacklist.active = true;
            whitelist.active = false;
            id=5;
        }

        if(press) {
            this.handler.onButtonClick(this.client.player, id);
            this.client.interactionManager.clickButton(this.handler.syncId, id);
        }

    }
    public int gh(int heightVal) {
        return (int) (height * (heightVal / 345.0));
    }
    public int gw(int widthVal) {
        return (int) (width * (widthVal / 640.0));
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


}
