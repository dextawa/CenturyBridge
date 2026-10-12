package top.dext.centurybridge.step.v1_20_3.rt;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 1.20.2's packets and packet listeners on 1.20.3: resource-pack packets (1.20.3 names each pack by a UUID),
 * the explosion, jigsaw and score packets (1.20.3 added fields; a score removal is its own packet now), and
 * the handlers 1.20.3 added to the listener interfaces a 1.20.2 listener implements.
 */
public final class CbNet {

    // ------------------------------------------------------------------ resource packs

    /**
     * the id 1.20.3 gives a resource pack that comes without one -- the name-based UUID of its URL, as
     * DedicatedServerProperties derives it when resource-pack-id is missing
     */
    public static UUID cbPackId(String url) {
        return UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
    }

    private static volatile UUID lastPushed;

    /** the client listener mixin records each pack the server pushes */
    public static void cbPushed(UUID id) {
        lastPushed = id;
    }

    /**
     * ServerboundResourcePackPacket(action) of 1.20.2 answered about the one pack the server had sent: the pack
     * the server pushed last; none pushed (no pack to answer about), the nil UUID
     */
    public static UUID cbResponsePackId() {
        UUID id = lastPushed;
        return id != null ? id : new UUID(0L, 0L);
    }

    // ------------------------------------------------------------------ explosion, jigsaw

    /**
     * ClientboundExplodePacket(x, y, z, power, toBlow, knockback) of 1.20.2: its client made the explosion
     * with Explosion's defaults -- blocks destroyed with decay, the explosion / explosion-emitter particles,
     * the generic explosion sound, which 1.20.3's packet carries
     */
    public static net.minecraft.class_1927.class_4179 cbLegacyBlockInteraction() {
        return net.minecraft.class_1927.class_4179.field_40879;
    }

    public static net.minecraft.class_2394 cbSmallExplosionParticles() {
        return net.minecraft.class_2398.field_11236;
    }

    public static net.minecraft.class_2394 cbLargeExplosionParticles() {
        return net.minecraft.class_2398.field_11221;
    }

    public static net.minecraft.class_3414 cbExplosionSound() {
        return net.minecraft.class_3417.field_15152;
    }

    /** a jigsaw's selection / placement priority 1.20.2 did not have: 1.20.3's JigsawBlockEntity default */
    public static int cbDefaultPriority() {
        return 0;
    }

    // ------------------------------------------------------------------ scores

    /** a ClientboundSetScorePacket made as 1.20.2's REMOVE: 1.20.3 sends a ClientboundResetScorePacket for it */
    public interface CbLegacyScorePacket {
        boolean cb$remove();

        void cb$markRemove();
    }

    /**
     * ClientboundSetScorePacket(method, objective, owner, score) of 1.20.2: 1.20.3's (owner, objective, score,
     * display, number format) with no display and no format, as 1.20.2 had neither; a REMOVE is marked
     */
    public static net.minecraft.class_2561 cbNoDisplay() {
        return null;
    }

    public static net.minecraft.class_9022 cbNoNumberFormat() {
        return null;
    }

    public static void cbSetScoreBuilt(net.minecraft.class_2757 packet, net.minecraft.class_2995.class_2996 method, String objective,
                                       String owner, int score) {
        if (method == net.minecraft.class_2995.class_2996.field_13430) {
            ((CbLegacyScorePacket) (Object) packet).cb$markRemove();
        }
    }

    /** the packet 1.20.3 sends for a 1.20.2-made one: a REMOVE is a ClientboundResetScorePacket */
    public static net.minecraft.class_2596<?> cbOutgoing(net.minecraft.class_2596<?> packet) {
        if (packet instanceof net.minecraft.class_2757 score && ((CbLegacyScorePacket) (Object) score).cb$remove()) {
            return new net.minecraft.class_9006(score.comp_2122(), score.comp_2123());
        }
        return packet;
    }

    /** a 1.20.3 score reset as the 1.20.2 packet it was: SetScorePacket REMOVE */
    public static net.minecraft.class_2757 cbLegacyRemove(net.minecraft.class_9006 reset) {
        net.minecraft.class_2757 packet = new net.minecraft.class_2757(reset.comp_2120(), reset.comp_2121(), 0, null, null);
        ((CbLegacyScorePacket) (Object) packet).cb$markRemove();
        return packet;
    }

    // ------------------------------------------------------------------ listener handlers 1.20.3 added

    /** marks a 1.20.2 packet listener given the handlers 1.20.3 added (adapterHooks) */
    public interface CbLegacyListener {
    }

    /**
     * handleResetScore, for a 1.20.2 game listener: the reset delivered the way 1.20.2 sent it, as a SetScore
     * REMOVE to handleSetScore (a listener that extends 1.20.3's ClientPacketListener passes it on to the
     * vanilla reset, see the ClientPacketListener mixin)
     */
    public static void cbResetScore(net.minecraft.class_2602 listener, net.minecraft.class_9006 packet) {
        listener.method_11118(cbLegacyRemove(packet));
    }

    /**
     * a packet 1.20.2 did not have -- the server's tick-rate state and steps, a resource pack's removal, a
     * crafter slot's toggle: the handler a vanilla listener above the 1.20.2 one has (the call again from
     * here reaches it, guarded), else none -- a 1.20.2 listener has nothing that handles it
     */
    public static void cbTickingState(net.minecraft.class_2602 listener, net.minecraft.class_8913 packet) {
        listener.method_54806(packet);
    }

    public static void cbTickingStep(net.minecraft.class_2602 listener, net.minecraft.class_8914 packet) {
        listener.method_54807(packet);
    }

    public static void cbResourcePackPop(net.minecraft.class_8705 listener, net.minecraft.class_9053 packet) {
        listener.method_55512(packet);
    }

    public static void cbContainerSlotStateChanged(net.minecraft.class_2792 listener, net.minecraft.class_8875 packet) {
        listener.method_54436(packet);
    }

    private CbNet() {
    }
}
