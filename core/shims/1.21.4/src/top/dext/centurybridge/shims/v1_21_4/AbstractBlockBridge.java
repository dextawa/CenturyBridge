package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4538;
import net.minecraft.class_4970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * getPickStack narrowed its world parameter at 1.20.3 and went protected with
 * a trailing includeData flag at 1.21.4. The legacy overload lives here on
 * class_4970 -- where the modern method is DECLARED -- because @Shadow does
 * not walk the hierarchy; legacy call sites naming class_2248 still resolve
 * up to this overload. Legacy pick never carried block-entity data.
 */
@Mixin(class_4970.class)
public abstract class AbstractBlockBridge {

    @Shadow
    protected abstract class_1799 method_9574(class_4538 world, class_2338 pos, class_2680 state, boolean includeData);

    /** legacy getPickStack shape (world param narrowed at 1.20.3) */
    public class_1799 method_9574(class_1922 world, class_2338 pos, class_2680 state) {
        return this.method_9574((class_4538) world, pos, state, false);
    }
}
