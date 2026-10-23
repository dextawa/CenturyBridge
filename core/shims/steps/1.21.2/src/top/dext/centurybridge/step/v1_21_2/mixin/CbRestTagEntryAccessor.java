package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2960;
import net.minecraft.class_3497;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** TagEntry.id / tag (private), read by fabric-tag-api-v1's removal entries (rt/CbRestFapiTags) */
@Mixin(class_3497.class)
public interface CbRestTagEntryAccessor {

    @Accessor("field_15584")
    class_2960 cb$id();

    @Accessor("field_39267")
    boolean cb$tag();
}
