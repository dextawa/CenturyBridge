package cb.smx6n.mixin;

import cb.smx6n.Driver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1058;
import net.minecraft.class_2350;
import net.minecraft.class_3665;
import net.minecraft.class_777;
import net.minecraft.class_783;
import net.minecraft.class_789;
import net.minecraft.class_793;
import net.minecraft.class_796;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** 1.21's BlockModel.bakeFace: a native @WrapOperation of its FaceBakery.bakeQuad call, counting and calling through. */
@Mixin(class_793.class)
public abstract class BakeFaceWrapCounter {
    @WrapOperation(method = "method_3447", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_796;method_3468(Lorg/joml/Vector3f;"
        + "Lorg/joml/Vector3f;Lnet/minecraft/class_783;Lnet/minecraft/class_1058;Lnet/minecraft/class_2350;Lnet/minecraft/class_3665;"
        + "Lnet/minecraft/class_789;Z)Lnet/minecraft/class_777;"))
    private static class_777 cb$wrap(class_796 bakery, Vector3f from, Vector3f to, class_783 face, class_1058 sprite, class_2350 direction,
                                     class_3665 state, class_789 rotation, boolean shade, Operation<class_777> original) {
        Driver.FACE_WRAPS.incrementAndGet();
        return original.call(bakery, from, to, face, sprite, direction, state, rotation, shade);
    }
}
