package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_161;
import net.minecraft.class_5257;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Advancement.Builder's restored 1.20.1 fromJson (CbAdvBuilderJson, a private static), for the consulting route
 * (CbAdvBuilders.consult). The restoring mixin applies first (CbVanillaReads.PRIORITY).
 */
@Mixin(class_161.class_162.class)
public interface CbAdvBuilderAccess {

    @Invoker("method_692")
    static class_161.class_162 cb$fromJson(JsonObject json, class_5257 context) {
        throw new AssertionError();
    }
}
