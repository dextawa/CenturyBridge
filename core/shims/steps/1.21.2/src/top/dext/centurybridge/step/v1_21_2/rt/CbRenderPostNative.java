package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.facade.PostChain;

/**
 * 1.21.1's GameRenderer.currentEffect() over 1.21.2's post effect.
 *
 * <p>1.21.1's GameRenderer kept the PostChain of its entity post effect (creeper / spider / invert, loaded from
 * shaders/post/&lt;name&gt;.json by checkEntityPostEffect) and ran it each frame while the effect was active;
 * currentEffect() answered that chain, shutdownEffect() closed and dropped it. 1.21.2's GameRenderer keeps the
 * effect's id (the ShaderManager builds and caches the chain of that post_effect) and runs it the same way.
 *
 * <p>The 1.21.1 chain answered for an id is this one: named as 1.21.1 named the chain it loaded
 * (&lt;namespace&gt;:shaders/post/&lt;path&gt;.json), over the main render target; process(partialTick) runs 1.21.2's chain
 * of the id as GameRenderer does (main target, the renderer's resource pool), setUniform sets the uniform on it. It
 * has no 1.21.1 passes or targets of its own: 1.21.2's chain sizes its targets to the frame it runs in, so resize only
 * updates 1.21.1's recorded size and projection, and close() frees nothing (the ShaderManager owns the chain).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderPostNative extends PostChain {

    private final net.minecraft.class_2960 id;

    private CbRenderPostNative(net.minecraft.class_2960 id) {
        super(net.minecraft.class_310.method_1551().method_1478(), net.minecraft.class_310.method_1551().method_1522(),
            id.method_45136("shaders/post/" + id.method_12832() + ".json").toString());
        this.id = id;
    }

    /** 1.21.1's chain for a 1.21.2 post effect id */
    public static CbRenderPostNative of(net.minecraft.class_2960 id) {
        return new CbRenderPostNative(id);
    }

    public net.minecraft.class_2960 id() {
        return this.id;
    }

    private net.minecraft.class_279 chain() {
        return net.minecraft.class_310.method_1551().method_62887().method_62941(this.id, net.minecraft.class_9960.field_53902);
    }

    @Override
    public void method_1258(float partialTick) {
        net.minecraft.class_279 chain = this.chain();
        if (chain != null) {
            net.minecraft.class_310 minecraft = net.minecraft.class_310.method_1551();
            chain.method_1258(minecraft.method_1522(), ((CbRenderFrames.ResourcePool) minecraft.field_1773).cb$resourcePool());
        }
    }

    @Override
    public void method_57799(String name, float value) {
        net.minecraft.class_279 chain = this.chain();
        if (chain != null) {
            chain.method_57799(name, value);
        }
    }
}
