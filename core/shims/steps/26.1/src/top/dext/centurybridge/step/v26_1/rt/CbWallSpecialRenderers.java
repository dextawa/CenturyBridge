package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * 1.21.11's SpecialModelRenderer.submit(argument, displayContext, pose, collector, light, overlay, foil, outline)
 * (and NoDataSpecialModelRenderer's submit(displayContext, ...)) on 26.1's, which dropped the display context.
 * 1.21.11's item rendering handed a special renderer the display context of the item being rendered; block
 * rendering handed it NONE. 26.1's item render state still knows its display context
 * (mixin/CbWallItemStackRenderStateMixin records it while the state submits), so a 1.21.11 special renderer is
 * asked with that context, and with NONE outside an item. A mod's own call passes its context on to a 1.21.11
 * renderer and drops it for 26.1's (whose bodies never read one).
 */
@Environment(EnvType.CLIENT)
public final class CbWallSpecialRenderers {

    private CbWallSpecialRenderers() {
    }

    private static final ThreadLocal<ItemDisplayContext> CONTEXT = new ThreadLocal<>();

    /** an item render state starts submitting: its display context, the one in effect before (restored after) */
    public static ItemDisplayContext enter(ItemDisplayContext context) {
        ItemDisplayContext outer = CONTEXT.get();
        CONTEXT.set(context);
        return outer;
    }

    public static void exit(ItemDisplayContext outer) {
        if (outer == null) {
            CONTEXT.remove();
        } else {
            CONTEXT.set(outer);
        }
    }

    private static ItemDisplayContext context() {
        ItemDisplayContext context = CONTEXT.get();
        return context != null ? context : ItemDisplayContext.NONE;
    }

    /** 1.21.11's SpecialModelRenderer.submit, declared by a mod's renderer (erased: its bridge method) */
    public interface LegacySubmit {
        void submit(Object argument, ItemDisplayContext context, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean foil,
            int outline);
    }

    /** 1.21.11's NoDataSpecialModelRenderer.submit, declared by a mod's renderer */
    public interface LegacyNoDataSubmit {
        void submit(ItemDisplayContext context, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean foil, int outline);
    }

    /** 26.1's submit(argument, ...) of a mod renderer: its 1.21.11 submit with the display context in effect */
    public static void submit(LegacySubmit self, Object argument, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean foil,
        int outline) {
        self.submit(argument, context(), pose, collector, light, overlay, foil, outline);
    }

    /** 26.1's NoDataSpecialModelRenderer.submit(...) of a mod renderer: its 1.21.11 submit with the display context in effect */
    public static void submitNoData(LegacyNoDataSubmit self, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean foil,
        int outline) {
        self.submit(context(), pose, collector, light, overlay, foil, outline);
    }

    /** a mod's call of 1.21.11's SpecialModelRenderer.submit(argument, context, ...) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void callSubmit(SpecialModelRenderer renderer, Object argument, ItemDisplayContext context, PoseStack pose, SubmitNodeCollector collector,
        int light, int overlay, boolean foil, int outline) {
        if (renderer instanceof LegacySubmit legacy) {
            legacy.submit(argument, context, pose, collector, light, overlay, foil, outline);
        } else if (renderer instanceof LegacyNoDataSubmit legacy) {
            legacy.submit(context, pose, collector, light, overlay, foil, outline);
        } else {
            renderer.submit(argument, pose, collector, light, overlay, foil, outline);
        }
    }

    /** a mod's call of 1.21.11's NoDataSpecialModelRenderer.submit(context, ...) */
    public static void callSubmitNoData(NoDataSpecialModelRenderer renderer, ItemDisplayContext context, PoseStack pose, SubmitNodeCollector collector,
        int light, int overlay, boolean foil, int outline) {
        if (renderer instanceof LegacyNoDataSubmit legacy) {
            legacy.submit(context, pose, collector, light, overlay, foil, outline);
        } else {
            renderer.submit(pose, collector, light, overlay, foil, outline);
        }
    }

    /** a mod's call of 1.21.11's NoDataSpecialModelRenderer.submit(null, context, ...) */
    public static void callSubmitVoid(NoDataSpecialModelRenderer renderer, Void argument, ItemDisplayContext context, PoseStack pose,
        SubmitNodeCollector collector, int light, int overlay, boolean foil, int outline) {
        callSubmitNoData(renderer, context, pose, collector, light, overlay, foil, outline);
    }
}
