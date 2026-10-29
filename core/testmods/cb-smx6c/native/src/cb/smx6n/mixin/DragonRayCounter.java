package cb.smx6n.mixin;

import cb.smx6n.Driver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** 1.21's EnderDragonRenderer death rays (method_61158): a native @WrapOperation of each ray vertex's addVertex, counting. */
@Mixin(net.minecraft.class_895.class)
public abstract class DragonRayCounter {
    @WrapOperation(method = "method_61158", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4588;method_61032(Lnet/minecraft/class_4587$class_4665;Lorg/joml/Vector3f;)Lnet/minecraft/class_4588;"))
    private static net.minecraft.class_4588 cb$vertex(net.minecraft.class_4588 consumer, net.minecraft.class_4587.class_4665 pose,
                                                      org.joml.Vector3f v, Operation<net.minecraft.class_4588> original) {
        Driver.RAY_VERTS.incrementAndGet();
        return original.call(consumer, pose, v);
    }
}
