package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.ModelBakery;
import top.dext.centurybridge.step.v26_2.facade.MultiBufferSource;

/**
 * 26.1.2's RenderBuffers.bufferSource() and crumblingBufferSource() (javap of both), restored on RenderBuffers
 * (mixin/CbS26RenderBuffersMixin) as 26.1.2 built them: the main source over a 786432-byte shared buffer with fixed
 * buffers, in order, for the block-item and item sheets, the glints and the water mask; the crumbling source over an
 * empty shared buffer with one fixed buffer per destroy stage. 26.1.2 backed the two block-item sheets with the
 * section pack's CUTOUT / TRANSLUCENT buffers; 26.2's section pack is its own, so each fixed type gets a buffer of its
 * own, at 26.1.2's default initial size (RenderSetup's 1536 bytes; 26.2's RenderType has no size, and a ByteBufferBuilder
 * grows as written).
 *
 * <p>26.1.2's vanilla ended their batches where its renderers had drawn (mixin/CbS26BufferFlushMixin, the same phases
 * in 26.2): the main pass after the solid features, after the translucent features (with the crumbling source), after
 * the translucent features drawn over the terrain; the hand after its features; the level after the screen effects;
 * the GUI before it prepared its elements. A 26.1.2 mod's geometry in these sources is drawn there.
 */
@Environment(EnvType.CLIENT)
public final class CbS26RenderBuffers {
    private CbS26RenderBuffers() {}

    /** 26.1.2's members, restored on RenderBuffers */
    public interface Holder {
        MultiBufferSource.BufferSource bufferSource();

        MultiBufferSource.BufferSource crumblingBufferSource();
    }

    private static final int DEFAULT_BUFFER_SIZE = 1536;

    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> map, RenderType type) {
        map.put(type, new ByteBufferBuilder(DEFAULT_BUFFER_SIZE));
    }

    public static MultiBufferSource.BufferSource newBufferSource() {
        Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> fixed = new Object2ObjectLinkedOpenHashMap<>();
        put(fixed, Sheets.cutoutBlockItemSheet());
        put(fixed, Sheets.translucentBlockItemSheet());
        put(fixed, Sheets.cutoutItemSheet());
        put(fixed, Sheets.translucentItemSheet());
        put(fixed, RenderTypes.armorEntityGlint());
        put(fixed, RenderTypes.glint());
        put(fixed, RenderTypes.glintTranslucent());
        put(fixed, RenderTypes.entityGlint());
        put(fixed, RenderTypes.waterMask());
        return MultiBufferSource.immediateWithBuffers(fixed, new ByteBufferBuilder(786432));
    }

    public static MultiBufferSource.BufferSource newCrumblingBufferSource() {
        Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> fixed = new Object2ObjectLinkedOpenHashMap<>();
        ModelBakery.DESTROY_TYPES.forEach(type -> put(fixed, type));
        return MultiBufferSource.immediateWithBuffers(fixed, new ByteBufferBuilder(0));
    }

    public static Holder of(RenderBuffers buffers) {
        return (Holder) buffers;
    }

    /** instance redirect: 26.1.2 RenderBuffers.bufferSource() */
    public static MultiBufferSource.BufferSource bufferSource(RenderBuffers self) {
        return of(self).bufferSource();
    }

    /** instance redirect: 26.1.2 RenderBuffers.crumblingBufferSource() */
    public static MultiBufferSource.BufferSource crumblingBufferSource(RenderBuffers self) {
        return of(self).crumblingBufferSource();
    }

    /** the game's render buffers, as 26.1.2's GuiRenderer held their main source */
    public static Holder game() {
        return of(Minecraft.getInstance().gameRenderer.renderBuffers());
    }
}
