package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's particle API on 1.20.5 (s4client, particles): the gust seed provider's fixed shape, the firework
 * starter's tag, the particle engine's render with its pose.
 */
@Environment(EnvType.CLIENT)
public final class CbClientParticles {

    private CbClientParticles() {
    }

    // ------------------------------------------------------------------ gust seed

    /** 1.20.4 GustSeedParticle.Provider(): the seed spread its gusts up to 4 blocks out (1.20.5's Provider(scale, lifetime, interval)) */
    public static double gustSeedScale() {
        return 4.0D;
    }

    /** ... lived 7 ticks ... */
    public static int gustSeedLifetime() {
        return 7;
    }

    /** ... and spawned 3 gusts every tick (1.20.5 spawns them when age % (interval + 1) == 0) */
    public static int gustSeedInterval() {
        return 0;
    }

    // ------------------------------------------------------------------ firework starter

    /**
     * The explosions of a starter 1.20.4 built from a null or explosion-less tag: an invisible particle living 8
     * ticks that never bursts or twinkles (1.20.4 kept its explosion list null). 1.20.5's constructor refuses an
     * empty list; this instance passes it (mixin/CbClientFireworkStarterMixin) and marks the starter to tick as
     * 1.20.4's did.
     */
    public static final java.util.List<net.minecraft.class_9283> NO_EXPLOSIONS = java.util.Collections.unmodifiableList(new java.util.ArrayList<>());

    /** 1.20.4 FireworkParticles.Starter's fireworks tag as 1.20.5's explosions ({@link #NO_EXPLOSIONS} for none) */
    public static java.util.List<net.minecraft.class_9283> starterExplosions(net.minecraft.class_2487 tag) {
        java.util.List<net.minecraft.class_9283> explosions = CbBlockLevel.fireworkExplosions(tag);
        return explosions.isEmpty() ? NO_EXPLOSIONS : explosions;
    }

    // ------------------------------------------------------------------ particle engine render

    /** the 1.20.4 ParticleEngine.render(pose, buffers, light, camera, partialTick) a mod engine class overrides (adapter of the override hook) */
    public interface LegacyRender {
        void method_3049(net.minecraft.class_4587 pose, net.minecraft.class_4597.class_4598 buffers, net.minecraft.class_765 light,
                         net.minecraft.class_4184 camera, float partialTick);
    }

    /** super's 1.20.5 render(light, camera, partialTick), reached from such a class's super.render(..) */
    public interface SuperRender {
        void cb$super$method_3049(net.minecraft.class_765 light, net.minecraft.class_4184 camera, float partialTick);
    }

    /** ParticleEngine.render(pose, buffers, light, camera, partialTick) called on any engine: a mod engine's own override, else 1.20.4's body */
    public static void method_3049(net.minecraft.class_702 engine, net.minecraft.class_4587 pose, net.minecraft.class_4597.class_4598 buffers,
                                   net.minecraft.class_765 light, net.minecraft.class_4184 camera, float partialTick) {
        if (engine instanceof LegacyRender legacy) {
            legacy.method_3049(pose, buffers, light, camera, partialTick);
        } else {
            withPose(pose, () -> engine.method_3049(light, camera, partialTick));
        }
    }

    /**
     * 1.20.5 render(light, camera, partialTick) of a mod engine overriding 1.20.4's (bridge of the override hook).
     * 1.20.4's level renderer handed it its pose stack (the camera rotation) and the frame's buffer source,
     * the model-view stack not yet rotated; 1.20.5's model-view stack already carries the rotation, so the
     * override gets an identity pose -- what it multiplies onto the model-view (its super call) or draws with
     * lands where 1.20.4 drew it -- and the frame's buffer source.
     */
    public static void render(LegacyRender self, net.minecraft.class_765 light, net.minecraft.class_4184 camera, float partialTick) {
        self.method_3049(new net.minecraft.class_4587(), net.minecraft.class_310.method_1551().method_22940().method_23000(), light, camera,
            partialTick);
    }

    /** a mod engine's super.render(pose, buffers, light, camera, partialTick): 1.20.4's body over super's 1.20.5 render */
    public static void superRender(SuperRender self, net.minecraft.class_4587 pose, net.minecraft.class_4597.class_4598 buffers,
                                   net.minecraft.class_765 light, net.minecraft.class_4184 camera, float partialTick) {
        withPose(pose, () -> self.cb$super$method_3049(light, camera, partialTick));
    }

    /** 1.20.4's render body around 1.20.5's: the pose multiplied onto the model-view stack for the particles, then taken off (the buffers unused, as 1.20.4 left them) */
    private static void withPose(net.minecraft.class_4587 pose, Runnable render) {
        org.joml.Matrix4fStack modelView = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(pose.method_23760().method_23761());
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
        render.run();
        modelView.popMatrix();
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
    }
}
