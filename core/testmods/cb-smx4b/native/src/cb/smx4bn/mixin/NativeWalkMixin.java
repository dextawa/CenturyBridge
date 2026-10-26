package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.5 WalkNodeEvaluator.getPathTypeOfMob and getPathTypeWithinMobBB, counted */
@Mixin(net.minecraft.class_14.class)
public abstract class NativeWalkMixin {
    @Inject(method = "method_25(Lnet/minecraft/class_9316;IIILnet/minecraft/class_1308;)Lnet/minecraft/class_7;", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$ofMob(net.minecraft.class_9316 context, int x, int y, int z, net.minecraft.class_1308 mob,
                                CallbackInfoReturnable<net.minecraft.class_7> cir) {
        cb.smx4bn.Driver.walkOfMob++;
    }

    @Inject(method = "method_57090(Lnet/minecraft/class_9316;III)Ljava/util/Set;", at = @At("HEAD"), remap = false)
    private void cbsmx4bn$within(net.minecraft.class_9316 context, int x, int y, int z,
                                 CallbackInfoReturnable<java.util.Set<net.minecraft.class_7>> cir) {
        cb.smx4bn.Driver.walkWithin++;
    }
}
