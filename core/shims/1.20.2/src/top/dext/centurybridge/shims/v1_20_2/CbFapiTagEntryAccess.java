package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** A tag entry's id and whether it names a tag (1.20.1's tag API TagEntryAccessor). */
@Mixin(net.minecraft.class_3497.class)
public interface CbFapiTagEntryAccess {

    @Accessor("field_15584")
    net.minecraft.class_2960 cb$id();

    @Accessor("field_39267")
    boolean cb$tag();
}
