package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestSpawn;

/**
 * EntityType.create(level, consumer, pos, reason, ..): where 1.20.4's create(level, tag, consumer, ..)
 * handed the new mob's finalizeSpawn the tag it was given, 1.20.5's passes none. The create takes the
 * tag a 1.20.4 caller handed over at its start and pairs it with that finalizeSpawn call
 * (rt/CbRestSpawn); the pairing is cleared after the call.
 */
@Mixin(net.minecraft.class_1299.class)
public abstract class CbRestEntityTypeMixin {

    @Inject(method = "method_5888", at = @At("HEAD"))
    private void cbRest$beginCreate(net.minecraft.class_3218 level, java.util.function.Consumer<?> consumer, net.minecraft.class_2338 pos,
                                    net.minecraft.class_3730 reason, boolean alignPosition, boolean invertY,
                                    CallbackInfoReturnable<net.minecraft.class_1297> cir) {
        CbRestSpawn.cbBeginCreate();
    }

    @Redirect(method = "method_5888", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1308;method_5943(Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;)Lnet/minecraft/class_1315;"))
    private net.minecraft.class_1315 cbRest$finalizeSpawn(net.minecraft.class_1308 mob, net.minecraft.class_5425 level,
                                                         net.minecraft.class_1266 difficulty, net.minecraft.class_3730 reason,
                                                         net.minecraft.class_1315 groupData) {
        CbRestSpawn.cbWithTag(difficulty, CbRestSpawn.cbCreateTag());
        try {
            return mob.method_5943(level, difficulty, reason, groupData);
        } finally {
            CbRestSpawn.cbEndCall(difficulty);
        }
    }
}
