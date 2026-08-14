package top.dext.centurybridge.shims.v1_20_2;

import top.dext.centurybridge.rt.v1_20_2.Trackers;

import net.minecraft.class_161;
import net.minecraft.class_185;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: Advancement became a record; getDisplay() maps to the
 * Optional display component (old contract was nullable).
 * NOT bridgeable here: getId (moved to AdvancementEntry, no longer stored) and
 * children iteration (moved to PlacedAdvancement) -- those stay tombstoned.
 */
@Mixin(class_161.class)
public abstract class AdvancementBridge {

    public class_185 method_686() {
        return ((class_161) (Object) this).comp_1913().orElse(null);
    }

    /** legacy toBuilder(): fully reconstructed from the 1.20.2 record components */
    public class_161.class_162 method_689() {
        class_161 self = (class_161) (Object) this;
        class_161.class_162 builder = class_161.class_162.method_707();
        self.comp_1912().ifPresent(builder::method_708);
        self.comp_1913().ifPresent(builder::method_693);
        builder.method_706(self.comp_1914());
        for (var e : self.comp_1915().entrySet()) {
            builder.method_705(e.getKey(), e.getValue());
        }
        builder.method_34884(self.comp_1916());
        if (self.comp_1917()) {
            builder.method_53634();
        }
        return builder;
    }

    /** legacy getId(): answered from the loader-populated identity map */
    public net.minecraft.class_2960 method_688() {
        net.minecraft.class_8779 entry = Trackers.ADVANCEMENT_ENTRIES.get(this);
        if (entry == null) {
            throw new UnsupportedOperationException(
                "CenturyBridge: advancement id unavailable -- 1.20.2 moved ids to AdvancementEntry"
                + " and this instance was not loaded via ServerAdvancementLoader");
        }
        return entry.comp_1919();
    }
}
