package top.dext.centurybridge.step.v1_21_4.compat.fapi.renderer;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.UnwrappableBakedModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import top.dext.centurybridge.step.v1_21_4.facade.BakedOverrides;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemRender;

/**
 * Fabric API 1.21.3's ForwardingBakedModel on 1.21.4 (gone there; 1.21.4's DelegateBakedModel takes its parent in the constructor,
 * this one is set by the subclass). Every member forwards to {@link #wrapped} as 1.21.3's did; 1.21.3's WrapperBakedModel is 1.21.4's
 * UnwrappableBakedModel. The 1.21.3-shaped emit methods forward through {@link CbFapiRenderer} (a wrapped 1.21.3 model gets the same
 * context), the 1.21.4 shapes forward directly; a subclass's override of a 1.21.3 shape is reconnected to the 1.21.4 one by the
 * step's override hooks. getOverrides / isCustomRenderer, gone from 1.21.4's BakedModel, answer the wrapped model's (CbItemRender).
 */
@Environment(EnvType.CLIENT)
public abstract class ForwardingBakedModel implements net.minecraft.class_1087, FabricBakedModel, UnwrappableBakedModel,
    CbItemRender.CbLegacyOverrides, CbItemRender.CbLegacyCustomRenderer {
    public net.minecraft.class_1087 wrapped;

    public ForwardingBakedModel() {
    }

    @Override
    public boolean isVanillaAdapter() {
        return ((FabricBakedModel) this.wrapped).isVanillaAdapter();
    }

    public void emitBlockQuads(net.minecraft.class_1920 blockView, net.minecraft.class_2680 state, net.minecraft.class_2338 pos,
        Supplier<net.minecraft.class_5819> randomSupplier, RenderContext context) {
        CbFapiRenderer.emitBlockQuads(this.wrapped, blockView, state, pos, randomSupplier, context);
    }

    public void emitItemQuads(net.minecraft.class_1799 stack, Supplier<net.minecraft.class_5819> randomSupplier, RenderContext context) {
        CbFapiRenderer.emitItemQuads(this.wrapped, stack, randomSupplier, context);
    }

    @Override
    public void emitBlockQuads(QuadEmitter emitter, net.minecraft.class_1920 blockView, net.minecraft.class_2680 state, net.minecraft.class_2338 pos,
        Supplier<net.minecraft.class_5819> randomSupplier, Predicate<net.minecraft.class_2350> cullTest) {
        ((FabricBakedModel) this.wrapped).emitBlockQuads(emitter, blockView, state, pos, randomSupplier, cullTest);
    }

    @Override
    public void emitItemQuads(QuadEmitter emitter, Supplier<net.minecraft.class_5819> randomSupplier) {
        ((FabricBakedModel) this.wrapped).emitItemQuads(emitter, randomSupplier);
    }

    @Override
    public List<net.minecraft.class_777> method_4707(net.minecraft.class_2680 state, net.minecraft.class_2350 face, net.minecraft.class_5819 random) {
        return this.wrapped.method_4707(state, face, random);
    }

    @Override
    public boolean method_4708() {
        return this.wrapped.method_4708();
    }

    @Override
    public boolean method_4712() {
        return this.wrapped.method_4712();
    }

    /** was isCustomRenderer(), gone from 1.21.4's BakedModel */
    public boolean method_4713() {
        return CbItemRender.isCustomRenderer(this.wrapped);
    }

    @Override
    public boolean cb$isCustomRenderer() {
        return CbItemRender.isCustomRenderer(this.wrapped);
    }

    @Override
    public net.minecraft.class_1058 method_4711() {
        return this.wrapped.method_4711();
    }

    @Override
    public boolean method_24304() {
        return this.wrapped.method_24304();
    }

    @Override
    public net.minecraft.class_809 method_4709() {
        return this.wrapped.method_4709();
    }

    /** was getOverrides(), gone from 1.21.4's BakedModel */
    public BakedOverrides method_4710() {
        return CbItemRender.getOverrides(this.wrapped);
    }

    @Override
    public BakedOverrides cb$getOverrides() {
        return CbItemRender.getOverrides(this.wrapped);
    }

    @Override
    public net.minecraft.class_1087 getWrappedModel() {
        return this.wrapped;
    }
}
