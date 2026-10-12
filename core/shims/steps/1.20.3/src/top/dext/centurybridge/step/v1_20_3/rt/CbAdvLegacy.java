package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonObject;
import java.util.Optional;
import top.dext.centurybridge.step.v1_20_3.facade.DeserializationContext;

/**
 * The 1.20.2 criterion shapes a converted mod class still speaks, as adapter interfaces
 * (adapterHooks add them to the mod class, whose own methods implement them): the bridges
 * reach a 1.20.2 trigger's or instance's own methods through these, typed.
 *
 * 1.20.3 moved criteria to codecs: CriterionTrigger.createInstance(json, context) became
 * codec(), CriterionTriggerInstance.serializeToJson() became validate(validator). A 1.20.2
 * mod's trigger still decodes its conditions with createInstance, and its instance still
 * writes them with serializeToJson; the codec synthesized into the mod class
 * (CbAdvCriteria) is built from exactly those two methods.
 */
public final class CbAdvLegacy {

    /** 1.20.2 CriterionTriggerInstance (class_184): serializeToJson() */
    public interface Instance {
        JsonObject method_807();
    }

    /** 1.20.2 CriterionTrigger (class_179): createInstance(json, context) */
    public interface Trigger {
        net.minecraft.class_184 method_795(JsonObject json, DeserializationContext context);
    }

    /** 1.20.2 SimpleCriterionTrigger (class_4558): createInstance(json, player, context) */
    public interface SimpleTrigger {
        net.minecraft.class_4558.class_8788 method_27854(JsonObject json, Optional<net.minecraft.class_5258> player,
                DeserializationContext context);
    }

    private CbAdvLegacy() {
    }
}
