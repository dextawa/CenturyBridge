package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * DamageSourcePredicate's restored 1.20.1 fromJson (CbAdvDamageSourcePredicateJson, private statics), for the consulting route and the restored DamagePredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2022.class)
public interface CbAdvDamageSourcePredicateAccess {

    @Invoker("method_8846")
    static net.minecraft.class_2022 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
