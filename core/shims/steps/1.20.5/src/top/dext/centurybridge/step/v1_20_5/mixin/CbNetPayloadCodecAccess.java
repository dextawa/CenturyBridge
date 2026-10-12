package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_2960;
import net.minecraft.class_9139;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** The payload codec's own lookup of a payload id's codec: registered, else its fallback (CustomPacketPayload$1.findCodec). */
@Mixin(targets = "net.minecraft.class_8710$1")
public interface CbNetPayloadCodecAccess {

    @Invoker("method_56487")
    class_9139<? super net.minecraft.class_2540, ?> cb$findCodec(class_2960 id);
}
