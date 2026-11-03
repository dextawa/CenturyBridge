package top.dext.centurybridge.step.v1_21.facade;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.rt.CbRenderBuffers;

/**
 * 1.20.6's SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults (class_846$class_851$class_4578$class_7435,
 * gone in 1.21): what RebuildTask.compile answered. A view over 1.21's SectionCompiler.Results (class_9810$class_9811,
 * the same five fields in the same order), made by the restored compile (CbRenderRebuildTaskRestoredMixin):
 * <ul><li>globalBlockEntities / blockEntities: the Results' own lists (1.20.6 field_39079 / field_39080 are 1.21's
 * field_52166 / field_52167: handleBlockEntity adds to them in the same order, javap of both);
 * <li>renderedLayers: each layer's MeshData as the layer's RenderedBuffer facade, the same conversion as the
 * restored BufferBuilder.end();
 * <li>visibilitySet: the Results' value;
 * <li>transparencyState: 1.20.6's SortState rebuilt from 1.21's -- 1.21 keeps one only for a QUADS translucent layer
 * (MeshData.sortQuads, class_9801.method_60819), so mode QUADS, the vertex count of the translucent layer's mesh, 1.21's
 * points, and the sorting compile was given.</ul>
 * The view never releases a buffer: the caller owns them, as 1.20.6's caller owned the RenderedBuffers (release()
 * closes the mesh, once). {@link #cbResults()} writes the view's writable state back into the Results before 1.21 code
 * reads them (the restored handleBlockEntity); a SortState written back carries only its points (1.21's keeps no mode,
 * vertex count or sorting: a known difference, release_notes_known.md). Members 1.20.6 kept package-private are
 * public here: a mod's access widener may name them, and wideners never reach a CenturyBridge class.
 */
@Environment(EnvType.CLIENT)
public final class RebuildTaskCompileResults {

    /** globalBlockEntities */
    public final List<net.minecraft.class_2586> field_39079;

    /** blockEntities */
    public final List<net.minecraft.class_2586> field_39080;

    /** renderedLayers */
    public final Map<net.minecraft.class_1921, BufferBuilderRenderedBuffer> field_39081 =
        new it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap<>();

    /** visibilitySet */
    public net.minecraft.class_854 field_39082;

    /** transparencyState */
    public BufferBuilderSortState field_39083;

    private final net.minecraft.class_9810.class_9811 cbResults;

    /** the SortState this view built, to tell a mod's write from it */
    private BufferBuilderSortState cbBuiltSort;

    /** 1.20.6's (package-private) constructor: empty results, over a fresh 1.21 Results */
    public RebuildTaskCompileResults() {
        this(new net.minecraft.class_9810.class_9811(), null);
    }

    private RebuildTaskCompileResults(net.minecraft.class_9810.class_9811 results, net.minecraft.class_8251 sorting) {
        this.cbResults = results;
        this.field_39079 = results.field_52166;
        this.field_39080 = results.field_52167;
        for (Map.Entry<net.minecraft.class_1921, net.minecraft.class_9801> e : results.field_52168.entrySet()) {
            this.field_39081.put(e.getKey(), CbRenderBuffers.cbAsRendered(e.getValue()));
        }
        this.field_39082 = results.field_52169;
        net.minecraft.class_9801.class_9802 sort = results.field_52170;
        net.minecraft.class_9801 translucent = results.field_52168.get(net.minecraft.class_1921.method_23583());
        if (sort != null && translucent != null) {
            this.field_39083 = new BufferBuilderSortState(net.minecraft.class_293.class_5596.field_27382,
                translucent.method_60822().comp_750(), sort.comp_2840(), sorting);
        }
        this.cbBuiltSort = this.field_39083;
    }

    /** a view of the Results 1.21's SectionCompiler.compile answered with the sorting it was given */
    public static RebuildTaskCompileResults cbOf(net.minecraft.class_9810.class_9811 results, net.minecraft.class_8251 sorting) {
        return new RebuildTaskCompileResults(results, sorting);
    }

    /** the Results under the view, the view's writable state written back first (layers, visibility, sort points) */
    public net.minecraft.class_9810.class_9811 cbResults() {
        this.cbResults.field_52169 = this.field_39082;
        Map<net.minecraft.class_1921, net.minecraft.class_9801> layers = this.cbResults.field_52168;
        layers.keySet().retainAll(this.field_39081.keySet());
        for (Map.Entry<net.minecraft.class_1921, BufferBuilderRenderedBuffer> e : this.field_39081.entrySet()) {
            net.minecraft.class_9801 mesh = e.getValue() == null ? null : e.getValue().cbMesh();
            if (mesh != null) {
                layers.put(e.getKey(), mesh);
            } else {
                layers.remove(e.getKey());
            }
        }
        if (this.field_39083 != this.cbBuiltSort) {
            BufferBuilderSortState s = this.field_39083;
            if (s == null) {
                this.cbResults.field_52170 = null;
            } else {
                net.minecraft.class_9801 translucent = layers.get(net.minecraft.class_1921.method_23583());
                net.minecraft.class_293.class_5595 index = translucent != null ? translucent.method_60822().comp_753()
                    : net.minecraft.class_293.class_5595.method_31972(s.field_27359); // as 1.21's BufferBuilder picks it: least(vertex count)
                this.cbResults.field_52170 = new net.minecraft.class_9801.class_9802(s.field_27360, index);
            }
            this.cbBuiltSort = s;
        }
        return this.cbResults;
    }
}
