package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * MultiLineEditBox.tick(): in 1.20.1 it advanced the frame counter the cursor blink was read from;
 * 1.20.2 blinks the cursor by the time since the box gained focus and has no counter, so the 1.20.1
 * tick has nothing left to do. Restored as that, for a mod's call and a mod subclass's override and
 * super call. (Its 1.20.1 caller, ChatReportScreen.tick, ticked vanilla's own box.)
 */
@Mixin(value = net.minecraft.class_7529.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbGuiMultiLineEditBoxBridge {

    /** 1.20.1 MultiLineEditBox.tick() */
    public void method_44406() {
    }
}
