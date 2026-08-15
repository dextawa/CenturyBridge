package top.dext.centurybridge.shims.v1_20_4;

import net.minecraft.class_161;
import net.minecraft.class_163;
import net.minecraft.class_2960;
import net.minecraft.class_8781;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: AdvancementManager.get now returns PlacedAdvancement;
 * the legacy overload unwraps back to the Advancement record.
 */
@Mixin(class_163.class)
public abstract class AdvancementManagerBridge {

    public class_161 method_716(class_2960 id) {
        class_8781 placed = ((class_163) (Object) this).method_716(id);
        return placed == null ? null : placed.method_53647();
    }
}
