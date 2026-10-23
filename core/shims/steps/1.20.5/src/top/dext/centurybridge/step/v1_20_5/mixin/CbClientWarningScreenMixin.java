package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * WarningScreen's 1.20.4 members, named on 1.20.5's WarningScreen so a converted subclass's own calls to them
 * (owner WarningScreen; the subclass extends facade/WarningScreen, which overrides each) resolve: renderTitle
 * and getLineHeight with 1.20.4's bodies, initButtons as 1.20.4 declared it -- abstract, every 1.20.4 warning
 * screen supplying its own.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_7065.class)
public abstract class CbClientWarningScreenMixin extends net.minecraft.class_437 {

    private CbClientWarningScreenMixin() {
        super(null);
    }

    /** 1.20.4 WarningScreen.initButtons(yOffset): abstract there */
    protected void method_41160(int yOffset) {
        throw new AbstractMethodError("WarningScreen.initButtons(int) is abstract in 1.20.4");
    }

    /** 1.20.4 WarningScreen.renderTitle(graphics) */
    protected void method_44663(net.minecraft.class_332 graphics) {
        graphics.method_27535(this.field_22793, this.field_22785, 25, 30, 16777215);
    }

    /** 1.20.4 WarningScreen.getLineHeight() */
    protected int method_44068() {
        return 9 * 2;
    }
}
