package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_3272;
import net.minecraft.class_3288;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbPacks;

/**
 * 1.20.2's Pack.readPackInfo(id, resources, version) builds an Info without the pack format the
 * pack declared; 1.20.1's Info answered it (format()). The declared format seen while the info is
 * read (getDeclaredPackVersions is handed the pack's metadata section) is recorded for the info it
 * returns (CbPacks).
 */
@Mixin(class_3288.class)
public abstract class CbPackInfoFormat {

    @Inject(method = "method_52440", at = @At("HEAD"))
    private static void cb$seeFormat(String id, class_3272 meta, CallbackInfoReturnable<?> cir) {
        if (meta != null) {
            CbPacks.seeDeclaredFormat(meta.comp_1581());
        }
    }

    /**
     * Takes none of readPackInfo's arguments (it reads only the answer): 1.20.5 opens the pack by location where
     * 1.20.4 took its id, and an injector capturing no arguments climbs onto the reshaped method as it is.
     */
    @Inject(method = "method_45274", at = @At("RETURN"))
    private static void cb$recordFormat(CallbackInfoReturnable<class_3288.class_7679> cir) {
        CbPacks.claimDeclaredFormat(cir.getReturnValue());
    }
}
