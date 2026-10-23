package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelFaceBakery;

/**
 * FaceBakery's 1.21.3 instance methods bakeQuad / rotateVertexBy, which 1.21.4 made static (see {@link CbModelFaceBakery}):
 * every FaceBakery answers them again, with 1.21.4's static bodies (1.21.4's code is 1.21.3's, every private call made
 * static). 1.21.3's bakeQuad reached rotateVertexBy virtually (bakeQuad -> makeVertices -> bakeVertex), so a subclass's
 * bakeQuad runs 1.21.3's steps here, with its own rotateVertexBy.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_796.class)
public abstract class CbModelFaceBakeryMixin implements CbModelFaceBakery.CbLegacyFaceBakery {

    @Shadow
    private static void method_3455(org.joml.Vector3f pos, net.minecraft.class_4590 transformation) {
        throw new AssertionError();
    }

    @Shadow
    private static float[] method_3459(org.joml.Vector3f from, org.joml.Vector3f to) {
        throw new AssertionError();
    }

    @Shadow
    private static void method_3463(org.joml.Vector3f pos, net.minecraft.class_789 rotation) {
        throw new AssertionError();
    }

    @Shadow
    private static void method_3460(int[] vertices, int index, org.joml.Vector3f pos, net.minecraft.class_1058 sprite, net.minecraft.class_787 uv) {
        throw new AssertionError();
    }

    @Shadow
    private static net.minecraft.class_2350 method_3467(int[] vertices) {
        throw new AssertionError();
    }

    @Shadow
    private static void method_3462(int[] vertices, net.minecraft.class_2350 direction) {
        throw new AssertionError();
    }

    @Override
    public net.minecraft.class_777 cb$bakeQuad(org.joml.Vector3f from, org.joml.Vector3f to, net.minecraft.class_783 face,
            net.minecraft.class_1058 sprite, net.minecraft.class_2350 direction, net.minecraft.class_3665 state,
            net.minecraft.class_789 rotation, boolean shade, int lightEmission) {
        if ((Object) this.getClass() == net.minecraft.class_796.class) {
            return net.minecraft.class_796.method_3468(from, to, face, sprite, direction, state, rotation, shade, lightEmission);
        }
        // 1.21.3 bakeQuad, step by step
        net.minecraft.class_787 uv = face.comp_2870();
        if (state.method_3512()) {
            uv = net.minecraft.class_796.method_3454(face.comp_2870(), direction, state.method_3509());
        }
        float[] saved = new float[uv.field_4235.length];
        System.arraycopy(uv.field_4235, 0, saved, 0, saved.length);
        float shrink = sprite.method_23842();
        float cu = (uv.field_4235[0] + uv.field_4235[0] + uv.field_4235[2] + uv.field_4235[2]) / 4.0f;
        float cv = (uv.field_4235[1] + uv.field_4235[1] + uv.field_4235[3] + uv.field_4235[3]) / 4.0f;
        uv.field_4235[0] = net.minecraft.class_3532.method_16439(shrink, uv.field_4235[0], cu);
        uv.field_4235[2] = net.minecraft.class_3532.method_16439(shrink, uv.field_4235[2], cu);
        uv.field_4235[1] = net.minecraft.class_3532.method_16439(shrink, uv.field_4235[1], cv);
        uv.field_4235[3] = net.minecraft.class_3532.method_16439(shrink, uv.field_4235[3], cv);
        float[] shape = method_3459(from, to);
        net.minecraft.class_4590 transformation = state.method_3509();
        int[] vertices = new int[32];
        for (int i = 0; i < 4; i++) {
            // bakeVertex
            net.minecraft.class_753.class_755 info = net.minecraft.class_753.method_3163(direction).method_3162(i);
            org.joml.Vector3f pos = new org.joml.Vector3f(shape[info.field_3975], shape[info.field_3974], shape[info.field_3973]);
            method_3463(pos, rotation);
            this.cb$rotateVertexBy(pos, transformation);
            method_3460(vertices, i, pos, sprite, uv);
        }
        net.minecraft.class_2350 facing = method_3467(vertices);
        System.arraycopy(saved, 0, uv.field_4235, 0, saved.length);
        if (rotation == null) {
            method_3462(vertices, facing);
        }
        return new net.minecraft.class_777(vertices, face.comp_2868(), facing, sprite, shade, lightEmission);
    }

    @Override
    public void cb$rotateVertexBy(org.joml.Vector3f pos, net.minecraft.class_4590 transformation) {
        method_3455(pos, transformation);
    }
}
