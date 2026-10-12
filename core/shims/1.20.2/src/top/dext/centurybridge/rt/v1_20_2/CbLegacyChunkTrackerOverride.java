package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks a converted ServerPlayer class overriding 1.20.1's trackChunk(pos, packet) or
 * untrackChunk(pos): 1.20.1's chunk map sent a chunk to a player through trackChunk and told it to
 * forget one through untrackChunk; 1.20.2's PlayerChunkSender sends both itself. For such a player
 * the sender goes through the overrides again (CbPlayerChunkSenderLegacy).
 */
public interface CbLegacyChunkTrackerOverride {

    /** 1.20.1 trackChunk(pos, packet) */
    void method_14205(net.minecraft.class_1923 pos, net.minecraft.class_2596<?> packet);

    /** 1.20.1 untrackChunk(pos) */
    void method_14246(net.minecraft.class_1923 pos);
}
