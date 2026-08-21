package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1058;
import org.spongepowered.asm.mixin.Mixin;

/**
 * class_1058 (EntityRenderDispatcher): camera-angle accessors changed from
 * double to float precision at 1.20.2. Old mods pass double literals; the JVM
 * sees descriptor (D)F and finds no match. We add the missing overloads that
 * narrow to float and forward.
 */
@Mixin(class_1058.class)
public abstract class EntityRenderDispatcherBridge {

    /** 1.20.1 getPitch(tickDelta) -> double. 102 call sites. */
    public float method_4580(double tickDelta) {
        return ((class_1058) (Object) this).method_4580((float) tickDelta);
    }

    /** 1.20.1 getYaw(tickDelta) -> double. 102 call sites. */
    public float method_4570(double tickDelta) {
        return ((class_1058) (Object) this).method_4570((float) tickDelta);
    }
}
