package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * MobEffectsPredicate's restored 1.20.1 fromJson (CbAdvMobEffectsPredicateJson, private statics), for the consulting route and the restored EntityPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2102.class)
public interface CbAdvMobEffectsPredicateAccess {

    @Invoker("method_9064")
    static net.minecraft.class_2102 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
