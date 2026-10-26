package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderLiving;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 LivingEntityRenderer.getBob / getAttackAnim, gone in 1.21.2 (their values live in the render state), put back
 * with 1.21.1's bodies, and reachable publicly for the bridges (CbRenderLiving.bob / attackAnim), which call them
 * virtually so a renderer's override -- a mod's, or a 1.21.1 vanilla one put back (CbRenderLivingBob*Mixin) -- answers.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_922.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderLivingMixin implements CbRenderLiving.Legacy {

    /** 1.21.1 class_922.method_4045 (getBob): entity.tickCount + partialTick */
    protected float method_4045(net.minecraft.class_1309 entity, float partialTick) {
        return (float) entity.field_6012 + partialTick;
    }

    /** 1.21.1 class_922.method_4044 (getAttackAnim): entity.getAttackAnim(partialTick) */
    protected float method_4044(net.minecraft.class_1309 entity, float partialTick) {
        return entity.method_6055(partialTick);
    }

    @Override
    public float cb$bob(net.minecraft.class_1309 entity, float partialTick) {
        return this.method_4045(entity, partialTick);
    }

    @Override
    public float cb$attackAnim(net.minecraft.class_1309 entity, float partialTick) {
        return this.method_4044(entity, partialTick);
    }
}
