package top.dext.centurybridge.step.v26_1.mixin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTagBuilderHooks;

/** TagBuilder keeps 1.21.11 Fabric API's removal entries again (1.21.11's TagBuilderMixin: required entries) */
@Mixin(TagBuilder.class)
public abstract class CbFapiTagBuilderMixin implements CbFapiTagBuilderHooks {

    @Unique
    private final List<TagEntry> cb$removes = new ArrayList<>();

    @Override
    public List<TagEntry> cb$remove() {
        return Collections.unmodifiableList(this.cb$removes);
    }

    @Override
    public void cb$removeElement(Identifier id) {
        this.cb$removes.add(TagEntry.element(id));
    }

    @Override
    public void cb$removeTag(Identifier id) {
        this.cb$removes.add(TagEntry.tag(id));
    }
}
