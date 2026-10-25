package top.dext.centurybridge.shims.v1_20_2;

import java.util.function.Consumer;
import net.minecraft.class_161;
import net.minecraft.class_184;
import net.minecraft.class_8779;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2 Advancement rework, Builder side. Work order: x70 mods.
 *
 * - criterion(name, conditions): conditions are now paired with their trigger
 *   in AdvancementCriterion (CbAdvCriteria.criterionOf).
 * - build(consumer, id): return type moved to AdvancementEntry; old overload
 *   unwraps entry.value() both for the return and the consumer.
 */
@Mixin(value = class_161.class_162.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class AdvancementBuilderBridge {

    /**
     * legacy criterion(name, conditions): 1.20.1 paired the conditions with the trigger their id names when
     * the advancement was loaded; 1.20.2's builder takes the pair (CbAdvCriteria.criterionOf: the trigger the
     * instance belongs to, as 1.20.1's PlayerAdvancements found it)
     */
    public class_161.class_162 method_709(String name, class_184 conditions) {
        return ((class_161.class_162) (Object) this).method_705(name,
            top.dext.centurybridge.rt.v1_20_2.CbAdvCriteria.criterionOf(conditions));
    }

    /** legacy parent(Advancement): the parent's holder (CbAdvNodes.holderOf) */
    public class_161.class_162 method_701(class_161 parent) {
        return ((class_161.class_162) (Object) this).method_701(top.dext.centurybridge.rt.v1_20_2.CbAdvNodes.holderOf(parent));
    }

    public class_161 method_694(Consumer<class_161> exporter, String id) {
        class_8779 entry = ((class_161.class_162) (Object) this)
            .method_694(e -> exporter.accept(e.comp_1920()), id);
        return entry.comp_1920();
    }

    /** legacy Builder.toJson(): build with a scratch id, serialize via 1.20.2 Advancement.toJson */
    public com.google.gson.JsonObject method_698() {
        class_8779 entry = ((class_161.class_162) (Object) this)
            .method_694(e -> { }, "centurybridge_tojson");
        return entry.comp_1920().method_53621();
    }
}
