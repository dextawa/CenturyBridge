package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * FluidPredicate's restored 1.20.1 fromJson (CbAdvFluidPredicateJson, a private static), for the restored LocationPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so the method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_4551.class)
public interface CbAdvFluidPredicateAccess {

    @Invoker("method_22474")
    static net.minecraft.class_4551 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
