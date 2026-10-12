package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockHopper;

/**
 * 1.20.5's two users of a hopper's suck area, for a mod hopper whose class overrides 1.20.4's
 * getSuckShape(): the shape itself, as 1.20.4 used it, where 1.20.5 would use the single box
 * getSuckAabb() can answer (CbBlockHopper). Every other hopper takes 1.20.5's own path.
 */
@Mixin(net.minecraft.class_2614.class)
public abstract class CbBlockHopperMixin {

    @Shadow
    private static boolean method_11243(net.minecraft.class_1937 level, net.minecraft.class_2338 pos,
            net.minecraft.class_2680 state, net.minecraft.class_2614 hopper, java.util.function.BooleanSupplier action) {
        throw new AssertionError();
    }

    /** getItemsAtAndAbove: every box of the shape, offset as 1.20.4 offset them */
    @Inject(method = "method_11237", at = @At("HEAD"), cancellable = true)
    private static void cb$legacyItemsAtAndAbove(net.minecraft.class_1937 level, net.minecraft.class_2615 hopper,
            CallbackInfoReturnable<java.util.List<net.minecraft.class_1542>> cir) {
        if (hopper instanceof CbBlockHopper.Legacy legacy) {
            cir.setReturnValue(CbBlockHopper.itemsAtAndAbove(level, hopper, legacy.method_11262()));
        }
    }

    /** entityInside: an item entity inside the shape (1.20.4: joinIsNotEmpty with the moved entity box) */
    @Inject(method = "method_11236", at = @At("HEAD"), cancellable = true)
    private static void cb$legacyEntityInside(net.minecraft.class_1937 level, net.minecraft.class_2338 pos,
            net.minecraft.class_2680 state, net.minecraft.class_1297 entity, net.minecraft.class_2614 hopper, CallbackInfo ci) {
        if (hopper instanceof CbBlockHopper.Legacy legacy) {
            ci.cancel();
            if (entity instanceof net.minecraft.class_1542 item && !item.method_6983().method_7960()
                    && net.minecraft.class_259.method_1074(
                        net.minecraft.class_259.method_1078(entity.method_5829().method_989(-pos.method_10263(), -pos.method_10264(), -pos.method_10260())),
                        legacy.method_11262(), net.minecraft.class_247.field_16896)) {
                method_11243(level, pos, state, hopper, () -> net.minecraft.class_2614.method_11247(hopper, item));
            }
        }
    }
}
