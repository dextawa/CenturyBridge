package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** LeafEntry's protected weight / quality / functions, for the 1.20.1 serializer facade. */
@Mixin(net.minecraft.class_85.class)
public interface LeafEntryAccessor {

    @Accessor("field_995")
    int cb$weight();

    @Accessor("field_994")
    int cb$quality();

    @Accessor("field_996")
    List<net.minecraft.class_117> cb$functions();
}
