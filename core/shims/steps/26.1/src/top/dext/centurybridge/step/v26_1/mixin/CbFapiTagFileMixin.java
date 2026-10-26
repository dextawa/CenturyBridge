package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Collections;
import java.util.List;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.compat.fapi.tag.v1.FabricTagFile;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTagFileHooks;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * TagFile carries 1.21.11's "fabric:remove" entries again and is a 1.21.11 FabricTagFile: its CODEC reads and
 * writes the list (1.21.11's TagFileMixin; rt/CbFapiTagRemovals.modifyTagFileCodec, CbFapiTagFileReadsMixin).
 */
@Mixin(value = TagFile.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFapiTagFileMixin implements FabricTagFile, CbFapiTagFileHooks {

    @Unique
    private List<TagEntry> cb$remove = Collections.emptyList();

    @Override
    public List<TagEntry> remove() {
        return Collections.unmodifiableList(this.cb$remove);
    }

    @Override
    public void cb$setRemove(List<TagEntry> remove) {
        this.cb$remove = remove;
    }
}
