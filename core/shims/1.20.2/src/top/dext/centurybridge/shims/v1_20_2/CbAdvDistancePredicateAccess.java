package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * DistancePredicate's restored 1.20.1 fromJson (CbAdvDistancePredicateJson, private statics), for the consulting route and the restored EntityPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2025.class)
public interface CbAdvDistancePredicateAccess {

    @Invoker("method_8857")
    static net.minecraft.class_2025 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
