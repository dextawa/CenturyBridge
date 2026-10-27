package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.compat.fapi.impl.tag.CbFapiTagRemoval;

/**
 * The injector into 1.20.2's own TagFile static initializer that goes with {@link CbFapiTagFileRemoval} (its restored
 * 1.20.1 members stay there, at {@code CbVanillaReads.PRIORITY}): the codec the initialiser stored is replaced by one
 * that reads and writes "fabric:remove" as the initialiser ends.
 */
@Mixin(net.minecraft.class_7475.class)
abstract class CbFapiTagFileRemovalHooks {

    @Shadow @Final @Mutable
    private static Codec<net.minecraft.class_7475> field_39269;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$tagFileCodec(CallbackInfo ci) {
        field_39269 = CbFapiTagRemoval.tagFileCodec();
    }
}
