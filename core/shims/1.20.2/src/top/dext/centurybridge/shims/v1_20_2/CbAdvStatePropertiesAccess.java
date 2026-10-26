package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * StatePropertiesPredicate's restored 1.20.1 fromJson (CbAdvStatePropertiesJson, a private static), for the consulting
 * route and the restored Block / FluidPredicate readers. The restoring mixin applies first (CbVanillaReads.PRIORITY), so
 * the method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_4559.class)
public interface CbAdvStatePropertiesAccess {

    @Invoker("method_22519")
    static net.minecraft.class_4559 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
