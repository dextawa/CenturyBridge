package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.facade.RebuildTaskCompileResults;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * 1.20.6's RebuildTask.compile(x, y, z, pack) (method_22785) and handleBlockEntity(results, blockEntity) (method_23087),
 * given back as callable members (s121 C20, option (a)): 1.21 moved the meshing into SectionCompiler.compile
 * (class_9810.method_60904) and handleBlockEntity into SectionCompiler (method_60902), and RebuildTask.doTask calls
 * SectionCompiler itself. Each restored member calls 1.21's SectionCompiler (no copied body), so 1.21's own code and every
 * native mixin into SectionCompiler (the layer's CbRenderSectionCompileRouteMixin included) runs for these calls too.
 * 1.21's doTask is not routed through them: a converted mod's injectors inside 1.20.6's compile body stay open (not
 * marked CbVanillaReads).
 * <p>Public, though 1.20.6 kept them private: a mod reaches them through its access widener, which the loader applies
 * before Mixin merges these members, so a private merge would stay private to the mod's call.
 */
@Mixin(targets = "net.minecraft.class_846$class_851$class_4578", priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderRebuildTaskRestoredMixin {

    @Shadow
    protected net.minecraft.class_853 field_20838;

    @Shadow
    @Final
    net.minecraft.class_846.class_851 field_20839;

    /**
     * compile(x, y, z, pack): as 1.20.6 took the task's region (and cleared it), with the camera position sorting the
     * translucent layer by distance from the section origin (VertexSorting.byDistance(x - ox, y - oy, z - oz), 1.20.6's
     * javap), compiled by 1.21's SectionCompiler; the answer is a view of 1.21's Results. Without a region (taken already)
     * 1.20.6 answered empty results with an empty graph's visibility.
     */
    public RebuildTaskCompileResults method_22785(float x, float y, float z, net.minecraft.class_750 pack) {
        net.minecraft.class_853 region = this.field_20838;
        this.field_20838 = null;
        if (region == null) {
            RebuildTaskCompileResults empty = new RebuildTaskCompileResults();
            empty.field_39082 = new net.minecraft.class_852().method_3679();
            return empty;
        }
        net.minecraft.class_2338 origin = this.field_20839.method_3670().method_10062();
        net.minecraft.class_8251 sorting = net.minecraft.class_8251.method_49906(x - origin.method_10263(), y - origin.method_10264(),
            z - origin.method_10260());
        net.minecraft.class_9810.class_9811 results = this.field_20839.field_20833.field_52171.method_60904(
            net.minecraft.class_4076.method_18682(origin), region, sorting, pack);
        return RebuildTaskCompileResults.cbOf(results, sorting);
    }

    /** handleBlockEntity(results, blockEntity): 1.21's SectionCompiler.handleBlockEntity on the view's Results (its own lists) */
    public void method_23087(RebuildTaskCompileResults results, net.minecraft.class_2586 blockEntity) {
        this.field_20839.field_20833.field_52171.method_60902(results.cbResults(), blockEntity);
    }
}
