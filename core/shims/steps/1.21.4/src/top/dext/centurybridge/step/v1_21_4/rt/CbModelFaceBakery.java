package top.dext.centurybridge.step.v1_21_4.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 FaceBakery (class_796) instance methods on 1.21.4, which made them static: bakeQuad (public static now) and
 * rotateVertexBy (private static now). 1.21.3 dispatched them virtually, so a mod's FaceBakery subclass could override
 * them for its own calls; FaceBakery gets them back as instance methods under {@link CbLegacyFaceBakery} names
 * (CbModelFaceBakeryMixin), a mod's override of either is renamed onto them (overrideRenames), and a mod's call is routed
 * through them here.
 */
@Environment(EnvType.CLIENT)
public final class CbModelFaceBakery {
    private CbModelFaceBakery() {
    }

    /** FaceBakery's 1.21.3 instance methods, implemented by every FaceBakery (the mixin) and by a mod override */
    public interface CbLegacyFaceBakery {
        net.minecraft.class_777 cb$bakeQuad(org.joml.Vector3f from, org.joml.Vector3f to, net.minecraft.class_783 face,
            net.minecraft.class_1058 sprite, net.minecraft.class_2350 direction, net.minecraft.class_3665 state,
            net.minecraft.class_789 rotation, boolean shade, int lightEmission);

        void cb$rotateVertexBy(org.joml.Vector3f pos, net.minecraft.class_4590 transformation);
    }

    /** was bakeQuad(from, to, face, sprite, direction, state, rotation, shade, lightEmission) (method_3468) */
    public static net.minecraft.class_777 method_3468(net.minecraft.class_796 bakery, org.joml.Vector3f from, org.joml.Vector3f to,
        net.minecraft.class_783 face, net.minecraft.class_1058 sprite, net.minecraft.class_2350 direction, net.minecraft.class_3665 state,
        net.minecraft.class_789 rotation, boolean shade, int lightEmission) {
        return ((CbLegacyFaceBakery) bakery).cb$bakeQuad(from, to, face, sprite, direction, state, rotation, shade, lightEmission);
    }

    /** was rotateVertexBy(pos, transformation) (method_3455) */
    public static void method_3455(net.minecraft.class_796 bakery, org.joml.Vector3f pos, net.minecraft.class_4590 transformation) {
        ((CbLegacyFaceBakery) bakery).cb$rotateVertexBy(pos, transformation);
    }
}
