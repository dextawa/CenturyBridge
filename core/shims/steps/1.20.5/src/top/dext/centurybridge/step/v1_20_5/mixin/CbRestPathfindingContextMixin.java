package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * A PathfindingContext with no mob: 1.20.4's static path type queries (getBlockPathTypeStatic,
 * checkNeighbourBlocks) and an evaluator not prepared for a mob had none to give. Such a context has no
 * cache and no mob position -- 1.20.5's constructor read both off the mob. 1.20.5 itself always passes
 * a mob, for which nothing changes.
 */
@Mixin(net.minecraft.class_9316.class)
public abstract class CbRestPathfindingContextMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1308;method_37908()Lnet/minecraft/class_1937;"))
    private net.minecraft.class_1937 cbRest$level(net.minecraft.class_1308 mob) {
        return mob == null ? null : mob.method_37908();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1308;method_24515()Lnet/minecraft/class_2338;"))
    private net.minecraft.class_2338 cbRest$position(net.minecraft.class_1308 mob) {
        return mob == null ? null : mob.method_24515();
    }
}
