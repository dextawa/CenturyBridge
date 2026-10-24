package top.dext.centurybridge.step.v1_21_2.compat.fapi.renderer.v1.render;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 Fabric API's RenderContext.BakedModelConsumer (classRenames), removed in 1.21.2: the context's consumer of
 * vanilla baked models (accept(model) for the current block state or item, accept(model, state)). A context answers
 * it through rt/CbFapiRendererApi.bakedModelConsumer.
 */
@Environment(EnvType.CLIENT)
public interface RenderContextBakedModelConsumer extends Consumer<net.minecraft.class_1087> {

    @Override
    void accept(net.minecraft.class_1087 model);

    void accept(net.minecraft.class_1087 model, net.minecraft.class_2680 state);
}
