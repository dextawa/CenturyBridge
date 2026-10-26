package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * EntityTypePredicate's restored 1.20.1 fromJson (CbAdvEntityTypePredicateJson, private statics), for the restored EntityPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2050.class)
public interface CbAdvEntityTypePredicateAccess {

    @Invoker("method_8928")
    static net.minecraft.class_2050 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
