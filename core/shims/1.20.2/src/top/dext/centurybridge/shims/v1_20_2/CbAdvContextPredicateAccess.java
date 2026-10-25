package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * ContextAwarePredicate's restored 1.20.1 fromElement (CbAdvContextPredicateJson, private statics), for the restored EntityPredicate.fromElement.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_5258.class)
public interface CbAdvContextPredicateAccess {

    @Invoker("method_27807")
    static net.minecraft.class_5258 cb$fromElement(String name, net.minecraft.class_5257 context, com.google.gson.JsonElement json, net.minecraft.class_176 params) {
        throw new AssertionError();
    }
}
