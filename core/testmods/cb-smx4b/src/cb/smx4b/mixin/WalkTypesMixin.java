package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.4 WalkNodeEvaluator.getBlockPathTypes(...) and evaluateBlockPathType(level, pos, type): counted; while
 * Driver.walkBlockAll is set every evaluated cell is BLOCKED (no path)
 */
@Mixin(net.minecraft.class_14.class)
public abstract class WalkTypesMixin {
    @Inject(method = "method_64(Lnet/minecraft/class_1922;IIILjava/util/EnumSet;Lnet/minecraft/class_7;Lnet/minecraft/class_2338;)Lnet/minecraft/class_7;",
        at = @At("HEAD"), remap = false)
    private void cbsmx4b$boxTypes(net.minecraft.class_1922 level, int x, int y, int z, java.util.EnumSet<net.minecraft.class_7> types,
                                  net.minecraft.class_7 current, net.minecraft.class_2338 mobPos, CallbackInfoReturnable<net.minecraft.class_7> cir) {
        Driver.walkBoxAsked++;
    }

    @Inject(method = "method_61(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_7;)Lnet/minecraft/class_7;",
        at = @At("RETURN"), cancellable = true, remap = false)
    private void cbsmx4b$evaluate(net.minecraft.class_1922 level, net.minecraft.class_2338 pos, net.minecraft.class_7 type,
                                  CallbackInfoReturnable<net.minecraft.class_7> cir) {
        Driver.walkEvaluated++;
        if (Driver.walkBlockAll) {
            cir.setReturnValue(net.minecraft.class_7.field_22);
        }
    }
}
