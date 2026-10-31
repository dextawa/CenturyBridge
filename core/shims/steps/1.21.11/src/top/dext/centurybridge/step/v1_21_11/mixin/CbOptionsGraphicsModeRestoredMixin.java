package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_315;
import net.minecraft.class_5365;
import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_11.rt.CbGraphicsMode;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/** 1.21.10 Options.graphicsMode() given back as the live view of rt/CbGraphicsMode. 1.21.11 never calls it. */
@Mixin(value = class_315.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbOptionsGraphicsModeRestoredMixin {
    @Unique
    private class_7172<class_5365> cb$graphicsMode;

    /** Options.graphicsMode() */
    public class_7172<class_5365> method_42534() {
        class_7172<class_5365> v = this.cb$graphicsMode;
        if (v == null) {
            v = CbGraphicsMode.create((class_315) (Object) this);
            this.cb$graphicsMode = v;
        }
        return v;
    }
}
