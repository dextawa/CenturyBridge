package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_2338;
import net.minecraft.class_2342;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: BlockPointer became a record; the old accessors map to
 * record components. Careful: old method_10120 returned the BLOCK STATE, not
 * the position. Work order: L3 x~20 each.
 */
@Mixin(class_2342.class)
public abstract class BlockPointerBridge {

    private class_2342 self() {
        return (class_2342) (Object) this;
    }

    public class_2338 method_10122() {
        return self().comp_1968();
    }

    public class_2680 method_10120() {
        return self().comp_1969();
    }

    @SuppressWarnings("unchecked")
    public <T extends class_2586> T method_10121() {
        return (T) (class_2586) self().comp_1970();
    }

    public class_3218 method_10207() {
        return self().comp_1967();
    }
}
