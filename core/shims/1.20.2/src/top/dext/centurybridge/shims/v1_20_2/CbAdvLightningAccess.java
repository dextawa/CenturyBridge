package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * LightningBoltPredicate's restored 1.20.1 fromJson (CbAdvLightningJson, private statics), for CbAdvPredicateJson.subPredicateFromJson (the "lightning" entry of 1.20.1's EntitySubPredicate.fromJson).
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_6404.class)
public interface CbAdvLightningAccess {

    @Invoker("method_37238")
    static net.minecraft.class_6404 cb$fromJson(com.google.gson.JsonObject json) {
        throw new AssertionError();
    }
}
