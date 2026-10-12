package top.dext.centurybridge.step.v1_20_5.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.class_9136;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** IdDispatchCodec's id table (packet type -> wire id): which packets a protocol's codec knows. */
@Mixin(class_9136.class)
public interface CbNetDispatchAccess {

    @Accessor("field_48579")
    Object2IntMap<Object> cb$toId();
}
