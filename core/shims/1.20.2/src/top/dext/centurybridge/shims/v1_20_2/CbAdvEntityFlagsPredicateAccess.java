package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * EntityFlagsPredicate's restored 1.20.1 fromJson (CbAdvEntityFlagsPredicateJson, private statics), for the restored EntityPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2040.class)
public interface CbAdvEntityFlagsPredicateAccess {

    @Invoker("method_8893")
    static net.minecraft.class_2040 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
