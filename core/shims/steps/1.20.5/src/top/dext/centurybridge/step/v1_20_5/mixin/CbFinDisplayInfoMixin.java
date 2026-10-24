package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinPredicates;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetBufs;

/**
 * DisplayInfo: 1.20.4's public serializeToNetwork(FriendlyByteBuf), which a subclass could override.
 * 1.20.5 writes a display through its stream codec into the private serializeToNetwork(RegistryFriendlyByteBuf).
 * The 1.20.4 method is back (it writes as 1.20.5 does), and when the display's class overrides it
 * 1.20.5's write is that override -- whose super call writes 1.20.5's way.
 */
@Mixin(net.minecraft.class_185.class)
public abstract class CbFinDisplayInfoMixin {

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

    @Inject(method = "method_813(Lnet/minecraft/class_9129;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyWrite(net.minecraft.class_9129 buf, CallbackInfo ci) {
        if (!CbFinPredicates.cbInDisplayWrite() && CbFinPredicates.cbOverridesDisplayWrite(this.getClass())) {
            boolean outer = CbFinPredicates.cbEnterDisplayWrite();
            try {
                this.method_813((net.minecraft.class_2540) buf);
            } finally {
                CbFinPredicates.cbExitDisplayWrite(outer);
            }
            ci.cancel();
        }
    }
}
