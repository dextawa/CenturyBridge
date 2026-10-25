package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * EnchantmentPredicate's restored 1.20.1 fromJson / fromJsonArray (CbAdvEnchantmentPredicateJson, private statics), for the restored ItemPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2035.class)
public interface CbAdvEnchantmentPredicateAccess {

    @Invoker("method_8882")
    static net.minecraft.class_2035 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }

    @Invoker("method_8879")
    static net.minecraft.class_2035[] cb$fromJsonArray(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
