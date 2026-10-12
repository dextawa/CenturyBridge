package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** CompositeEntryBase's protected children (an array in 1.20.1), for the composite serializer and field reads. */
@Mixin(net.minecraft.class_69.class)
public interface CbLootCompositeEntryChildren {

    @Accessor("field_982")
    List<net.minecraft.class_79> cb$children();
}
