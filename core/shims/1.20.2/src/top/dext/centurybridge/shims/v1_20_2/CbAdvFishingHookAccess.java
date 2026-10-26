package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * FishingHookPredicate's restored 1.20.1 fromJson (CbAdvFishingHookJson, private statics), for CbAdvPredicateJson.subPredicateFromJson (the "fishing_hook" entry of 1.20.1's EntitySubPredicate.fromJson).
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_4965.class)
public interface CbAdvFishingHookAccess {

    @Invoker("method_26094")
    static net.minecraft.class_4965 cb$fromJson(com.google.gson.JsonObject json) {
        throw new AssertionError();
    }
}
