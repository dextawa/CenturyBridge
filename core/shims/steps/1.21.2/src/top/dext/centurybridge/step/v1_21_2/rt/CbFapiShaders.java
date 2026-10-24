package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendering.v1.CoreShaderRegistrationCallback;

/** 1.21.1 Fabric API's core shader registration, run after each ShaderManager reload (see CoreShaderRegistrationCallback) */
@Environment(EnvType.CLIENT)
public final class CbFapiShaders {

    private CbFapiShaders() {
    }

    /** 1.21.1 Fabric API's GameRendererMixin: load every registered shader, then hand each to its callback */
    public static void reloaded() {
        net.minecraft.class_5912 provider = net.minecraft.class_310.method_1551().method_1478();
        List<Pair<net.minecraft.class_5944, Consumer<net.minecraft.class_5944>>> loaded = new ArrayList<>();
        try {
            CoreShaderRegistrationCallback.EVENT.invoker().registerShaders((id, format, callback) ->
                loaded.add(Pair.of(CbRenderShaders.create(provider, id.toString(), format), callback)));
        } catch (java.io.IOException e) {
            throw new RuntimeException("could not reload shaders", e);
        }
        for (Pair<net.minecraft.class_5944, Consumer<net.minecraft.class_5944>> pair : loaded) {
            pair.getSecond().accept(pair.getFirst());
        }
    }
}
