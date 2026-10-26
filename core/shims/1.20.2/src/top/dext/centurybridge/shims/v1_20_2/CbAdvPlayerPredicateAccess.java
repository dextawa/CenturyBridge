package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * PlayerPredicate's restored 1.20.1 fromJson (CbAdvPlayerPredicateJson, a private static), for
 * CbAdvPredicateJson.subPredicateFromJson: the "player" entry of 1.20.1's EntitySubPredicate.fromJson. The restoring
 * mixin applies first (CbVanillaReads.PRIORITY), so the method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_4553.class)
public interface CbAdvPlayerPredicateAccess {

    @Invoker("method_22499")
    static net.minecraft.class_4553 cb$fromJson(com.google.gson.JsonObject json) {
        throw new AssertionError();
    }
}
