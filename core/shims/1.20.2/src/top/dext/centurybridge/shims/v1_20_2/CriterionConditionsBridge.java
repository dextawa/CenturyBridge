package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_195;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.facade.v1_20_2.class_5267;

/**
 * 1.20.1 -> 1.20.2: conditions.toJson(serializer) lost its parameter (the
 * serializer type was deleted; call sites get it facade-renamed by the
 * converter, so this overload's descriptor matches the rewritten call).
 */
@Mixin(class_195.class)
public abstract class CriterionConditionsBridge {

    public JsonObject method_807(class_5267 serializer) {
        return ((class_195) (Object) this).method_807();
    }
}
