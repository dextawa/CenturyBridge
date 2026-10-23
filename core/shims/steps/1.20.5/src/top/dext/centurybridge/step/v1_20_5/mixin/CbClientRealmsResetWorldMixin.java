package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * RealmsResetWorldScreen.switchSlot(callback): 1.20.4's -- the long-running task screen (back to the screen
 * before) switching the realm to this screen's slot, the callback run on the main thread when it is done.
 * 1.20.5 folded the switch into its own reset flow and dropped the method.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4410.class)
public abstract class CbClientRealmsResetWorldMixin extends net.minecraft.class_437 {

    @Shadow
    @Final
    private net.minecraft.class_437 field_20000;

    @Shadow
    @Final
    private net.minecraft.class_4877 field_20001;

    @Shadow
    @Final
    public int field_19998;

    private CbClientRealmsResetWorldMixin() {
        super(null);
    }

    /** 1.20.4 RealmsResetWorldScreen.switchSlot(callback) */
    public void method_21377(Runnable callback) {
        this.field_22787.method_1507(new net.minecraft.class_4398(this.field_20000, new net.minecraft.class_4358[] {
            new net.minecraft.class_4443(this.field_20001.field_22599, this.field_19998, () -> this.field_22787.execute(callback))}));
    }
}
