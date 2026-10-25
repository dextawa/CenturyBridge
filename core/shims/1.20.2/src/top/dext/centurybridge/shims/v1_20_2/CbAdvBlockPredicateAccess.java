package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * BlockPredicate's restored 1.20.1 fromJson (CbAdvBlockPredicateJson, a private static), for the restored LocationPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so the method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_4550.class)
public interface CbAdvBlockPredicateAccess {

    @Invoker("method_22453")
    static net.minecraft.class_4550 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
