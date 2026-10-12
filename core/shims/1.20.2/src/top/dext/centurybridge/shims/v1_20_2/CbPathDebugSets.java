package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_11;
import net.minecraft.class_9;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.20.1's Path.getOpenSet() / getClosedSet() back (s1misc): the debug node sets a path carries
 * once pathfinding debugging has filled them in, null before -- 1.20.2 keeps them in the path's
 * DebugData record instead.
 */
@Mixin(class_11.class)
public abstract class CbPathDebugSets {

    @Shadow
    public abstract class_11.class_8644 method_52604();

    public class_9[] method_22880() {
        class_11.class_8644 debug = this.method_52604();
        return debug == null ? null : debug.comp_1587();
    }

    public class_9[] method_22881() {
        class_11.class_8644 debug = this.method_52604();
        return debug == null ? null : debug.comp_1588();
    }
}
