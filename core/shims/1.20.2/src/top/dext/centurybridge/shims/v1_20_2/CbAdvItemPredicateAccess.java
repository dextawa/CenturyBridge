package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * ItemPredicate's restored 1.20.1 fromJson / fromJsonArray (CbAdvItemPredicateJson, private statics), for the consulting route.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2073.class)
public interface CbAdvItemPredicateAccess {

    @Invoker("method_8969")
    static net.minecraft.class_2073 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }

    @Invoker("method_8972")
    static net.minecraft.class_2073[] cb$fromJsonArray(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
