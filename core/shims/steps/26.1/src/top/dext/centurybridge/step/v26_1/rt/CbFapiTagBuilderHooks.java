package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagEntry;

/** a TagBuilder's 1.21.11 Fabric API removal entries (mixin/CbFapiTagBuilderMixin) */
public interface CbFapiTagBuilderHooks {
    List<TagEntry> cb$remove();

    void cb$removeElement(Identifier id);

    void cb$removeTag(Identifier id);
}
