package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * NbtPredicate's restored 1.20.1 fromJson (CbAdvNbtPredicateJson, a private static), for the restored predicate readers.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2105.class)
public interface CbAdvNbtPredicateAccess {

    @Invoker("method_9073")
    static net.minecraft.class_2105 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
