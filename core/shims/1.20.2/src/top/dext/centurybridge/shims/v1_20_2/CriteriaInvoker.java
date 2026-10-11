package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Criteria.register(id, criterion) is private static: the registration bridges call it through here. */
@Mixin(net.minecraft.class_174.class)
public interface CriteriaInvoker {

    @Invoker("method_767")
    static <T extends net.minecraft.class_179<?>> T cb$register(String id, T criterion) {
        throw new AssertionError();
    }
}
