package nowhed.ringlesgunturret.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nowhed.ringlesgunturret.RinglesGunTurret;
import nowhed.ringlesgunturret.networking.ModMessages;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class GunTurretScreen extends HandledScreen<GunTurretScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(RinglesGunTurret.MOD_ID, "textures/gui/generic2x2.png");

    public TextWidget name_field_label;
    public TextWidget main_label;
    public ButtonWidget target_hostiles;
    public ButtonWidget target_all;
    public ButtonWidget target_disable;
    public ButtonWidget target_onlyplayers;

    public TextFieldWidget player_name_field;
    public ButtonWidget player_name_confirm;
    public ButtonWidget whitelist;
    public ButtonWidget blacklist;

    public ButtonWidget claim;
    public MultilineTextWidget warning_box;


    public GunTurretScreen(GunTurretScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler,inventory,getPositionText(handler).orElse(title));
    }

    @Override
    protected void init() {
        super.init();

        /*button1 = ButtonWidget.builder(Text.translatable("gui.button.ringlesgunturret.settings"), button -> {
                    handler.setMenu(1);
                    remove(button1);
                })
                .dimensions(width / 2 - 250, height / 2 - 50, 150, 20)
                .tooltip(Tooltip.of(Text.translatable("gui.button.ringlesgunturret.settings.tooltip")))
                .build();
       addDraw*/

        //this goes x,y,w,h

        // insane gui code, ringle!

        name_field_label = new TextWidget(gw(410),gh(50),gw(125),gh(25),Text.translatable("gui.text.ringlesgunturret.players_settings"),textRenderer);


        warning_box = new MultilineTextWidget(gw(170),gh(280),Text.literal(""),textRenderer);
        warning_box.setMaxWidth(gw(300));
        warning_box.setMaxRows(5);
        warning_box.setTextColor(16777045);
        warning_box.active = true;


        main_label = new TextWidget(gw(75),gh(50),gw(100),gh(25),Text.translatable("gui.text.ringlesgunturret.main_label"),textRenderer);


        player_name_field = new TextFieldWidget(textRenderer,gw(425),gh(75),gw(150),gh(20),Text.literal(" "));
        player_name_field.setTooltip(Tooltip.of(Text.translatable("gui.text_field.ringlesgunturret.player_names.tooltip")));


        whitelist = ButtonWidget.builder(Text.translatable("gui.button.ringlesgunturret.whitelist"), button -> {
                    updateButtonPlayers(false);
                })
                .dimensions(gw(425),gh(100),gw(150),gh(20))
                .build();


        blacklist = ButtonWidget.builder(Text.translatable("gui.button.ringlesgunturret.blacklist"), button -> {
                    updateButtonPlayers(true);
                })
                .dimensions(gw(425),gh(125),gw(150),gh(20))
                .build();



        player_name_confirm = ButtonWidget.builder(Text.literal("✓"), button -> {
                    // make all lowercase and also remove whitespace
                    String namelist = player_name_field.getText().toLowerCase().replaceAll("\\s","");
                    PacketByteBuf packet = PacketByteBufs.create();
                    packet.writeString(namelist);
                    ClientPlayNetworking.send(ModMessages.PLAYER_LIST_ID, packet);
                })
                .dimensions(player_name_field.getWidth()+player_name_field.getX()+5,player_name_field.getY()-2,gh(23),gh(23))
                .build();



        target_all = ButtonWidget.builder(Text.translatable("gui.button.ringlesgunturret.target_all"), button -> {
                    updateButton("all");
                })
                .dimensions(gw(55),gh(75),gw(150),gh(20))
                .tooltip(Tooltip.of(Text.translatable("gui.button.ringlesgunturret.target_all.tooltip")))
                .build();

        target_hostiles = ButtonWidget.builder(Text.translatable("gui.button.ringlesgunturret.target_hostiles"), button -> {
                    updateButton("hostiles");
                })
                .dimensions(gw(55),gh(100),gw(150),gh(20))
                .tooltip(Tooltip.of(Text.translatable("gui.button.ringlesgunturret.target_hostiles.tooltip")))
                .build();
        target_onlyplayers = ButtonWidget.builder(Text.translatable("gui.button.ringlesgunturret.target_onlyplayers"), button -> {
                    updateButton("onlyplayers");
                })
                .dimensions(gw(55),gh(125),gw(150),gh(20))
                .build();
        target_disable = ButtonWidget.builder(Text.translatable("gui.button.ringlesgunturret.target_disable"), button -> {

                    updateButton("disable");
                })
                .dimensions(gw(55),gh(150),gw(150),gh(20))
                .tooltip(Tooltip.of(Text.translatable("gui.button.ringlesgunturret.target_disable.tooltip")))
                .build();

        claim = ButtonWidget.builder(Text.translatable("gui.button.ringlesgunturret.claim_gun_turret"),button -> {
                    //send claim request to server
                    PacketByteBuf packet = PacketByteBufs.create();
                    packet.writeBlockPos(this.handler.getBlockEntity().getPos());
                    ClientPlayNetworking.send(ModMessages.CLAIM_ID, packet);
        })
                .dimensions(gw(75),gh(300),gw(75),gh(20))
                .tooltip(Tooltip.of(Text.translatable("gui.button.ringlesgunturret.claim_gun_turret.tooltip")))
                .build();

        requestPlayerData();

        boolean hasOwner = this.handler.getBlockEntity().getOwner() != null;

        if(!hasOwner || !this.handler.getPlayerEntity().getUuid().equals(this.handler.getBlockEntity().getOwner().getUuid())) {
            setAllVisible(false);
            setWarningBox("message.ringlesgunturret.warning.not_owned_by_player");
        } else {
            setAllVisible(true);
        }

        if(!hasOwner
                && (this.handler.getPlayerEntity().getWorld().getGameRules().getBoolean(RinglesGunTurret.SURVIVAL_CLAIM_TURRET)
            || this.handler.getPlayerEntity().isCreative())) {
            //if the turret is claimable and the player is either in creative or they can claim turrets
            //allow claiming
            claim.visible = true;
        } else {
            // no allow claiming
            claim.visible = false;
        }

        // add left side buttons and labels
        addDrawableChild(main_label);
        addDrawableChild(target_all);
        addDrawableChild(target_hostiles);
        addDrawableChild(target_onlyplayers);
        addDrawableChild(target_disable);
        //add right side buttons and labels
        addDrawableChild(name_field_label);
        addDrawableChild(player_name_field);
        addDrawableChild(whitelist);
        addDrawableChild(blacklist);
        addDrawableChild(player_name_confirm);
        //special
        addDrawableChild(claim);
        addDrawableChild(warning_box);

    }

    private void requestPlayerData() {
        ClientPlayNetworking.send(ModMessages.REQUEST_PLAYER_DATA_ID,PacketByteBufs.create());
    }

    public void setPlayerData(String targetSel, String playerLst, Boolean blklst) {
        updateButton(targetSel,false);
        updateButtonPlayers(blklst,false);
        player_name_field.setText(playerLst);
    }

    public void setAllVisible(boolean visible) {
        main_label.visible = visible;
        target_all.visible = visible;
        target_hostiles.visible = visible;
        target_onlyplayers.visible = visible;
        target_disable.visible = visible;
        name_field_label.visible = visible;
        player_name_field.visible = visible;
        whitelist.visible = visible;
        blacklist.visible = visible;
        player_name_confirm.visible = visible;
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
        switch(sel) {
            case "all":
                target_all.active = false;
                setWarningBox("message.ringlesgunturret.warning.target_all");
                break;
            case "hostiles":
                target_hostiles.active = false;
                break;
            case "onlyplayers":
                target_onlyplayers.active = false;
                break;
            default: // "disable"
                sel = "disable";
                target_disable.active = false;
                break;
        }

        if(press) {
            PacketByteBuf packet = PacketByteBufs.create();
            packet.writeString(sel);
            ClientPlayNetworking.send(ModMessages.TARGET_SELECTION_ID, packet);
        }


    }
    public void updateButtonPlayers(Boolean isBlacklist){
        updateButtonPlayers(isBlacklist, true);
    }

    public void setWarningBox(String translatableId) {
        warning_box.setMessage(Text.translatable(translatableId));
        //RinglesGunTurret.LOGGER.info(Text.translatable(translatableId).getString());
    }

    public void updateButtonPlayers(Boolean isBlacklist,Boolean press) {
        // true = blacklist false = whitelist
        if(isBlacklist) {
            blacklist.active = false;
            whitelist.active = true;
        } else {
            blacklist.active = true;
            whitelist.active = false;
        }

        if(press) {
            PacketByteBuf packet = PacketByteBufs.create();
            packet.writeBoolean(isBlacklist);
            ClientPlayNetworking.send(ModMessages.BLACKLIST_ID, packet);
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
                PlayerEntity playerEntity = ((GunTurretScreenHandler) handler).getBlockEntity().getOwner();

                if (playerEntity == null) return Optional.empty();

                Text ownerName = playerEntity.getDisplayName();

                Text output = Text.translatable("block.ringlesgunturret.gun_turret").append(Text.literal(" / Owner: ")).append(ownerName.copy());
                // Gun Turret(append) / Owner: (append)[player name]

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

    /*@Override
    public void close() {
        if(player_name_field.isFocused()) {
            KeyBinding invKey = client.options.inventoryKey;

            if (!invKey.wasPressed()) { // if the player didn't press E, they must have pressed ESC
                player_name_field.setFocused(false);
                RinglesGunTurret.LOGGER.info("esc press'd");
            }
            return;
        }
        super.close();
    }*/

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!player_name_field.isFocused()) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {

            if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
                // IF FIELD FOCUSED and ESCAPE PRESSED, unfocus field, DO NOT CLOSE SCREEN
                player_name_field.setFocused(false);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
                player_name_field.setFocused(false);
                player_name_confirm.onPress();
                return true;
            } else if (this.getFocused().keyPressed(keyCode, scanCode, modifiers)) { // enters the key into the field
                return true;
            }
        }
        return false;
    }


}
