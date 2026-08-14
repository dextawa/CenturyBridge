package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonObject;
import net.minecraft.class_195;
import net.minecraft.class_4558;
import net.minecraft.class_5257;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: AbstractCriterion.conditionsFromJson's return ERASURE
 * changed (T extends AbstractCriterionConditions -> T extends the new
 * Conditions interface). The runtime object is the same conditions instance,
 * so the legacy-erasure overload just delegates and casts. Work order: x14.
 */
@Mixin(class_4558.class)
public abstract class AbstractCriterionBridge {

    public class_195 method_27853(JsonObject json, class_5257 deserializer) {
        Object conditions = ((class_4558<?>) (Object) this).method_27853(json, deserializer);
        return (class_195) conditions;
    }
}
