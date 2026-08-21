package top.dext.centurybridge.shims.v1_21_4;

import java.util.function.Consumer;
import net.minecraft.class_161;
import net.minecraft.class_8779;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 port of the Advancement Builder bridges. The 1.20.3 codec wave
 * removed the legacy-conditions criterion wrapper (method_709 -> ledgered)
 * and Advancement.toJson (method_698 -> ledgered); parent/build survive.
 */
@Mixin(class_161.class_162.class)
public abstract class AdvancementBuilderBridge {

    /** legacy parent(Advancement): resolve the entry via the tracker map */
    public class_161.class_162 method_701(class_161 parent) {
        class_8779 entry = top.dext.centurybridge.rt.v1_21_4.Trackers.ADVANCEMENT_ENTRIES.get(parent);
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
}
