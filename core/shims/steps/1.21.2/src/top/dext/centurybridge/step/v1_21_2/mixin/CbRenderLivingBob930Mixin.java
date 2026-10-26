package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 ParrotRenderer.getBob (class_930.method_4081), gone in 1.21.2 (the state carries the value), put back with its body: lerp(pt, oFlap, flap) and lerp(pt, oFlapSpeed, flapSpeed): (sin(flap) + 1) * flapSpeed. */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_930.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderLivingBob930Mixin {

    /** 1.21.1 class_930.method_4081 */
    public float method_4081(net.minecraft.class_1453 entity, float partialTick) {
        float flap = net.minecraft.class_3532.method_16439(partialTick, entity.field_6829, entity.field_6818);
        float flapSpeed = net.minecraft.class_3532.method_16439(partialTick, entity.field_6827, entity.field_6819);
        return (net.minecraft.class_3532.method_15374(flap) + 1.0F) * flapSpeed;
    }

    /** javac's 1.21.1 bridge method_4045(LivingEntity, float) to it */
    protected float method_4045(net.minecraft.class_1309 entity, float partialTick) {
        return this.method_4081((net.minecraft.class_1453) entity, partialTick);
    }
}
