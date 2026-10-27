package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * SafetyScreen.initButtons(yOffset), named on 1.20.5's SafetyScreen so a converted subclass's own call to it
 * (owner SafetyScreen; the subclass extends facade/SafetyScreen, which overrides it) resolves: 1.20.4's body --
 * Proceed (remembering "don't show again") to the multiplayer screen, Back to the previous screen.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_4749.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientSafetyScreenMixin extends net.minecraft.class_7065 {

    @Shadow
    @Final
    private net.minecraft.class_437 field_39354;

    private CbClientSafetyScreenMixin() {
        super(null, null, null);
    }

    /** 1.20.4 SafetyScreen.initButtons(yOffset) */
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
}
