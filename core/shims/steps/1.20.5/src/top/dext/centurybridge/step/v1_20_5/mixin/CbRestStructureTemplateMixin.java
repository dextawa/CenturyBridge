package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestSpawn;

/**
 * StructureTemplate's entity placement: 1.20.4 handed each placed mob's finalizeSpawn the template
 * entity's NBT (with its new position, without its UUID) -- the compound it had just made the entity
 * from; 1.20.5 passes none. The compound is noted when the entity is made from it and paired with that
 * mob's finalizeSpawn call (rt/CbRestSpawn), the pairing cleared after the call.
 */
@Mixin(net.minecraft.class_3499.class)
public abstract class CbRestStructureTemplateMixin {

    @Shadow
    private static java.util.Optional<net.minecraft.class_1297> method_17916(net.minecraft.class_5425 level, net.minecraft.class_2487 nbt) {
        throw new AssertionError();
    }

    @Redirect(method = "method_15179", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3499;method_17916(Lnet/minecraft/class_5425;Lnet/minecraft/class_2487;)Ljava/util/Optional;"))
    private java.util.Optional<net.minecraft.class_1297> cbRest$entityFrom(net.minecraft.class_5425 level, net.minecraft.class_2487 nbt) {
        CbRestSpawn.cbStructureEntity(nbt);
        return method_17916(level, nbt);
    }

    @Redirect(method = "method_17917", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1308;method_5943(Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;)Lnet/minecraft/class_1315;"))
    private static net.minecraft.class_1315 cbRest$finalizeSpawn(net.minecraft.class_1308 mob, net.minecraft.class_5425 level,
                                                                net.minecraft.class_1266 difficulty, net.minecraft.class_3730 reason,
                                                                net.minecraft.class_1315 groupData) {
        CbRestSpawn.cbWithTag(difficulty, CbRestSpawn.cbStructureTag());
        try {
            return mob.method_5943(level, difficulty, reason, groupData);
        } finally {
            CbRestSpawn.cbEndCall(difficulty);
        }
    }
}
