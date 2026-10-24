package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** TagEntry's id and whether it names a tag (rt/CbFapiTagRemovals rebuilds a removal as an optional entry) */
@Mixin(TagEntry.class)
public interface CbFapiTagEntryAccess {

    @Accessor("id")
    Identifier cb$id();

    @Accessor("tag")
    boolean cb$tag();
}
