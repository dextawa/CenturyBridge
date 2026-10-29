package top.dext.centurybridge.step.v1_21.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_21.rt.CbDragonRays;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * EnderDragonRenderer.method_61158 (1.21's death rays, CbDragonRays): while a converted mod mixes a restored 1.20.6 ray
 * helper (CbMixedMembers), each addVertex(pose, vector) is held back and its setColor(color) draws the vertex through the
 * helper 1.20.6 drew it with (which calls addVertex(matrix, x, y, z), setColor and endVertex itself). Unmixed, 1.21's calls
 * run untouched. Priority CbVanillaReads.ROUTE_FIRST: the innermost wrapper of both calls, so a native 1.21 mod's
 * @WrapOperation of them wraps the route and runs either way.
 */
@Mixin(value = net.minecraft.class_895.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbRenderDragonRaysRouteMixin {

    @WrapOperation(method = "method_61158(Lnet/minecraft/class_4587;FLnet/minecraft/class_4588;)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4588;method_61032(Lnet/minecraft/class_4587$class_4665;Lorg/joml/Vector3f;)Lnet/minecraft/class_4588;"))
    private static net.minecraft.class_4588 cb$v1_21$rayVertex(net.minecraft.class_4588 consumer, net.minecraft.class_4587.class_4665 pose,
                                                               org.joml.Vector3f v, Operation<net.minecraft.class_4588> original) {
        if (CbDragonRays.routed()) {
            CbDragonRays.begin(pose, v);
            return consumer;
        }
        return original.call(consumer, pose, v);
    }

    @WrapOperation(method = "method_61158(Lnet/minecraft/class_4587;FLnet/minecraft/class_4588;)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4588;method_39415(I)Lnet/minecraft/class_4588;"))
    private static net.minecraft.class_4588 cb$v1_21$rayColor(net.minecraft.class_4588 consumer, int color,
                                                              Operation<net.minecraft.class_4588> original) {
        if (CbDragonRays.end(consumer, color)) {
            return consumer;
        }
        return original.call(consumer, color);
    }
}
