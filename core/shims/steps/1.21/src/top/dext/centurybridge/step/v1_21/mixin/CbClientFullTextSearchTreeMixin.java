package top.dext.centurybridge.step.v1_21.mixin;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * FullTextSearchTree.refresh() of 1.20.6: the text index rebuilt from the contents and the filler (the tooltip
 * lines change with the language). 1.21 builds it once in the constructor; the constructor's contents and filler
 * are kept for the rebuild (CbClientFullTextSearchTreeMixinHooks, default priority).
 */
@Mixin(value = net.minecraft.class_1126.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientFullTextSearchTreeMixin<T> implements CbPrioritySplit.SearchTreeSource {

    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_1129<T> field_39198;

    @Unique
    private List<T> cb$contents;

    @Unique
    private Function<T, Stream<String>> cb$filler;

    @Override
    @SuppressWarnings("unchecked")
    public void cb$v1_21$source(Function<?, ?> filler, List<?> contents) {
        this.cb$filler = (Function<T, Stream<String>>) filler;
        this.cb$contents = (List<T>) contents;
    }

    /** refresh() */
    public void method_4799() {
        this.field_39198 = net.minecraft.class_1129.plainText(this.cb$contents, this.cb$filler);
    }
}
