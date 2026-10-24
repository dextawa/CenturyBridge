package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.systems.RenderPass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * 1.21.11's ParticleGroupRenderer contract on 26.1's.
 * <p>
 * 1.21.11's ParticleFeatureRenderer prepared each group once -- prepare(cache), every layer -- and rendered
 * it twice, render(prepared, cache, pass, textures, translucent): the opaque layers into the main target,
 * then the translucent ones into the particles target. 26.1 runs a whole pass per translucency
 * (ParticleFeatureRenderer.render(collection, translucent): solid, then translucent), each preparing the
 * group again -- prepare(cache, translucent), that translucency's layers, in a buffer cache of its own -- and
 * rendering what it prepared, render(prepared, cache, pass, textures).
 * <p>
 * So a group renderer of a 1.21.11 class is, in each 26.1 pass, prepared as 1.21.11 prepared it and asked
 * to render that pass's translucency (the order 1.21.11 rendered them in); its super calls reach 26.1's
 * QuadParticleRenderState with the pass's translucency -- whose layers are all 26.1 prepares and renders in
 * that pass, the other translucency's being the other pass's. The pass running is recorded by
 * mixin/CbWallParticleFeatureMixin.
 */
@Environment(EnvType.CLIENT)
public final class CbWallParticles {

    private CbWallParticles() {
    }

    /** the translucency of the 26.1 particle pass running on this thread, null outside one */
    private static final ThreadLocal<Boolean> PASS = new ThreadLocal<>();

    public static void passStarted(boolean translucent) {
        PASS.set(translucent);
    }

    public static void passEnded() {
        PASS.remove();
    }

    private static boolean pass(String what) {
        Boolean translucent = PASS.get();
        if (translucent == null) {
            throw new IllegalStateException(what + " outside a particle pass: 1.21.11 prepared every layer in one buffer, 26.1"
                + " prepares one translucency per pass (ParticleFeatureRenderer.render), and none is running");
        }
        return translucent;
    }

    /** 1.21.11's prepare(cache), declared by a mod's group renderer */
    public interface LegacyPrepare {
        QuadParticleRenderState.PreparedBuffers prepare(ParticleFeatureRenderer.ParticleBufferCache cache);
    }

    /** 1.21.11's render(prepared, cache, pass, textures, translucent), declared by a mod's group renderer */
    public interface LegacyRender {
        void render(QuadParticleRenderState.PreparedBuffers prepared, ParticleFeatureRenderer.ParticleBufferCache cache, RenderPass pass,
            TextureManager textures, boolean translucent);
    }

    /** super's 26.1 prepare, from a mod group renderer's super.prepare(cache) (the accessor keeps the intermediary name) */
    public interface SuperPrepare {
        QuadParticleRenderState.PreparedBuffers cb$super$method_74755(ParticleFeatureRenderer.ParticleBufferCache cache, boolean translucent);
    }

    /** super's 26.1 render, from a mod group renderer's super.render(prepared, cache, pass, textures, translucent) */
    public interface SuperRender {
        void cb$super$method_74324(QuadParticleRenderState.PreparedBuffers prepared, ParticleFeatureRenderer.ParticleBufferCache cache,
            RenderPass pass, TextureManager textures);
    }

    /** 26.1's prepare(cache, translucent) of a mod group renderer: 1.21.11's prepare, every layer */
    public static QuadParticleRenderState.PreparedBuffers prepare(LegacyPrepare self, ParticleFeatureRenderer.ParticleBufferCache cache, boolean translucent) {
        return self.prepare(cache);
    }

    /** 26.1's render(prepared, cache, pass, textures) of a mod group renderer: 1.21.11's render of this pass's translucency */
    public static void render(LegacyRender self, QuadParticleRenderState.PreparedBuffers prepared, ParticleFeatureRenderer.ParticleBufferCache cache,
        RenderPass pass, TextureManager textures) {
        self.render(prepared, cache, pass, textures, pass("ParticleGroupRenderer.render"));
    }

    /** a mod's super.prepare(cache): super's 26.1 prepare of this pass's translucency */
    public static QuadParticleRenderState.PreparedBuffers superPrepare(SuperPrepare self, ParticleFeatureRenderer.ParticleBufferCache cache) {
        return self.cb$super$method_74755(cache, pass("ParticleGroupRenderer.prepare"));
    }

    /**
     * a mod's super.render(prepared, cache, pass, textures, translucent): super's 26.1 render when the
     * translucency is this pass's; the other translucency's layers are prepared and rendered by the other pass
     */
    public static void superRender(SuperRender self, QuadParticleRenderState.PreparedBuffers prepared, ParticleFeatureRenderer.ParticleBufferCache cache,
        RenderPass pass, TextureManager textures, boolean translucent) {
        if (translucent == pass("ParticleGroupRenderer.render")) {
            self.cb$super$method_74324(prepared, cache, pass, textures);
        }
    }

    /** a mod's call of 1.21.11's prepare(cache) on a group renderer: 26.1's prepare of this pass's translucency */
    public static QuadParticleRenderState.PreparedBuffers callPrepare(SubmitNodeCollector.ParticleGroupRenderer renderer,
        ParticleFeatureRenderer.ParticleBufferCache cache) {
        return renderer.prepare(cache, pass("ParticleGroupRenderer.prepare"));
    }

    /** a mod's call of 1.21.11's render(prepared, cache, pass, textures, translucent): as {@link #superRender} */
    public static void callRender(SubmitNodeCollector.ParticleGroupRenderer renderer, QuadParticleRenderState.PreparedBuffers prepared,
        ParticleFeatureRenderer.ParticleBufferCache cache, RenderPass pass, TextureManager textures, boolean translucent) {
        if (translucent == pass("ParticleGroupRenderer.render")) {
            renderer.render(prepared, cache, pass, textures);
        }
    }
}
