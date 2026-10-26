package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinPredicates;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetBufs;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * DisplayInfo: 1.20.4's public serializeToNetwork(FriendlyByteBuf), which a subclass could override.
 * 1.20.5 writes a display through its stream codec into the private serializeToNetwork(RegistryFriendlyByteBuf).
 * The 1.20.4 method is back (it writes as 1.20.5 does), and when the display's class overrides it
 * 1.20.5's write is that override -- whose super call writes 1.20.5's way (CbFinDisplayInfoMixinHooks, default
 * priority).
 */
@Mixin(value = net.minecraft.class_185.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFinDisplayInfoMixin implements CbPrioritySplit.DisplayWrite {

    @Shadow
    private void method_813(net.minecraft.class_9129 buf) {
        throw new AssertionError();
    }

    /** was serializeToNetwork(buf) */
    public void method_813(net.minecraft.class_2540 buf) {
        boolean outer = CbFinPredicates.cbEnterDisplayWrite();
        try {
            this.method_813(CbNetBufs.cbRegistryBuf(buf));
        } finally {
            CbFinPredicates.cbExitDisplayWrite(outer);
        }
    }

    /** 1.20.4's serializeToNetwork called virtually, for 1.20.5's write (CbFinDisplayInfoMixinHooks) */
    @Override
    public void cb$v1_20_5$serializeToNetwork(net.minecraft.class_2540 buf) {
        this.method_813(buf);
    }
}
