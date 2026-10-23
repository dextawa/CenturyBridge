package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.render.FabricLayerRenderState;
import net.fabricmc.fabric.impl.client.renderer.LayerRenderStateExtension;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render.ItemRenderTypeGetter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

/**
 * 1.21.11's per-layer item render type getter (Renderer.setLayerRenderTypeGetter, FabricLayerRenderState.setRenderTypeGetter)
 * on 26.1, where each mesh quad carries its own item render type. The getter is kept on the layer
 * (mixin/CbFapiLayerRenderStateMixin, cleared with the layer as 1.21.11's Indigo cleared it); when the layer submits,
 * every quad of its mesh whose getter answer is not null takes that render type -- 1.21.11 asked the getter for each
 * quad at render time and used the layer's default for a null answer, which a 26.1 quad already carries. The mesh is
 * the one 26.1's Fabric API keeps on the layer (LayerRenderStateExtension, the field behind FabricLayerRenderState.emitter()).
 */
@Environment(EnvType.CLIENT)
public final class CbFapiLayerRenderTypes {
    private CbFapiLayerRenderTypes() {
    }

    /** the layer's 1.21.11 getter (implemented by mixin/CbFapiLayerRenderStateMixin) */
    public interface Holder {
        void cb$setRenderTypeGetter(ItemRenderTypeGetter getter);
    }

    /** 1.21.11 Renderer.setLayerRenderTypeGetter (Indigo's body: the layer keeps it) */
    public static void setLayerRenderTypeGetter(Renderer renderer, ItemStackRenderState.LayerRenderState layer, ItemRenderTypeGetter getter) {
        ((Holder) (Object) layer).cb$setRenderTypeGetter(getter);
    }

    private static final MethodType SET = MethodType.methodType(void.class, FabricLayerRenderState.class, ItemRenderTypeGetter.class);
    private static final ClassValue<MethodHandle> SET_OVERRIDE = new ClassValue<>() {
        @Override
        protected MethodHandle computeValue(Class<?> type) {
            return CbFapiOverrides.find(type, "setRenderTypeGetter", SET);
        }
    };

    /** a 1.21.11 call of FabricLayerRenderState.setRenderTypeGetter: the receiver's own override, else 1.21.11's default */
    public static void setRenderTypeGetter(FabricLayerRenderState layer, ItemRenderTypeGetter getter) {
        MethodHandle override = SET_OVERRIDE.get(layer.getClass());
        if (override == null) {
            setRenderTypeGetterBase(layer, getter);
            return;
        }
        try {
            override.invokeExact(layer, getter);
        } catch (Throwable t) {
            throw CbFapiOverrides.rethrow(t);
        }
    }

    /** 1.21.11 FabricLayerRenderState.setRenderTypeGetter's default body (a super call reaches it) */
    public static void setRenderTypeGetterBase(FabricLayerRenderState layer, ItemRenderTypeGetter getter) {
        setLayerRenderTypeGetter(Renderer.get(), (ItemStackRenderState.LayerRenderState) (Object) layer, getter);
    }

    /** at the layer's submit: the getter's answers become the mesh quads' item render types */
    public static void apply(ItemStackRenderState.LayerRenderState layer, ItemRenderTypeGetter getter) {
        MutableMesh mesh = ((LayerRenderStateExtension) (Object) layer).fabric_getMutableMesh();
        if (mesh == null || mesh.size() == 0) {
            return;
        }
        mesh.forEachMutable(quad -> {
            RenderType type = getter.renderType(quad.atlas(), quad.chunkLayer());
            if (type != null) {
                quad.itemRenderType(type);
            }
        });
    }
}
