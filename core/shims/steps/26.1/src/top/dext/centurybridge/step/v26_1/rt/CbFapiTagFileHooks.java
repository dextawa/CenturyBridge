package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import net.minecraft.tags.TagEntry;

/** the write side of a TagFile's 1.21.11 "fabric:remove" entries (mixin/CbFapiTagFileMixin) */
public interface CbFapiTagFileHooks {
    void cb$setRemove(List<TagEntry> remove);
}
