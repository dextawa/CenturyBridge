package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbGuiOverrides;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbGuiClientPlayerSkinBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_742.class)
public abstract class CbGuiClientPlayerSkinBridgeHooks {

    private static final CbGuiOverrides.Hook[] CB_GETTERS = {
        new CbGuiOverrides.Hook(net.minecraft.class_742.class, "method_3125"),
        new CbGuiOverrides.Hook(net.minecraft.class_742.class, "method_3127"),
        new CbGuiOverrides.Hook(net.minecraft.class_742.class, "method_3117"),
        new CbGuiOverrides.Hook(net.minecraft.class_742.class, "method_3119"),
        new CbGuiOverrides.Hook(net.minecraft.class_742.class, "method_3126"),
        new CbGuiOverrides.Hook(net.minecraft.class_742.class, "method_3122"),
        new CbGuiOverrides.Hook(net.minecraft.class_742.class, "method_3121"),
    };

    @Inject(method = "method_52814", at = @At("RETURN"), cancellable = true)
    private void cb$legacyGetters(CallbackInfoReturnable<net.minecraft.class_8685> cir) {
        for (CbGuiOverrides.Hook h : CB_GETTERS) {
            if (h.in(this)) {
                top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.SkinGetters self = (top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.SkinGetters) (Object) this;
                net.minecraft.class_8685 skin = cir.getReturnValue();
                cir.setReturnValue(new net.minecraft.class_8685(self.method_3117(), skin.comp_1911(),
                    self.method_3125() ? self.method_3119() : null, self.method_3126() ? self.method_3122() : null,
                    net.minecraft.class_8685.class_7920.method_52857(self.method_3121()), skin.comp_1630()));
                return;
            }
        }
    }
}
