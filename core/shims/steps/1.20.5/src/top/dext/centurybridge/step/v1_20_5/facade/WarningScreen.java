package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * What a converted subclass of 1.20.4's WarningScreen extends (superRenames). 1.20.4's warning screen laid
 * out its message (a MultiLineLabel 100 narrower than the screen at y 70), an optional "don't show again"
 * checkbox below it, and whatever buttons the subclass's initButtons(yOffset) added; 1.20.5's builds a
 * scrolling layout around addFooterButtons(). This class is 1.20.4's screen again on 1.20.5's WarningScreen
 * (so it still is one): 1.20.4's init, render, renderTitle and getLineHeight, and 1.20.4's resize (rebuild).
 */
@Environment(EnvType.CLIENT)
public abstract class WarningScreen extends net.minecraft.class_7065 {

    /** content */
    public final net.minecraft.class_2561 field_37219;
    /** check */
    public final net.minecraft.class_2561 field_37220;
    /** narration */
    public final net.minecraft.class_2561 field_37221;
    /** message */
    public net.minecraft.class_5489 field_37222 = net.minecraft.class_5489.field_26528;

    protected WarningScreen(net.minecraft.class_2561 title, net.minecraft.class_2561 content, net.minecraft.class_2561 narration) {
        this(title, content, null, narration);
    }

    protected WarningScreen(net.minecraft.class_2561 title, net.minecraft.class_2561 content, net.minecraft.class_2561 check,
                            net.minecraft.class_2561 narration) {
        super(title, content, check, narration);
        this.field_37219 = content;
        this.field_37220 = check;
        this.field_37221 = narration;
    }

    /** initButtons(yOffset) */
    protected abstract void method_41160(int yOffset);

    /** 1.20.5's footer is never built: 1.20.4's init places the buttons (initButtons) */
    @Override
    protected net.minecraft.class_8133 method_57750() {
        return net.minecraft.class_8667.method_52741();
    }

    @Override
    protected void method_25426() {
        this.field_37222 = net.minecraft.class_5489.method_30890(this.field_22793, this.field_37219, this.field_22789 - 100);
        int yOffset = (this.field_37222.method_30887() + 1) * this.method_44068();
        if (this.field_37220 != null) {
            int checkWidth = this.field_22793.method_27525(this.field_37220);
            this.field_37217 = net.minecraft.class_4286.method_54787(this.field_37220, this.field_22793)
                .method_54789(this.field_22789 / 2 - checkWidth / 2 - 8, 76 + yOffset).method_54788();
            this.method_37063(this.field_37217);
        }
        this.method_41160(yOffset);
    }

    /** 1.20.4's resize: Screen's (the widgets rebuilt), 1.20.5's layout never having been built */
    @Override
    protected void method_48640() {
        this.method_41843();
    }

    @Override
    public void method_25394(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick) {
        super.method_25394(graphics, mouseX, mouseY, partialTick);
        this.method_44663(graphics);
        int x = this.field_22789 / 2 - this.field_37222.method_44048() / 2;
        this.field_37222.method_30893(graphics, x, 70, this.method_44068(), 16777215);
    }

    /** renderTitle(graphics) */
    protected void method_44663(net.minecraft.class_332 graphics) {
        graphics.method_27535(this.field_22793, this.field_22785, 25, 30, 16777215);
    }

    /** getLineHeight() */
    protected int method_44068() {
        return 9 * 2;
    }
}
