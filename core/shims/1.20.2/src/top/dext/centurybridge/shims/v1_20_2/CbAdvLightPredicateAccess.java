package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * LightPredicate's restored 1.20.1 fromJson (CbAdvLightPredicateJson, a private static), for the restored LocationPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so the method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_4552.class)
public interface CbAdvLightPredicateAccess {

    @Invoker("method_22482")
    static net.minecraft.class_4552 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
