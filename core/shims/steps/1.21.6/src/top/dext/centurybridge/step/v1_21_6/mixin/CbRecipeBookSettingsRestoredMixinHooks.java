package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_2540;
import net.minecraft.class_5411;
import net.minecraft.class_9139;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_6.rt.CbRecipeBooks;

/**
 * The injector into 1.21.6's own RecipeBookSettings &lt;clinit&gt; that goes with {@link CbRecipeBookSettingsRestoredMixin}
 * (its restored 1.21.5 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.5 injectors can anchor
 * inside them): STREAM_CODEC wrapped at &lt;clinit&gt; TAIL, decoding / encoding through the restored read / write
 * (CbPrioritySplit.RecipeBookSettingsNet) only when a converted mod mixed them, otherwise through 1.21.6's own codec
 * untouched. Same wire format both ways.
 */
@Mixin(class_5411.class)
public abstract class CbRecipeBookSettingsRestoredMixinHooks {

    @Shadow
    @Final
    @Mutable
    public static class_9139<class_2540, class_5411> field_54548;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$wrapStreamCodec(CallbackInfo ci) {
        class_9139<class_2540, class_5411> native16 = field_54548;
        field_54548 = class_9139.method_56438((settings, buf) -> {
            if (CbRecipeBooks.routeNetWrite()) {
                ((CbPrioritySplit.RecipeBookSettingsNet) (Object) settings).cb$v1_21_6$netWrite(buf);
            } else {
                native16.encode(buf, settings);
            }
        }, buf -> CbRecipeBooks.routeNetRead()
            ? ((CbPrioritySplit.RecipeBookSettingsNet) (Object) new class_5411()).cb$v1_21_6$netRead(buf)
            : native16.decode(buf));
    }
}
