package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.CrashReportCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * CrashReportCategory.trimStacktrace(int), which 26.2 dropped, restored with 26.1.2's body (javap): the category's
 * stack trace without its last {@code amount} frames. 26.1.2's vanilla never called it (only mods did), so a mod's
 * mixin on it changes what it did on 26.1.2: the calls mods make.
 */
@Mixin(value = CrashReportCategory.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26CrashCategoryMixin {

    @Shadow
    private StackTraceElement[] stackTrace;

    /** 26.1.2 CrashReportCategory.trimStacktrace(amount) */
    public void trimStacktrace(int amount) {
        StackTraceElement[] trimmed = new StackTraceElement[this.stackTrace.length - amount];
        System.arraycopy(this.stackTrace, 0, trimmed, 0, trimmed.length);
        this.stackTrace = trimmed;
    }
}
