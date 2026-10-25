package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * SlimePredicate's restored 1.20.1 fromJson (CbAdvSlimeJson, private statics), for CbAdvPredicateJson.subPredicateFromJson (the "slime" entry of 1.20.1's EntitySubPredicate.fromJson).
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_7383.class)
public interface CbAdvSlimeAccess {

    @Invoker("method_43158")
    static net.minecraft.class_7383 cb$fromJson(com.google.gson.JsonObject json) {
        throw new AssertionError();
    }
}
