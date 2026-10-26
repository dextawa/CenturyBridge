package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * TagPredicate's restored 1.20.1 fromJson (CbAdvTagPredicateJson, private statics), for the restored DamageSourcePredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_8129.class)
public interface CbAdvTagPredicateAccess {

    @Invoker("method_48966")
    static <T> net.minecraft.class_8129<T> cb$fromJson(com.google.gson.JsonElement json, net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> registry) {
        throw new AssertionError();
    }
}
