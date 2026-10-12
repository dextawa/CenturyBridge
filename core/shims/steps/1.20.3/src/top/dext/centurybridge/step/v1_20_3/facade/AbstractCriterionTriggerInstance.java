package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonObject;
import java.util.Optional;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvLegacy;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvPredicates;

/**
 * Facade for 1.20.2 AbstractCriterionTriggerInstance (class_195), gone in 1.20.3 (vanilla's
 * trigger instances became records implementing SimpleCriterionTrigger.SimpleInstance
 * directly). A 1.20.2 mod's trigger instance extends it; the facade keeps that instance a
 * 1.20.3 SimpleInstance, so vanilla's SimpleCriterionTrigger.trigger reads its player predicate
 * through player() -- which answers playerPredicate(), the 1.20.2 method the instance may
 * override.
 *
 * validate(validator), new in 1.20.3, does nothing here: a 1.20.2 instance is validated while
 * it is deserialized (its trigger's createInstance reads predicates through the
 * DeserializationContext, which validates and reports their conditions, as 1.20.2 did), and
 * 1.20.2 had no later validation pass to run again.
 */
public abstract class AbstractCriterionTriggerInstance implements net.minecraft.class_4558.class_8788, CbAdvLegacy.Instance {

    /** player */
    public final Optional<net.minecraft.class_5258> field_24381;

    public AbstractCriterionTriggerInstance(Optional<net.minecraft.class_5258> player) {
        this.field_24381 = player;
    }

    /** playerPredicate() */
    public Optional<net.minecraft.class_5258> method_27790() {
        return field_24381;
    }

    /** 1.20.3 SimpleInstance.player(): what vanilla reads -- the 1.20.2 playerPredicate() */
    @Override
    public Optional<net.minecraft.class_5258> comp_2029() {
        return method_27790();
    }

    /** serializeToJson(): the player predicate under "player", as 1.20.2 wrote it */
    @Override
    public JsonObject method_807() {
        JsonObject json = new JsonObject();
        field_24381.ifPresent(player -> json.add("player", CbAdvPredicates.method_27804(player)));
        return json;
    }

    /** 1.20.3 validate(validator): see the class comment */
    @Override
    public void method_54938(net.minecraft.class_8944 validator) {
    }
}
