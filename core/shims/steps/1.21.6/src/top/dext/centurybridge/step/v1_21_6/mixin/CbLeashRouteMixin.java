package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_9817;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbLeash;

/**
 * Consults the restored 1.21.5 leash hooks (CbLeashRestoredMixin and the restored vanilla overrides) in 1.21.6's
 * Leashable.tickLeash, only for an entity whose class takes a hook from a converted mod or whose hook declaration a
 * converted mod mixed (CbLeash.routeHandle / routeElastic); every other entity runs 1.21.6's tick exactly as it is.
 * <ul>
 * <li>handleLeashAtDistance: right after 1.21.6's whenLeashedTo (where 1.21.5 asked it, before any branch), with
 *     1.21.5's distanceTo(holder); false cancels the rest of the tick for that entity, as 1.21.5 returned there
 *     (coordinator decision 2026-10-26: that entity's leash behaviour is its own mod's).</li>
 * <li>elasticRangeLeashBehaviour: 1.21.6 runs its checkElasticInteractions; when it pulls (onElasticLeashPull is
 *     next), the motion goes back to what it was before 1.21.6's pull and the restored hook pulls instead, with
 *     1.21.5's distance; 1.21.6's onElasticLeashPull then runs, as 1.21.5's slow-fall check followed the pull (same
 *     body).</li>
 * </ul>
 */
@Mixin(class_9817.class)
public interface CbLeashRouteMixin {

    @Inject(method = "method_60965(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9817;method_71005(Lnet/minecraft/class_1297;)V", shift = At.Shift.AFTER),
        cancellable = true)
    private static void cb$legacyHandleLeash(class_3218 level, class_1297 entity, CallbackInfo ci) {
        if (!CbLeash.routeHandle(entity)) {
            return;
        }
        class_1297 holder = ((class_9817) entity).method_60952();
        if (holder != null && !((CbLeash.Legacy) entity).method_60956(holder, entity.method_5739(holder))) {
            ci.cancel();
        }
    }

    @Inject(method = "method_60965(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9817;method_70995(Lnet/minecraft/class_1297;Lnet/minecraft/class_9817$class_9818;)Z"))
    private static void cb$legacyElasticBefore(class_3218 level, class_1297 entity, CallbackInfo ci) {
        if (CbLeash.routeElastic(entity)) {
            CbLeash.beforePull(entity);
        }
    }

    @Inject(method = "method_60965(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9817;method_71006()V"))
    private static void cb$legacyElasticPull(class_3218 level, class_1297 entity, CallbackInfo ci) {
        class_243 before = CbLeash.takeBeforePull(entity);
        if (before == null) {
            return;
        }
        class_1297 holder = ((class_9817) entity).method_60952();
        if (holder != null) {
            entity.method_18799(before);
            ((CbLeash.Legacy) entity).method_61162(holder, entity.method_5739(holder));
        }
    }
}
