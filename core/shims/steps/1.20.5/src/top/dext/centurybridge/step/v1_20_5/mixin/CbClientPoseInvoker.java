package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** PoseStack.Pose(pose, normal): a pose over 1.20.4's (Matrix4f, Matrix3f) pairs (rt/CbClientRender) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4587.class_4665.class)
public interface CbClientPoseInvoker {

    @Invoker("<init>")
    static net.minecraft.class_4587.class_4665 cb$newPose(org.joml.Matrix4f pose, org.joml.Matrix3f normal) {
        throw new AssertionError();
    }
}
