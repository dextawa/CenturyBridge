package top.dext.centurybridge.shims.v1_20_2;

import java.util.function.Consumer;
import net.minecraft.class_161;
import net.minecraft.class_174;
import net.minecraft.class_175;
import net.minecraft.class_179;
import net.minecraft.class_184;
import net.minecraft.class_2960;
import net.minecraft.class_8779;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2 Advancement rework, Builder side. Work order: x70 mods.
 *
 * - criterion(name, conditions): conditions are now paired with their trigger
 *   in AdvancementCriterion. Legacy conditions objects still carry getId()
 *   (method_806) in their own class even though the interface lost it, so we
 *   recover the trigger via reflection + Criteria lookup.
 * - build(consumer, id): return type moved to AdvancementEntry; old overload
 *   unwraps entry.value() both for the return and the consumer.
 */
@Mixin(class_161.class_162.class)
public abstract class AdvancementBuilderBridge {

    public class_161.class_162 method_709(String name, class_184 conditions) {
        class_2960 id;
        try {
            id = (class_2960) conditions.getClass().getMethod("method_806").invoke(conditions);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                "CenturyBridge: legacy criterion conditions " + conditions.getClass().getName()
                + " does not expose method_806 (getId); cannot recover trigger", e);
        }
        class_179<?> trigger = class_174.method_765(id);
        if (trigger == null) {
            throw new IllegalStateException("CenturyBridge: unknown criterion trigger " + id);
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        class_175<?> criterion = new class_175((class_179) trigger, conditions);
        return ((class_161.class_162) (Object) this).method_705(name, criterion);
    }

    /** legacy parent(Advancement): resolve the entry via the tracker map */
    public class_161.class_162 method_701(class_161 parent) {
        class_8779 entry = top.dext.centurybridge.rt.v1_20_2.Trackers.ADVANCEMENT_ENTRIES.get(parent);
        if (entry == null) {
            throw new UnsupportedOperationException(
                "CenturyBridge: parent advancement has no known entry (not loaded via ServerAdvancementLoader)");
        }
        return ((class_161.class_162) (Object) this).method_701(entry);
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
