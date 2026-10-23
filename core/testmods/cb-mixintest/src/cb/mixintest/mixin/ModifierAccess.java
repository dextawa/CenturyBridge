package cb.mixintest.mixin;

import java.util.UUID;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * AttributeModifier(UUID, Supplier name, amount, operation): 1.20.3 takes the name itself (step 2
 * superCtorReshapes: the supplier dropped, a placeholder pushed, the supplier handed to a post static)
 */
@Mixin(net.minecraft.class_1322.class)
public interface ModifierAccess {
    @Invoker("<init>")
    static net.minecraft.class_1322 cbtest$newModifier(UUID id, Supplier<String> name, double amount,
                                                      net.minecraft.class_1322.class_1323 operation) {
        throw new AssertionError();
    }
}
