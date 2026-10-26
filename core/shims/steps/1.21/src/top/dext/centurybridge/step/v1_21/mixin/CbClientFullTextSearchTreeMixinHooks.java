package top.dext.centurybridge.step.v1_21.mixin;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;

/**
 * The injector into 1.21's own FullTextSearchTree constructor that goes with {@link CbClientFullTextSearchTreeMixin}
 * (its restored 1.20.6 refresh stays there, at {@code CbVanillaReads.PRIORITY}): the constructor's contents and filler
 * are kept for the rebuild.
 */
@Mixin(net.minecraft.class_1126.class)
public abstract class CbClientFullTextSearchTreeMixinHooks<T> {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$made(Function<T, Stream<String>> filler, Function<T, Stream<net.minecraft.class_2960>> ids, List<T> contents,
                         CallbackInfo ci) {
        ((CbPrioritySplit.SearchTreeSource) (Object) this).cb$v1_21$source(filler, contents);
    }
}
