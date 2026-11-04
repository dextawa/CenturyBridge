package top.dext.centurybridge.step.v1_21_6.mixin;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2586;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's SectionCompiler.Results.globalBlockEntities (field_52166, a List of BlockEntity), restored: 1.21.5's
 * handleBlockEntity (method_60902) added a block entity there when its renderer said shouldRenderOffScreen(), and to
 * blockEntities otherwise. 1.21.6 dropped the list and keeps those block entities in ClientLevel instead
 * (onBlockEntityAdded, method_71970: the same test, its globallyRenderedBlockEntities set). The list is filled by
 * CbSectionResultsRestoredMixinHooks at handleBlockEntity's head, in the order the compile meets them, as 1.21.5's was;
 * a converted read reaches it through instanceFieldGetters (rt/CbS6SectionField#field_52166). Priority below every mod's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = net.minecraft.class_9810.class_9811.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSectionResultsRestoredMixin implements CbPrioritySplit.SectionGlobalBlockEntities {

    @Unique
    private final List<class_2586> cb$globalBlockEntities = new ArrayList<>();

    @Override
    public List<class_2586> cb$v1_21_6$globalBlockEntities() {
        return this.cb$globalBlockEntities;
    }
}
