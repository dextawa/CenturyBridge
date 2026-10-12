package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.gson.JsonObject;
import java.util.Optional;
import top.dext.centurybridge.step.v1_20_3.facade.DeserializationContext;

/**
 * SimpleCriterionTrigger.createInstance(json, player, context): protected abstract in 1.20.2, the step
 * the final createInstance(json, context) took after reading the "player" predicate itself. A 1.20.2
 * subclass's own this.createInstance(json, player, context) -- it inherits the method, so the object
 * is a subclass that implements it or a vanilla trigger below it -- reaches:
 * <ul>
 * <li>a 1.20.2 implementation, its own method (CbAdvLegacy.SimpleTrigger, s2adv's adapter);</li>
 * <li>a vanilla 1.20.3 trigger, its codec: 1.20.2's vanilla createInstance read every condition from the
 *     JSON but the player, which it took as given -- so the JSON is decoded with the given player in its
 *     "player" entry (absent when the player is empty, as an absent entry reads), through the same decode
 *     and validation as createInstance(json, context) (CbAdvCriteria.createInstance).</li>
 * </ul>
 */
public final class CbAdvSimpleTriggers {

    /** trigger.createInstance(json, player, context) */
    public static net.minecraft.class_4558.class_8788 method_27854(net.minecraft.class_4558<?> trigger, JsonObject json,
            Optional<net.minecraft.class_5258> player, DeserializationContext context) {
        if (trigger instanceof CbAdvLegacy.SimpleTrigger legacy) {
            return legacy.method_27854(json, player, context);
        }
        JsonObject withPlayer = json.deepCopy();
        withPlayer.remove("player");
        player.ifPresent(p -> withPlayer.add("player", CbAdvPredicates.method_27804(p)));
        return (net.minecraft.class_4558.class_8788) CbAdvCriteria.createInstance(trigger, withPlayer, context);
    }

    private CbAdvSimpleTriggers() {
    }
}
