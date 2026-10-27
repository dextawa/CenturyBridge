package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collections;
import java.util.List;
import net.minecraft.class_3497;
import net.minecraft.class_7475;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricTagFile;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestFapiTags;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * fabric-tag-api-v1's TagFileMixin (0.116, Apache-2.0, FabricMC) on 1.21.2's TagFile: the file keeps its
 * "fabric:remove" entries, read by the codec 0.116 wrapped around TagFile.CODEC (built where 1.21.2's static
 * initializer builds it; CbRestFapiTagFileMixinHooks, default priority).
 */
@Mixin(value = class_7475.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestFapiTagFileMixin implements FabricTagFile, CbRestFapiTags.TagFileHooks {

    @Unique
    private List<class_3497> cb$remove = Collections.emptyList();

    @Override
    public List<class_3497> remove() {
        return Collections.unmodifiableList(this.cb$remove);
    }

    @Override
    public void cb$setRemove(List<class_3497> remove) {
        this.cb$remove = remove;
    }
}
