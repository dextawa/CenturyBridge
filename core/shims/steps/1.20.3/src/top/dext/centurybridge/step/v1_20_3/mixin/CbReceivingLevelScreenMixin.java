package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * ReceivingLevelScreen.loadingPacketsReceived() (method_40040), gone in 1.20.3: 1.20.2's client packet
 * listener called it when the level's loading packets had arrived, and the screen then closed once the
 * player's section was ready. Recorded here. A screen built with the 1.20.2 constructor closes by that
 * record and 1.20.2's readiness test ({@link CbGui.CbLegacyLoading}); one 1.20.3 code built closes by the
 * supplier it was given, which tracks the same arrival itself.
 */
@Mixin(value = net.minecraft.class_434.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbReceivingLevelScreenMixin implements CbGui.CbLegacyReceivingLevel {

    /** levelReceived */
    @Shadow
    @Final
    private java.util.function.BooleanSupplier field_46575;

    @Unique
    private boolean cb$loadingPacketsReceived;

    @Override
    public void method_40040() {
        cb$loadingPacketsReceived = true;
    }

    @Override
    public boolean cb$loadingPacketsReceived() {
        return cb$loadingPacketsReceived;
    }

    @Override
    public java.util.function.BooleanSupplier cb$levelReceived() {
        return field_46575;
    }
}
