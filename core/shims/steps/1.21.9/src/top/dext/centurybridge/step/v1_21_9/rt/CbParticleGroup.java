package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11659;
import net.minecraft.class_11938;
import net.minecraft.class_11942;
import net.minecraft.class_12075;
import net.minecraft.class_1921;
import net.minecraft.class_3999;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4604;
import net.minecraft.class_702;
import net.minecraft.class_703;

/**
 * The particles of one 1.21.8 type that 1.21.9's groups cannot hold: drawn 1.21.8's way. 1.21.8's ParticleEngine drew
 * each sheet type's particles with render(consumer, camera, partialTick) into one buffer of the type's RenderType,
 * camera-relative and unculled, and each CUSTOM particle with renderCustom(pose, buffers, camera, partialTick); this
 * group submits the same as custom geometry of that RenderType (or through a CbSubmit capture for CUSTOM).
 */
@Environment(EnvType.CLIENT)
public final class CbParticleGroup extends class_11938<class_703> {
    private final class_3999 type;

    public CbParticleGroup(class_702 engine, class_3999 type) {
        super(engine);
        this.type = type;
    }

    @Override
    public class_11942 method_74276(class_4604 frustum, class_4184 camera, float partialTick) {
        return new State(this.type, new ArrayList<>(this.method_74290()), camera, partialTick);
    }

    /** the frame's particles of the group, drawn when the frame submits */
    static final class State implements class_11942 {
        private final class_3999 type;
        private final List<class_703> particles;
        private final class_4184 camera;
        private final float partialTick;

        State(class_3999 type, List<class_703> particles, class_4184 camera, float partialTick) {
            this.type = type;
            this.particles = particles;
            this.camera = camera;
            this.partialTick = partialTick;
        }

        @Override
        public void submit(class_11659 collector, class_12075 cameraState) {
            if (this.particles.isEmpty()) {
                return;
            }
            if (this.type.equals(CbParticles.CUSTOM)) {
                CbSubmit.Capture buffers = CbSubmit.capture(collector, cameraState);
                class_4587 pose = new class_4587();
                for (class_703 p : this.particles) {
                    CbParticles.method_65198(p, pose, buffers, this.camera, this.partialTick);
                }
                buffers.flush();
                return;
            }
            class_1921 renderType = CbParticles.comp_3341(this.type);
            if (renderType == null) {
                return;
            }
            collector.method_73483(new class_4587(), renderType, (pose, consumer) -> {
                for (class_703 p : this.particles) {
                    draw(p, consumer);
                }
            });
        }

        private void draw(class_703 p, class_4588 consumer) {
            try {
                CbParticles.method_3074(p, consumer, this.camera, this.partialTick);
            } catch (RuntimeException e) {
                throw new IllegalStateException("Rendering particle " + p + " of 1.21.8 type " + this.type.comp_3340(), e);
            }
        }
    }

    /** whether 1.21.9's engine should give this type to a legacy group */
    public static boolean legacy(class_3999 type) {
        return !CbParticles.nativeGroup(type);
    }
}
