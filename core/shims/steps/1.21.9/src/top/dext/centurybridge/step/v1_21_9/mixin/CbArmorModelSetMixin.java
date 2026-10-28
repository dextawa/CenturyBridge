package top.dext.centurybridge.step.v1_21_9.mixin;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.class_11677;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbArmorLayers;

/**
 * Carries rt/CbArmorLayers' whole armor meshes along 1.21.9's ArmorModelSet: through every map(fn) (baby scaling, the
 * layer definition: the same fn is applied to the whole meshes), and into the model roots at putFrom, where a vanilla
 * set's whole definitions also go in under 1.21.8's inner / outer armor locations. Additive injectors: what the set
 * holds and answers is untouched.
 */
@Mixin(class_11677.class)
public abstract class CbArmorModelSetMixin<T> {

    @Inject(method = "method_72962(Ljava/util/function/Function;)Lnet/minecraft/class_11677;", at = @At("RETURN"))
    private <U> void cb$mapWhole(Function<? super T, ? extends U> fn, CallbackInfoReturnable<class_11677<U>> cir) {
        CbArmorLayers.mapped(this, cir.getReturnValue(), fn);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "method_72960(Lnet/minecraft/class_11677;Lcom/google/common/collect/ImmutableMap$Builder;)V", at = @At("HEAD"))
    private void cb$putWhole(class_11677 definitions, ImmutableMap.Builder roots, CallbackInfo ci) {
        CbArmorLayers.put((class_11677) (Object) this, definitions, roots);
    }
}
