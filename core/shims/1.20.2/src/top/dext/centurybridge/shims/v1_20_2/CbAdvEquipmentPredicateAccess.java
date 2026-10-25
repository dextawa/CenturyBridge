package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * EntityEquipmentPredicate's restored 1.20.1 fromJson (CbAdvEquipmentPredicateJson, private statics), for the restored EntityPredicate reader.
 * The restoring mixin applies first (CbVanillaReads.PRIORITY), so each method exists when its invoker is made.
 */
@Mixin(net.minecraft.class_3735.class)
public interface CbAdvEquipmentPredicateAccess {

    @Invoker("method_16224")
    static net.minecraft.class_3735 cb$fromJson(com.google.gson.JsonElement json) {
        throw new AssertionError();
    }
}
