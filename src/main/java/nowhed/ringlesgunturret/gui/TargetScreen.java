package nowhed.ringlesgunturret.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class TargetScreen extends Screen {

    private final Screen parent;
    protected TargetScreen(Screen parent) {
        super(Text.literal("TEST"));
        this.parent = parent;
    }

     public TextFieldWidget field1;

    @Override
    protected void init() {
        //this goes x,y,h,w (which is weird, shouldn't it be x,y,w,h? or am i going crazy?)
        field1 = new TextFieldWidget(textRenderer,gw(400),gh(50),gh(150),gw(20),Text.literal(""));
        addDrawableChild(field1);

        System.out.println("HEIGHT:" + height);
        System.out.println("WIDTH:" + width);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    public int gh(int heightVal) {
        return (int) (height * (heightVal / 345.0));
    }
    public int gw(int widthVal) {
        return (int) (width * (widthVal / 640.0));
    }
}
