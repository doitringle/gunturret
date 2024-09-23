package nowhed.ringlesgunturret.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class TargetScreen extends Screen {

    private final Screen parent;
    protected TargetScreen(Screen parent) {
        super(Text.literal("TEST"));
        this.parent = parent;
    }

     public TextFieldWidget field1;

    @Override
    protected void init() {
        field1 = new TextFieldWidget(textRenderer,200,50,50,50,Text.literal(""));
        addDrawableChild(field1);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
