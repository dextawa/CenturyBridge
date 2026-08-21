package top.dext.centurybridge.shims.v26_2;

import java.util.function.Consumer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 port of the Advancement Builder bridges. The 1.20.3 codec wave
 * removed the legacy-conditions criterion wrapper (method_709 -> ledgered)
 * and Advancement.toJson (method_698 -> ledgered); parent/build survive.
 */
@Mixin(net.minecraft.advancements.Advancement.Builder.class)
public abstract class AdvancementBuilderBridge {

    /** legacy parent(Advancement): resolve the entry via the tracker map */
    public net.minecraft.advancements.Advancement.Builder parent(net.minecraft.advancements.Advancement parent) {
        net.minecraft.advancements.AdvancementHolder entry = top.dext.centurybridge.rt.v26_2.Trackers.ADVANCEMENT_ENTRIES.get(parent);
        if (entry == null) {
            throw new UnsupportedOperationException(
                "CenturyBridge: parent advancement has no known entry (not loaded via ServerAdvancementLoader)");
        }
        return ((net.minecraft.advancements.Advancement.Builder) (Object) this).parent(entry);
    }

    public net.minecraft.advancements.Advancement save(Consumer<net.minecraft.advancements.Advancement> exporter, String id) {
        net.minecraft.advancements.AdvancementHolder entry = ((net.minecraft.advancements.Advancement.Builder) (Object) this)
            .save(e -> exporter.accept(e.value()), id);
        return entry.value();
    }
}
