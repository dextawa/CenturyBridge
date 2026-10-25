package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * MobEffectInstancePredicate's restored 1.20.1 fromJson (CbAdvMobEffectInstanceJson, private statics), for the restored MobEffectsPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2102.class_2103.class)
public interface CbAdvMobEffectInstanceAccess {

    @Invoker("method_9070")
    static net.minecraft.class_2102.class_2103 cb$fromJson(com.google.gson.JsonObject json) {
        throw new AssertionError();
    }
}
