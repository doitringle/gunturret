package nowhed.ringlesgunturret.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
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

        public GunTurretScreen(GunTurretScreenHandler handler, PlayerInventory inventory, Text title) {
            super(handler,inventory,getPositionText(handler).orElse(title));
        }
    @Override
    protected void init() {
        super.init();
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
}
