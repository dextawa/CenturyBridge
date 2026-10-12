package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** PlayerChunkSender's set of chunks waiting to be sent (CbServerPlayerLegacy.untrackChunk). */
@Mixin(net.minecraft.class_8608.class)
public interface CbPlayerChunkSenderAccess {

    @Accessor("field_45005")
    it.unimi.dsi.fastutil.longs.LongSet cb$pending();
}
