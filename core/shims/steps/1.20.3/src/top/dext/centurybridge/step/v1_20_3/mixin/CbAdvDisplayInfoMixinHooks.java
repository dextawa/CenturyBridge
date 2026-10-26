package top.dext.centurybridge.step.v1_20_3.mixin;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvJson;

/**
 * The injector into 1.20.3's own DisplayInfo static initializer that goes with {@link CbAdvDisplayInfoMixin} (its
 * restored 1.20.2 serializeToJson stays there, at {@code CbVanillaReads.PRIORITY}): DisplayInfo.CODEC goes through
 * CbAdvJson.displayCodec, 1.20.2's way of writing a display.
 */
@Mixin(net.minecraft.class_185.class)
public abstract class CbAdvDisplayInfoMixinHooks {

    @Shadow
    @Final
    @Mutable
    public static Codec<net.minecraft.class_185> field_47190;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$displayCodec(CallbackInfo ci) {
        field_47190 = CbAdvJson.displayCodec(field_47190);
    }
}
