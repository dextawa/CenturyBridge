package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1547;
import net.minecraft.class_1665;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** AbstractSkeleton members the layer reaches that 1.21 keeps protected. */
@Mixin(class_1547.class)
public interface CbEntitySkeletonInvoker {

    @Invoker("method_6996")
    class_1665 cb$getArrow(class_1799 ammo, float power, class_1799 weapon);
}
