package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_4668;

/**
 * 1.21.8's RenderStateShard.TRANSLUCENT_TARGET (field_25280, "translucent_target") and PARTICLES_TARGET (field_25281,
 * "particles_target"), which 1.21.9's RenderStateShard no longer builds (javap -c of both code jars' class_4668
 * &lt;clinit&gt;: 1.21.9 keeps main / outline / weather / item_entity). 1.21.8 built them as OutputStateShards over
 * Minecraft.getInstance().levelRenderer.getTranslucentTarget() (class_761.method_29360, LevelTargetBundle field_53092)
 * and getParticlesTarget() (method_29362, field_53094). 1.21.9's LevelRenderer still declares both getters and still
 * fills both bundle slots, and its RenderType falls back to the main target when an output shard answers null, as
 * 1.21.8's did. So both shards come back with 1.21.8's names and suppliers (fieldGetters, s9i-outputs.json).
 */
@Environment(EnvType.CLIENT)
public final class CbOutputTargets {
    private CbOutputTargets() {
    }

    private static final class_4668.class_4678 TRANSLUCENT_TARGET = new class_4668.class_4678("translucent_target",
        () -> class_310.method_1551().field_1769.method_29360());
    private static final class_4668.class_4678 PARTICLES_TARGET = new class_4668.class_4678("particles_target",
        () -> class_310.method_1551().field_1769.method_29362());

    /** RenderStateShard.TRANSLUCENT_TARGET */
    public static class_4668.class_4678 field_25280() {
        return TRANSLUCENT_TARGET;
    }

    /** RenderStateShard.PARTICLES_TARGET */
    public static class_4668.class_4678 field_25281() {
        return PARTICLES_TARGET;
    }
}
