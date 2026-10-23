package top.dext.centurybridge.step.v1_21.mixin;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * FullTextSearchTree.refresh() of 1.20.6: the text index rebuilt from the contents and the filler (the tooltip
 * lines change with the language). 1.21 builds it once in the constructor; the constructor's contents and filler
 * are kept for the rebuild.
 */
@Mixin(net.minecraft.class_1126.class)
public abstract class CbClientFullTextSearchTreeMixin<T> {

    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_1129<T> field_39198;

    @Unique
    private List<T> cb$contents;

    @Unique
    private Function<T, Stream<String>> cb$filler;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$made(Function<T, Stream<String>> filler, Function<T, Stream<net.minecraft.class_2960>> ids, List<T> contents,
                         CallbackInfo ci) {
        this.cb$filler = filler;
        this.cb$contents = contents;
    }

    /** refresh() */
    public void method_4799() {
        this.field_39198 = net.minecraft.class_1129.plainText(this.cb$contents, this.cb$filler);
    }
}
