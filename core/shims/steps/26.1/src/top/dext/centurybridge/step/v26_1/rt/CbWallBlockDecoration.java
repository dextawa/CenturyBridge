package top.dext.centurybridge.step.v26_1.rt;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockDecorationLayer's block source (generic drift): 1.21.11's layer took a Function&lt;S, Optional&lt;BlockState&gt;&gt;
 * and drew the state's block model; 26.1's takes a Function&lt;S, BlockModelRenderState&gt; whose answer it submits,
 * vanilla's renderers resolving the state's model into their render state when they extract it (BlockModelResolver
 * with a display context of their own, as CopperGolemRenderer does). A 1.21.11 function is wrapped: its state is
 * resolved into a model render state kept per entity render state, and an empty answer is an empty model render
 * state (nothing drawn, as 1.21.11 drew nothing). The wrapper is adaptive -- a function that already answers a
 * BlockModelRenderState passes its answer through.
 */
@Environment(EnvType.CLIENT)
public final class CbWallBlockDecoration {

    private CbWallBlockDecoration() {
    }

    private static final BlockDisplayContext CONTEXT = BlockDisplayContext.create();
    private static ModelManager resolverModels;
    private static BlockModelResolver resolver;

    private static synchronized BlockModelResolver resolver() {
        ModelManager models = Minecraft.getInstance().getModelManager();
        if (resolver == null || resolverModels != models) {
            resolver = new BlockModelResolver(models);
            resolverModels = models;
        }
        return resolver;
    }

    private static final class Legacy<S> implements Function<S, BlockModelRenderState> {
        private final Function<S, ?> source;
        private final Map<S, BlockModelRenderState> states = new WeakHashMap<>();

        Legacy(Function<S, ?> source) {
            this.source = source;
        }

        @Override
        public BlockModelRenderState apply(S renderState) {
            Object answer = source.apply(renderState);
            if (answer instanceof BlockModelRenderState model) {
                return model;
            }
            BlockModelRenderState model;
            synchronized (states) {
                model = states.computeIfAbsent(renderState, s -> new BlockModelRenderState());
            }
            Optional<?> block = answer instanceof Optional<?> o ? o : Optional.empty();
            if (block.isPresent() && block.get() instanceof BlockState state) {
                resolver().update(model, state, CONTEXT);
            } else {
                model.clear();
            }
            return model;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Function blockModels(Function source) {
        if (source == null || source instanceof Legacy) {
            return source;
        }
        return new Legacy<>(source);
    }
}
