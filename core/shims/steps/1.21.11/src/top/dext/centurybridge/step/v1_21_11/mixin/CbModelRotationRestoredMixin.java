package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10821;
import net.minecraft.class_1086;
import net.minecraft.class_4990;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbModelRotationQuadrantHolder;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 ModelRotation's instance members on 1.21.11's ModelRotation (javap -c -p both code jars): the x / y Quadrants
 * the enum constant was built from (field_56976 / field_56977, set in its constructor) and rotation() (method_35808,
 * answering field_23374, the group 1.21.11 keeps in field_64679). The Quadrants are those of the 1.21.10 constant with
 * the instance's group (rt/CbModelRotationQuadrants), null for the 32 groups no 1.21.10 constant had. Filled at the end
 * of 1.21.11's constructor by mixin/CbModelRotationMixinHooks (default priority); nothing 1.21.11 runs reads them. The statics (constants, field_56975, values / valueOf / by)
 * cannot be merged by a mixin (Mixin refuses non-private statics) and are served by rt/CbModelRotations.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_1086.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbModelRotationRestoredMixin implements CbModelRotationQuadrantHolder {

    /** 1.21.10 ModelRotation.x (Quadrant) */
    private class_10821 field_56976;
    /** 1.21.10 ModelRotation.y (Quadrant) */
    private class_10821 field_56977;

    @Override
    public void cb$v1_21_11$setQuadrants(class_10821 x, class_10821 y) {
        field_56976 = x;
        field_56977 = y;
    }

    /** 1.21.10 ModelRotation.rotation() */
    public class_4990 method_35808() {
        return ((class_1086) (Object) this).field_64679;
    }
}
