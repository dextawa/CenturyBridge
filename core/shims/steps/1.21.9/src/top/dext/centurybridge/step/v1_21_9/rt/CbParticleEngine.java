package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_11659;
import net.minecraft.class_11938;
import net.minecraft.class_11939;
import net.minecraft.class_12075;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3300;
import net.minecraft.class_3302;
import net.minecraft.class_3940;
import net.minecraft.class_3999;
import net.minecraft.class_4184;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import net.minecraft.class_702;
import net.minecraft.class_703;
import org.joml.Matrix4f;

/**
 * ParticleEngine's 1.21.8 members that 1.21.9 moved out of it. 1.21.9 split the engine: the sprite sets, providers
 * and their reload went to ParticleResources (the game's one, which the engine is built over), the particle atlas
 * to the AtlasManager, block destroy / crack particles to ClientLevel (javap: destroy(pos, state) is
 * ClientLevel.addDestroyBlockEffect's body, crack(pos, direction) ClientLevel.addBreakingBlockEffect's, both reading
 * the level the engine was given), and drawing to extract (groups of particles into a ParticlesRenderState) plus
 * submit.
 */
@Environment(EnvType.CLIENT)
public final class CbParticleEngine {
    private CbParticleEngine() {
    }

    /** the game's ParticleResources: what a 1.21.8 engine loaded over its texture manager itself */
    public static class_11939 resources(class_1060 textureManager) {
        return class_310.method_1551().field_62565;
    }

    /** new ParticleEngine(level, textureManager) */
    public static class_702 create(class_638 level, class_1060 textureManager) {
        return new class_702(level, resources(textureManager));
    }

    /** ParticleEngine.reload(barrier, manager, background, game): the engine's resources reload (CbReload: 1.21.9's shape) */
    public static CompletableFuture<Void> method_25931(class_702 engine, class_3302.class_4045 barrier, class_3300 manager,
            Executor background, Executor game) {
        return CbReload.method_25931(engine.field_62618, barrier, manager, background, game);
    }

    /** ParticleEngine.close(): 1.21.8 cleared its particle atlas, which is the AtlasManager's on 1.21.9 */
    public static void method_18829(class_702 engine) {
        class_1059 atlas = class_310.method_1551().method_72703().method_73025(class_1059.field_17898);
        if (atlas != null) {
            atlas.method_4601();
        }
    }

    /** ParticleEngine.destroy(pos, state): the engine's level's addDestroyBlockEffect */
    public static void method_3046(class_702 engine, class_2338 pos, class_2680 state) {
        if (engine.field_3834 != null) {
            engine.field_3834.method_31595(pos, state);
        }
    }

    /** ParticleEngine.crack(pos, direction): the engine's level's addBreakingBlockEffect */
    public static void method_3054(class_702 engine, class_2338 pos, class_2350 direction) {
        if (engine.field_3834 != null) {
            engine.field_3834.method_74254(pos, direction);
        }
    }

    /** a frustum holding everything: 1.21.8's render drew every particle (culling was the particle's own business) */
    private static final class Everything extends class_4604 {
        Everything() {
            super(new Matrix4f(), new Matrix4f());
        }

        @Override
        public boolean method_74404(double x, double y, double z) {
            return true;
        }

        @Override
        public boolean method_23093(net.minecraft.class_238 box) {
            return true;
        }
    }

    /**
     * ParticleEngine.render(camera, partialTick, bufferSource): every particle drawn into the buffers now, then the
     * batch ended, as 1.21.8 did. Single-quad particles draw their quad into the buffer of their layer's 1.21.8 sheet
     * RenderType (CbParticles.Immediate); every other group (legacy groups, item pickup, elder guardians) submits its
     * extracted state through a collector rendering into the buffers at once (CbSubmit).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_3049(class_702 engine, class_4184 camera, float partialTick, class_4597.class_4598 buffers) {
        class_11659 collector = CbSubmit.collector(buffers);
        class_12075 cameraState = CbSubmit.camera(buffers);
        class_4604 everything = new Everything();
        for (Map.Entry<class_3999, class_11938<?>> e : ((Map<class_3999, class_11938<?>>) (Map) engine.field_3830).entrySet()) {
            class_11938<?> group = e.getValue();
            if (group.method_74284()) {
                continue;
            }
            if (e.getKey() == class_3999.field_62622) {
                for (class_703 p : group.method_74290()) {
                    if (p instanceof class_3940 quad) {
                        class_1921 type = CbParticles.comp_3341(CbParticles.sheetOf(quad.method_74255()));
                        if (type != null) {
                            quad.method_3074(CbParticles.immediate(buffers.method_73477(type)), camera, partialTick);
                        }
                    }
                }
            } else {
                group.method_74276(everything, camera, partialTick).submit(collector, cameraState);
            }
        }
        buffers.method_22993();
    }
}
