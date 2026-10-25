package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * EntityPredicate's restored 1.20.1 readers (CbAdvEntityPredicateJson, private statics), for the consulting route.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_2048.class)
public interface CbAdvEntityPredicateAccess {

    @Invoker("method_8913")
    static net.minecraft.class_2048 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }

    @Invoker("method_51705")
    static net.minecraft.class_5258 cb$fromJsonNamed(com.google.gson.JsonObject json, String name, net.minecraft.class_5257 context) {
        throw new AssertionError();
    }

    @Invoker("method_51707")
    static net.minecraft.class_5258[] cb$fromJsonArray(com.google.gson.JsonObject json, String name, net.minecraft.class_5257 context) {
        throw new AssertionError();
    }
}
