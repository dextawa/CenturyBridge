package cb.s212n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.21.2 UseAnim's enum constructor (name, ordinal, id, serializedName) */
@Mixin(net.minecraft.class_1839.class)
public interface NativeUseAnimInvoker {
    @Invoker(value = "<init>", remap = false)
    static net.minecraft.class_1839 cbs212n$create(String name, int ordinal, int id, String serializedName) {
        throw new IllegalStateException();
    }
}
