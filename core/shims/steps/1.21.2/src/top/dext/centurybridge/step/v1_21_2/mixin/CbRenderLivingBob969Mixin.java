package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/** 1.21.1 WolfRenderer.getBob (class_969.method_4167), gone in 1.21.2 (the state carries the value), put back with its body: wolf.getTailAngle(). */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_969.class)
public abstract class CbRenderLivingBob969Mixin {

    /** 1.21.1 class_969.method_4167 */
    protected float method_4167(net.minecraft.class_1493 entity, float partialTick) {
        return entity.method_6714();
    }

    /** javac's 1.21.1 bridge method_4045(LivingEntity, float) to it */
    protected float method_4045(net.minecraft.class_1309 entity, float partialTick) {
        return this.method_4167((net.minecraft.class_1493) entity, partialTick);
    }
}
