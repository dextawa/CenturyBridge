package top.dext.centurybridge.step.v1_21.rt;

import java.util.Map;
import com.google.common.collect.MapMaker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's ParticleRenderType on 1.21. 1.20.6's type was begin(builder, textures) -- set the render state up and
 * begin the Tesselator's builder -- and end(tesselator) -- draw it; 1.21's is begin(tesselator, textures)
 * answering the builder the particles go into, which 1.21's ParticleEngine then builds and draws itself.
 * <ul>
 * <li>A 1.20.6 type (CbRenderLegacyParticleType, superRenames) is begun on the Tesselator's 1.20.6 builder,
 *     which takes its particles, and ended with its own end(tesselator) where 1.21 would build and draw
 *     (CbRenderParticleEngineMixin) -- 1.20.6's ParticleEngine order.</li>
 * <li>A 1.20.6 call of begin(builder, textures) on a 1.21 type sets its state up as 1.21's begin does and begins
 *     the given builder in the mode and format 1.21's begin chose (1.20.6's vanilla begin bodies were 1.21's with
 *     builder.begin in place of tesselator.begin); end(tesselator) draws the Tesselator's builder for a type that
 *     begins one (1.20.6's vanilla ends were tesselator.end()) and nothing for one that does not.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderParticles {

    private CbRenderParticles() {
    }

    /** 1.20.6's ParticleRenderType, as a 1.20.6 class implements it */
    public interface CbRenderLegacyParticleType extends net.minecraft.class_3999 {

        /** begin(builder, textures) */
        void method_18130(net.minecraft.class_287 builder, net.minecraft.class_1060 textures);

        /** end(tesselator) */
        void method_18131(net.minecraft.class_289 tesselator);

        /** 1.21's begin: 1.20.6's on the Tesselator's builder, which then takes the particles */
        @Override
        default net.minecraft.class_287 method_18130(net.minecraft.class_289 tesselator, net.minecraft.class_1060 textures) {
            net.minecraft.class_287 builder = ((CbRenderTesselatorV) (Object) tesselator).method_1349();
            this.method_18130(builder, textures);
            return builder;
        }
    }

    /** whether a 1.21 type begins a builder (its 1.20.6 end draws the Tesselator's), learned from its begin */
    private static final Map<net.minecraft.class_3999, Boolean> DRAWS = new MapMaker().weakKeys().makeMap();

    /** type.begin(builder, textures), called 1.20.6's way */
    public static void cbBegin(net.minecraft.class_3999 type, net.minecraft.class_287 builder, net.minecraft.class_1060 textures) {
        if (type instanceof CbRenderLegacyParticleType legacy) {
            legacy.method_18130(builder, textures);
            return;
        }
        net.minecraft.class_287 begun = type.method_18130(net.minecraft.class_289.method_1348(), textures);
        DRAWS.put(type, begun != null);
        if (begun != null) {
            CbRenderBufferBuilderV b = (CbRenderBufferBuilderV) (Object) begun;
            ((CbRenderBufferBuilderV) (Object) builder).method_1328(b.cb$mode(), b.cb$format());
        }
    }

    /** type.end(tesselator), called 1.20.6's way */
    public static void cbEnd(net.minecraft.class_3999 type, net.minecraft.class_289 tesselator) {
        if (type instanceof CbRenderLegacyParticleType legacy) {
            legacy.method_18131(tesselator);
            return;
        }
        Boolean draws = DRAWS.get(type);
        if (draws == null) {
            draws = type == net.minecraft.class_3999.field_17827 || type == net.minecraft.class_3999.field_17828
                || type == net.minecraft.class_3999.field_17829 || type == net.minecraft.class_3999.field_17830;
        }
        if (draws) {
            ((CbRenderTesselatorV) (Object) tesselator).method_1350();
        }
    }
}
