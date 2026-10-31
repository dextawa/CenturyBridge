package cb.s205fr;

import java.util.concurrent.Callable;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The calls a 1.20.1 mod makes into the Fabric renderer API and the chunk render types: each one loads a class whose
 * static initializer, once climbed, runs a layer's RenderType construction (BlendMode, the renderer's material finder,
 * RenderType.translucent()). Each call is caught on its own, so an ExceptionInInitializerError shows as that call's
 * FAIL with its cause chain, and the later calls still run.
 */
public class S205frTestMod implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("cb_s205fr");
    private int ok;
    private int n;

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            check("BlendMode.values", () -> BlendMode.values().length + " modes");
            check("RendererAccess.getRenderer", () -> {
                Renderer r = RendererAccess.INSTANCE.getRenderer();
                if (r == null) throw new IllegalStateException("no renderer");
                return r.getClass().getName();
            });
            check("MaterialFinder.blendMode(0, TRANSLUCENT).find", () ->
                RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(0, BlendMode.TRANSLUCENT).find());
            check("RenderType.translucent", () -> net.minecraft.class_1921.method_23583());
            // a render type of the mod's own: POSITION_COLOR quads, buffer size 1536, a composite state left at the
            // builder's defaults (no output target set, so no TRANSLUCENT_TARGET / PARTICLES_TARGET field is read)
            check("RenderType.create(bufferSize 1536, default state)", () -> net.minecraft.class_1921.method_24048("cb_s205fr_own",
                net.minecraft.class_290.field_1576, net.minecraft.class_293.class_5596.field_27382, 1536,
                net.minecraft.class_1921.class_4688.method_23598().method_23617(false)));
            LOG.info("CB-S205FR {}/{}", ok, n);
        });
    }

    private void check(String name, Callable<Object> call) {
        n++;
        try {
            Object r = call.call();
            ok++;
            LOG.info("CB-S205FR PASS {}: {}", name, r);
        } catch (Throwable t) {
            StringBuilder s = new StringBuilder();
            for (Throwable c = t; c != null; c = c.getCause()) {
                s.append(s.length() == 0 ? "" : " <- ").append(c);
            }
            LOG.info("CB-S205FR FAIL {}: {}", name, s);
        }
    }
}
