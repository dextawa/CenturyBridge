package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_3414;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemSounds;

/** 1.21.4's ItemStack members 1.21.5 dropped, restored on ItemStack */
@Mixin(class_1799.class)
public abstract class CbItemStackMembersMixin {

    /** 1.21.4's ItemStack.getBreakingSound(): the item's (1.21.4's body), through the restored Item member */
    public class_3414 method_57351() {
        return ((CbItemSounds.ItemBreakingSound) ((class_1799) (Object) this).method_7909()).method_57336();
    }
}
