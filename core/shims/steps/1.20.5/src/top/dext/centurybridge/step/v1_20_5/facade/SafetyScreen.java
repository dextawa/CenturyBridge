package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * What a converted subclass of 1.20.4's SafetyScreen (the multiplayer warning) extends (superRenames): 1.20.4's
 * screen on 1.20.5's SafetyScreen (so it still is one) -- 1.20.4's WarningScreen layout (as facade/WarningScreen)
 * and SafetyScreen's initButtons: Proceed (remembering "don't show again" in options) to the multiplayer
 * screen, Back to the previous screen; Escape closes to no screen, as 1.20.4's Screen.onClose did.
 */
@Environment(EnvType.CLIENT)
public class SafetyScreen extends net.minecraft.class_4749 {

    private static final net.minecraft.class_2561 CONTENT = net.minecraft.class_2561.method_43471("multiplayerWarning.message");
    private static final net.minecraft.class_2561 CHECK = net.minecraft.class_2561.method_43471("multiplayerWarning.check");

    /** previous */
    public final net.minecraft.class_437 field_39354;
    /** message */
    public net.minecraft.class_5489 field_37222 = net.minecraft.class_5489.field_26528;

    public SafetyScreen(net.minecraft.class_437 previous) {
        super(previous);
        this.field_39354 = previous;
    }

    @Override
    protected void method_25426() {
        this.field_37222 = net.minecraft.class_5489.method_30890(this.field_22793, CONTENT, this.field_22789 - 100);
        int yOffset = (this.field_37222.method_30887() + 1) * this.method_44068();
        int checkWidth = this.field_22793.method_27525(CHECK);
        this.field_37217 = net.minecraft.class_4286.method_54787(CHECK, this.field_22793)
            .method_54789(this.field_22789 / 2 - checkWidth / 2 - 8, 76 + yOffset).method_54788();
        this.method_37063(this.field_37217);
        this.method_41160(yOffset);
    }

    /** initButtons(yOffset): 1.20.4 SafetyScreen's */
    protected void method_41160(int yOffset) {
        this.method_37063(net.minecraft.class_4185.method_46430(net.minecraft.class_5244.field_24338, b -> {
            if (this.field_37217.method_20372()) {
                this.field_22787.field_1690.field_21840 = true;
                this.field_22787.field_1690.method_1640();
            }
            this.field_22787.method_1507(new net.minecraft.class_500(this.field_39354));
        }).method_46434(this.field_22789 / 2 - 155, 100 + yOffset, 150, 20).method_46431());
        this.method_37063(net.minecraft.class_4185.method_46430(net.minecraft.class_5244.field_24339,
            b -> this.field_22787.method_1507(this.field_39354))
            .method_46434(this.field_22789 / 2 - 155 + 160, 100 + yOffset, 150, 20).method_46431());
    }

    /** 1.20.4's resize: Screen's (the widgets rebuilt) */
    @Override
    protected void method_48640() {
        this.method_41843();
    }

    /** 1.20.4's SafetyScreen kept Screen.onClose: back to no screen */
    @Override
    public void method_25419() {
        this.field_22787.method_1507(null);
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
