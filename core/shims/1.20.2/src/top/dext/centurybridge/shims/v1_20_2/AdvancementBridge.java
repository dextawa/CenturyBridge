package top.dext.centurybridge.shims.v1_20_2;

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
}
