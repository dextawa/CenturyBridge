package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2596;
import net.minecraft.class_2818;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_1923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyChunkTrackerOverride;

/**
 * 1.20.1's chunk map sent each chunk to a player through player.trackChunk(pos, packet) and had it
 * forgotten through player.untrackChunk(pos); 1.20.2's PlayerChunkSender sends the packets itself.
 * For a converted player overriding either (CbLegacyChunkTrackerOverride) the sender hands the
 * same packet / position to the override instead; every other player is left to 1.20.2.
 */
@Mixin(net.minecraft.class_8608.class)
public abstract class CbPlayerChunkSenderLegacy {

    /** sendChunk(connection, level, chunk): the chunk packet */
    @Redirect(method = "method_52388", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3244;method_14364(Lnet/minecraft/class_2596;)V", ordinal = 0))
    private static void cb$sendChunk(class_3244 connection, class_2596<?> packet,
                                     class_3244 connectionArg, class_3218 level, class_2818 chunk) {
        if (connection.field_14140 instanceof CbLegacyChunkTrackerOverride legacy) {
            legacy.method_14205(chunk.method_12004(), packet);
        } else {
            connection.method_14364(packet);
        }
    }

    /** dropChunk(player, pos): the forget packet for a chunk that was sent */
    @Redirect(method = "method_52387", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3244;method_14364(Lnet/minecraft/class_2596;)V"))
    private void cb$dropChunk(class_3244 connection, class_2596<?> packet, class_3222 player, class_1923 pos) {
        if (player instanceof CbLegacyChunkTrackerOverride legacy) {
            legacy.method_14246(pos);
        } else {
            connection.method_14364(packet);
        }
    }
}
