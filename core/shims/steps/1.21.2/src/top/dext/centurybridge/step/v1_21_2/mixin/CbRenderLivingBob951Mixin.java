package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 SquidRenderer.getBob (class_951.method_4125), gone in 1.21.2 (the state carries the value), put back with its body: lerp(pt, oldTentacleAngle, tentacleAngle). */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_951.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderLivingBob951Mixin {

    /** 1.21.1 class_951.method_4125 */
    protected float method_4125(net.minecraft.class_1477 entity, float partialTick) {
        return net.minecraft.class_3532.method_16439(partialTick, entity.field_6900, entity.field_6904);
    }

    /** javac's 1.21.1 bridge method_4045(LivingEntity, float) to it */
    protected float method_4045(net.minecraft.class_1309 entity, float partialTick) {
        return this.method_4125((net.minecraft.class_1477) entity, partialTick);
    }
}
