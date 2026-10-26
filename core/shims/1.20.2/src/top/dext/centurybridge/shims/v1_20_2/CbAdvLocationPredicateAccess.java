package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * LocationPredicate's restored 1.20.1 fromJson (CbAdvLocationPredicateJson, a private static), for the consulting route.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so the method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2090.class)
public interface CbAdvLocationPredicateAccess {

    @Invoker("method_9021")
    static net.minecraft.class_2090 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
