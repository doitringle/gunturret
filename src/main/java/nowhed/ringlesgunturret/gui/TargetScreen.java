package nowhed.ringlesgunturret.gui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TargetScreen extends Screen {

    private final Screen parent;
    protected TargetScreen(Screen parent) {
        super(Text.literal("TEST"));
        this.parent = parent;
    }

    public TextFieldWidget field1;
    public ButtonWidget target_hostiles;
    public ButtonWidget target_all;
    public ButtonWidget target_disable;
    public ButtonWidget target_onlyplayers;

    public int selection = 3;


    @Override
    protected void init() {
        //this goes x,y,h,w (which is weird, shouldn't it be x,y,w,h? or am i going crazy?)
        field1 = new TextFieldWidget(textRenderer,gw(400),gh(50),gh(150),gw(20),Text.literal(""));
        field1.setTooltip(Tooltip.of(Text.translatable("gui.textfield.player_names.tooltip")));
        addDrawableChild(field1);


        target_all = ButtonWidget.builder(Text.translatable("gui.button.target_all"), button -> {

                })
                .dimensions(50,50,150,20)
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_all.tooltip")))
                .build();
        target_all.active = false;
        target_hostiles = ButtonWidget.builder(Text.translatable("gui.button.target_hostiles"), button -> {

                })
                .dimensions(50,75,150,20)
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_hostiles.tooltip")))
                .build();
        target_onlyplayers = ButtonWidget.builder(Text.translatable("gui.button.target_onlyplayers"), button -> {

                })
                .dimensions(50,100,150,20)
                .build();
        target_disable = ButtonWidget.builder(Text.translatable("gui.button.target_disable"), button -> {

                })
                .dimensions(50,125,150,20)
                .tooltip(Tooltip.of(Text.translatable("gui.button.target_disable.tooltip")))
                .build();

        addDrawableChild(target_all);
        addDrawableChild(target_hostiles);
        addDrawableChild(target_onlyplayers);
        addDrawableChild(target_disable);

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
