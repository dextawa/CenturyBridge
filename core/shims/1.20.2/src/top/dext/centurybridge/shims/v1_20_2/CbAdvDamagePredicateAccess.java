package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * DamagePredicate's restored 1.20.1 fromJson (CbAdvDamagePredicateJson, private statics), for the consulting route.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2019.class)
public interface CbAdvDamagePredicateAccess {

    @Invoker("method_8839")
    static net.minecraft.class_2019 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
