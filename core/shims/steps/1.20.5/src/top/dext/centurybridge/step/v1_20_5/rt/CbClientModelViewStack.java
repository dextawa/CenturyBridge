package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

/**
 * 1.20.4's RenderSystem model-view PoseStack, over 1.20.5's model-view Matrix4fStack: every push, pop and
 * transform a 1.20.4 caller makes acts on the stack 1.20.5's applyModelViewMatrix() uploads. last() answers
 * a pose whose matrix is the stack's current matrix itself (1.20.4's last().pose() was the live top
 * matrix), its normal matrix derived from it as 1.20.4's mulPoseMatrix / scale / rotation kept it.
 */
@Environment(EnvType.CLIENT)
final class CbClientModelViewStack extends net.minecraft.class_4587 {

    final Matrix4fStack stack;

    CbClientModelViewStack(Matrix4fStack stack) {
        this.stack = stack;
    }

    @Override
    public void method_22904(double x, double y, double z) {
        this.stack.translate((float) x, (float) y, (float) z);
    }

    @Override
    public void method_46416(float x, float y, float z) {
        this.stack.translate(x, y, z);
    }

    @Override
    public void method_22905(float x, float y, float z) {
        this.stack.scale(x, y, z);
    }

    @Override
    public void method_22907(Quaternionf rotation) {
        this.stack.rotate(rotation);
    }

    @Override
    public void method_49278(Quaternionf rotation, float x, float y, float z) {
        this.stack.rotateAround(rotation, x, y, z);
    }

    @Override
    public void method_22903() {
        this.stack.pushMatrix();
    }

    @Override
    public void method_22909() {
        this.stack.popMatrix();
    }

    @Override
    public net.minecraft.class_4587.class_4665 method_23760() {
        Matrix3f normal = new Matrix3f(this.stack).invert().transpose();
        return CbClientRender.livePose(this.stack, normal);
    }

    /** JOML's Matrix4fStack keeps its depth in the private field curr (0 = only the base matrix) */
    private static final java.lang.reflect.Field CURR;

    static {
        try {
            CURR = Matrix4fStack.class.getDeclaredField("curr");
            CURR.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /** 1.20.4 PoseStack.clear(): whether only the base entry is left (the stack's depth is 0) */
    @Override
    public boolean method_22911() {
        try {
            return CURR.getInt(this.stack) == 0;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void method_34426() {
        this.stack.identity();
    }

    @Override
    public void method_34425(Matrix4f matrix) {
        this.stack.mul(matrix);
    }
}
