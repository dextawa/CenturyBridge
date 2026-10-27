package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * WitherSkeletonRenderer.scale(AbstractSkeleton, pose, partialTick): 1.20.4's shape (1.20.5's takes the
 * WitherSkeleton). 1.20.4's body: the pose scaled by 1.2 on every axis, whatever skeleton it was given. A
 * 1.20.4 subclass's call reaches it (its override of the old shape is hooked to the new one by mechanical.json).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_967.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientWitherSkeletonRendererMixin {

    /** 1.20.4 WitherSkeletonRenderer.scale(skeleton, pose, partialTick) */
    protected void method_4161(net.minecraft.class_1547 skeleton, net.minecraft.class_4587 pose, float partialTick) {
        pose.method_22905(1.2F, 1.2F, 1.2F);
    }
}
