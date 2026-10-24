package top.dext.centurybridge.step.v1_21_2.rt;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialView;
import net.fabricmc.fabric.api.renderer.v1.material.ShadeMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;

/**
 * 1.21.1 Fabric API renderer-api and rendering members 1.21.2's dropped or made abstract.
 * <ul>
 * <li>MaterialFinder.shadeMode(mode), MaterialView.shadeMode(), Mesh.outputTo(emitter): defaults in 1.21.1, abstract
 *     in 1.21.2 -- a 1.21.1 implementation gets 1.21.1's default (abstractFills), and a Mesh lambda (1.21.1's Mesh had
 *     one abstract method, forEach) is built as {@link LegacyMesh} and adapted (lambdaAdapters).</li>
 * <li>ForwardingBakedModel(wrapped): 1.21.2 keeps only the no-argument constructor; 1.21.1's set the protected
 *     wrapped field, done here after it (superCtorReshapes post).</li>
 * <li>WorldRenderContext.profiler(): 1.21.2's renderer profiles through the global Profiler.get(), the profiler 1.21.1's
 *     context answered (the one LevelRenderer.renderLevel was profiling with).</li>
 * <li>RenderContext.bakedModelConsumer() / fallbackConsumer(): 1.21.1's Indigo contexts answered a consumer emitting a
 *     vanilla model through VanillaModelEncoder (emitBlockQuads with the block's random supplier, the current block
 *     state for accept(model); emitItemQuads with the item context's random supplier, no state); 1.21.2's Indigo keeps
 *     the same contexts and encoder, so the consumer is 1.21.1's bodies on them. fallbackConsumer was a default
 *     answering bakedModelConsumer().</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbFapiRendererApi {

    /** 1.21.1 Mesh's one abstract method */
    @FunctionalInterface
    public interface LegacyMesh {
        void forEach(Consumer<QuadView> action);
    }

    private static final VarHandle WRAPPED;

    static {
        try {
            WRAPPED = MethodHandles.privateLookupIn(ForwardingBakedModel.class, MethodHandles.lookup())
                .findVarHandle(ForwardingBakedModel.class, "wrapped", net.minecraft.class_1087.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private CbFapiRendererApi() {
    }

    /** 1.21.1 MaterialFinder.shadeMode(mode)'s default: nothing set, the finder itself */
    public static MaterialFinder shadeMode(MaterialFinder finder, ShadeMode mode) {
        return finder;
    }

    /** 1.21.1 MaterialView.shadeMode()'s default */
    public static ShadeMode shadeMode(MaterialView view) {
        return ShadeMode.ENHANCED;
    }

    /** the same default, for a 1.21.1 RenderMaterial implementation */
    public static ShadeMode shadeMode(net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material) {
        return ShadeMode.ENHANCED;
    }

    /** the same default, for a 1.21.1 MaterialFinder implementation */
    public static ShadeMode shadeMode(MaterialFinder finder) {
        return ShadeMode.ENHANCED;
    }

    /** 1.21.1 Mesh.outputTo(emitter)'s default: every quad copied into the emitter and emitted */
    public static void outputTo(Mesh mesh, QuadEmitter emitter) {
        mesh.forEach(quad -> {
            emitter.copyFrom(quad);
            emitter.emit();
        });
    }

    /** a 1.21.1 Mesh lambda as 1.21.2's Mesh */
    public static Mesh mesh(LegacyMesh legacy) {
        return new Mesh() {
            @Override
            public void forEach(Consumer<QuadView> action) {
                legacy.forEach(action);
            }

            @Override
            public void outputTo(QuadEmitter emitter) {
                CbFapiRendererApi.outputTo(this, emitter);
            }
        };
    }

    /** after a 1.21.1 ForwardingBakedModel subclass's super(wrapped) */
    public static void forwarding(ForwardingBakedModel model, net.minecraft.class_1087 wrapped) {
        WRAPPED.set(model, wrapped);
    }

    /** WorldRenderContext.profiler() */
    public static net.minecraft.class_3695 profiler(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
        return net.minecraft.class_10209.method_64146();
    }

    private static final String INDIGO = "net.fabricmc.fabric.impl.client.indigo.renderer.render.";
    private static Class<?> blockContext;
    private static Class<?> itemContext;
    private static VarHandle blockInfo;
    private static VarHandle blockState;
    private static VarHandle blockRandom;
    private static VarHandle itemRandom;

    private static synchronized void indigo() {
        if (blockContext != null) {
            return;
        }
        try {
            ClassLoader loader = CbFapiRendererApi.class.getClassLoader();
            Class<?> block = Class.forName(INDIGO + "AbstractBlockRenderContext", false, loader);
            Class<?> info = Class.forName(INDIGO + "BlockRenderInfo", false, loader);
            Class<?> item = Class.forName(INDIGO + "ItemRenderContext", false, loader);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            blockInfo = MethodHandles.privateLookupIn(block, lookup).findVarHandle(block, "blockInfo", info);
            blockState = MethodHandles.privateLookupIn(info, lookup).findVarHandle(info, "blockState", net.minecraft.class_2680.class);
            blockRandom = MethodHandles.privateLookupIn(info, lookup).findVarHandle(info, "randomSupplier", java.util.function.Supplier.class);
            itemRandom = MethodHandles.privateLookupIn(item, lookup).findVarHandle(item, "randomSupplier", java.util.function.Supplier.class);
            itemContext = item;
            blockContext = block;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Indigo's render contexts are not those of Fabric API 0.106.1", e);
        }
    }

    /** RenderContext.bakedModelConsumer() */
    @SuppressWarnings("unchecked")
    public static top.dext.centurybridge.step.v1_21_2.compat.fapi.renderer.v1.render.RenderContextBakedModelConsumer bakedModelConsumer(
            net.fabricmc.fabric.api.renderer.v1.render.RenderContext context) {
        indigo();
        if (blockContext.isInstance(context)) {
            Object info = blockInfo.get(context);
            return new top.dext.centurybridge.step.v1_21_2.compat.fapi.renderer.v1.render.RenderContextBakedModelConsumer() {
                @Override
                public void accept(net.minecraft.class_1087 model) {
                    this.accept(model, (net.minecraft.class_2680) blockState.get(info));
                }

                @Override
                public void accept(net.minecraft.class_1087 model, net.minecraft.class_2680 state) {
                    net.fabricmc.fabric.impl.renderer.VanillaModelEncoder.emitBlockQuads(model, state,
                        (java.util.function.Supplier<net.minecraft.class_5819>) blockRandom.get(info), context);
                }
            };
        }
        if (itemContext.isInstance(context)) {
            return new top.dext.centurybridge.step.v1_21_2.compat.fapi.renderer.v1.render.RenderContextBakedModelConsumer() {
                @Override
                public void accept(net.minecraft.class_1087 model) {
                    this.accept(model, null);
                }

                @Override
                public void accept(net.minecraft.class_1087 model, net.minecraft.class_2680 state) {
                    net.fabricmc.fabric.impl.renderer.VanillaModelEncoder.emitItemQuads(model, state,
                        (java.util.function.Supplier<net.minecraft.class_5819>) itemRandom.get(context), context);
                }
            };
        }
        throw new IllegalStateException("bakedModelConsumer() of a render context Indigo did not make: " + context.getClass().getName());
    }

    /** RenderContext.fallbackConsumer(): 1.21.1's default, bakedModelConsumer() */
    public static Consumer<net.minecraft.class_1087> fallbackConsumer(net.fabricmc.fabric.api.renderer.v1.render.RenderContext context) {
        return bakedModelConsumer(context);
    }
}
