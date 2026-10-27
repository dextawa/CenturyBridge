package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 ChickenRenderer.getBob (class_882.method_3893), gone in 1.21.2 (the state carries the value), put back with its body: lerp(pt, oFlap, flap) and lerp(pt, oFlapSpeed, flapSpeed): (sin(flap) + 1) * flapSpeed. */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_882.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderLivingBob882Mixin {

    /** 1.21.1 class_882.method_3893 */
    protected float method_3893(net.minecraft.class_1428 entity, float partialTick) {
        float flap = net.minecraft.class_3532.method_16439(partialTick, entity.field_6736, entity.field_6741);
        float flapSpeed = net.minecraft.class_3532.method_16439(partialTick, entity.field_6738, entity.field_6743);
        return (net.minecraft.class_3532.method_15374(flap) + 1.0F) * flapSpeed;
    }

    /** javac's 1.21.1 bridge method_4045(LivingEntity, float) to it */
    protected float method_4045(net.minecraft.class_1309 entity, float partialTick) {
        return this.method_3893((net.minecraft.class_1428) entity, partialTick);
    }
}
