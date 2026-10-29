package cb.s212.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.21.1 UseAnim's enum constructor (name, ordinal), as Forge-style enum extensions call it (Kilt UseAnimAccessor) */
@Mixin(net.minecraft.class_1839.class)
public interface UseAnimInvoker {
    @Invoker(value = "<init>", remap = false)
    static net.minecraft.class_1839 cbs212$create(String name, int ordinal) {
        throw new IllegalStateException();
    }
}
