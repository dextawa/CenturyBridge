package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2818;
import net.minecraft.class_3218;
import net.minecraft.class_3244;
import net.minecraft.class_8608;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestAttachmentSync;

/**
 * 1.21.1's Fabric API (0.116) sent a player a chunk's synced attachments right after the chunk
 * (ChunkDataSenderMixin): PlayerChunkSender.sendChunk, at its end -- the chunk's and its block entities'.
 */
@Mixin(class_8608.class)
public abstract class CbRestAttachmentChunkMixin {

    @Inject(method = "method_52388", at = @At("TAIL"))
    private static void cb$chunkSent(class_3244 connection, class_3218 level, class_2818 chunk, CallbackInfo ci) {
        CbRestAttachmentSync.chunkSent(connection.field_14140, chunk);
    }
}
