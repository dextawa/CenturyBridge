package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.facade.CompileResults;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderRebuildTaskAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderSectionAccess;

/**
 * 1.20.6's RebuildTask.compile(x, y, z, pack) (method_22785) and handleBlockEntity(results, blockEntity) (method_23087), for a
 * converted mod's calls (instanceRedirects; private in 1.20.6, reached through the mod's access widener). 1.21 moved both into
 * SectionCompiler (method_60904 / method_60902); each static here calls 1.21's SectionCompiler on the game's own objects, so
 * every native SectionCompiler mixin -- and a 1.20.6 mod's injectors smixin18's mixinMoves carry there -- runs for these calls
 * too. No 1.20.6 body is copied, and nothing is restored on RebuildTask (s121 C20, shape agreed with smixin18).
 */
@Environment(EnvType.CLIENT)
public final class CbSectionCompile {

    private CbSectionCompile() {
    }

    /**
     * compile(x, y, z, pack): as 1.20.6 took the task's region (and cleared it), sorted the translucent layer by distance from
     * the section origin (VertexSorting.byDistance(x - ox, y - oy, z - oz), 1.20.6's javap), compiled by 1.21's
     * SectionCompiler; the answer is a view of 1.21's Results. Without a region 1.20.6 answered empty results with an empty
     * graph's visibility.
     */
    public static CompileResults compile(net.minecraft.class_846.class_851.class_4578 task, float x, float y, float z, net.minecraft.class_750 pack) {
        CbRenderRebuildTaskAccess t = (CbRenderRebuildTaskAccess) (Object) task;
        net.minecraft.class_853 region = t.cb$region();
        t.cb$setRegion(null);
        if (region == null) {
            CompileResults empty = new CompileResults();
            empty.field_39082 = new net.minecraft.class_852().method_3679();
            return empty;
        }
        net.minecraft.class_846.class_851 section = t.cb$section();
        net.minecraft.class_2338 origin = section.method_3670().method_10062();
        net.minecraft.class_8251 sorting = net.minecraft.class_8251.method_49906(x - origin.method_10263(), y - origin.method_10264(),
            z - origin.method_10260());
        net.minecraft.class_9810.class_9811 results = ((CbRenderSectionAccess) (Object) section).cb$dispatcher().field_52171.method_60904(
            net.minecraft.class_4076.method_18682(origin), region, sorting, pack);
        return CompileResults.cbOf(results, sorting);
    }

    /** handleBlockEntity(results, blockEntity): 1.21's SectionCompiler.handleBlockEntity on the view's Results (its own lists) */
    public static void handleBlockEntity(net.minecraft.class_846.class_851.class_4578 task, CompileResults results, net.minecraft.class_2586 blockEntity) {
        net.minecraft.class_846.class_851 section = ((CbRenderRebuildTaskAccess) (Object) task).cb$section();
        ((CbRenderSectionAccess) (Object) section).cb$dispatcher().field_52171.method_60902(results.cbResults(), blockEntity);
    }
}
