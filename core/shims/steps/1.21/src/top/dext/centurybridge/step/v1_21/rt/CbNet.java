package top.dext.centurybridge.step.v1_21.rt;

import com.google.common.collect.MapMaker;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * 1.20.6 network API (packet listeners, Connection, the packets whose shape changed, ProtocolInfoBuilder)
 * on 1.21. Common side: nothing here names a client-only type (the client-only parts are CbNetClient,
 * reached by static calls on branches a dedicated server never takes).
 */
public final class CbNet {

    private CbNet() {
    }

    // ------------------------------------------------------------------ disconnect reasons (class_9812)

    /**
     * Since 1.21 a disconnect hands listeners a DisconnectionDetails (the reason plus, for a crash, the
     * report file and a bug-report link) where 1.20.6 handed the reason alone. A 1.20.6 method gets the
     * reason; when it hands that same Component on (to super, back to vanilla) the details it came from go
     * with it, so vanilla still sees the report it made. A reason a 1.20.6 caller made itself carries
     * nothing more, exactly as in 1.20.6 (DisconnectionDetails(Component) is 1.21's own reason-only form).
     * Keys are the Component objects themselves (identity, weak): the same reason object is the one the
     * details were made with.
     */
    private static final Map<net.minecraft.class_2561, net.minecraft.class_9812> DETAILS =
        new MapMaker().weakKeys().makeMap();

    /** the reason of 1.21's disconnect details, the value a 1.20.6 method was handed (null stays null) */
    public static net.minecraft.class_2561 cbReasonOf(net.minecraft.class_9812 details) {
        if (details == null) {
            return null;
        }
        net.minecraft.class_2561 reason = details.comp_2853();
        if (reason != null) {
            DETAILS.put(reason, details);
        }
        return reason;
    }

    /** the details a 1.20.6 reason stands for: the ones it came out of, else the reason alone */
    public static net.minecraft.class_9812 cbDetailsOf(net.minecraft.class_2561 reason) {
        if (reason == null) {
            return null;
        }
        net.minecraft.class_9812 d = DETAILS.get(reason);
        return d != null ? d : new net.minecraft.class_9812(reason);
    }

    /**
     * Connection.getDisconnectedReason() of 1.20.6 is getDisconnectionDetails() since 1.21: the reason of
     * the details, asked virtually, so a 1.20.6 Connection subclass's own reason answers (its override is
     * what 1.21's getDisconnectionDetails runs, see the override hook).
     */
    public static net.minecraft.class_2561 cbDisconnectedReason(net.minecraft.class_2535 connection) {
        return cbReasonOf(connection.method_60926());
    }

    /** a 1.20.6 subclass's super.getDisconnectedReason(): Connection's own answer, the details it keeps */
    public static net.minecraft.class_2561 cbSuperDisconnectedReason(net.minecraft.class_2535 connection) {
        return cbReasonOf(((top.dext.centurybridge.step.v1_21.mixin.CbNetConnectionAccess) connection).cb$disconnectionDetails());
    }

    /**
     * PacketListener.fillListenerSpecificCrashDetails(category) of 1.20.6 takes the whole report too since
     * 1.21, which vanilla's 1.21 bodies use for sections of their own (ClientCommonPacketListenerImpl's
     * "Custom Server Details"). A 1.20.6 listener entering its own method directly has no report to give:
     * 1.20.6 wrote no such section anywhere, so its super call gets a report of its own that nothing
     * reads -- the category details still land in the caller's category.
     */
    public static net.minecraft.class_128 cbDetachedCrashReport() {
        return new net.minecraft.class_128("Packet listener details (1.20.6 call)",
            new IllegalStateException("1.20.6 listener filled its crash details outside a report"));
    }

    /**
     * ClientCommonPacketListener.handleCustomReportDetails / handleServerLinks are abstract since 1.21:
     * the server sends both packets during configuration and play. 1.20.6 had neither packet, so a
     * 1.20.6 listener never saw one and has nothing of its own the details or links belong to (vanilla's
     * own 1.21 listeners keep them for the crash report and the pause screen). A 1.20.6 implementation is
     * handed them and keeps nothing, as it kept nothing in 1.20.6.
     */
    public static void cbLegacyCustomReportDetails(net.minecraft.class_8705 listener, net.minecraft.class_9814 packet) {
    }

    /** see {@link #cbLegacyCustomReportDetails} */
    public static void cbLegacyServerLinks(net.minecraft.class_8705 listener, net.minecraft.class_9815 packet) {
    }

    // ------------------------------------------------------------------ ClientboundAddEntityPacket (class_2604)

    /*
     * 1.20.6's ClientboundAddEntityPacket(entity[, data]) read id, uuid, x, y, z, xRot, yRot, type, data,
     * motion and head yaw off the entity itself; 1.21's (entity, serverEntity[, data]) takes position and
     * rotations from the server entity's last sent (packed) state. The full constructor both releases
     * share is fed exactly what 1.20.6 read.
     */

    public static int cbId(net.minecraft.class_1297 e) {
        return e.method_5628();
    }

    public static java.util.UUID cbUuid(net.minecraft.class_1297 e) {
        return e.method_5667();
    }

    public static double cbX(net.minecraft.class_1297 e) {
        return e.method_23317();
    }

    public static double cbY(net.minecraft.class_1297 e) {
        return e.method_23318();
    }

    public static double cbZ(net.minecraft.class_1297 e) {
        return e.method_23321();
    }

    public static float cbXRot(net.minecraft.class_1297 e) {
        return e.method_36455();
    }

    public static float cbYRot(net.minecraft.class_1297 e) {
        return e.method_36454();
    }

    public static net.minecraft.class_1299<?> cbType(net.minecraft.class_1297 e) {
        return e.method_5864();
    }

    public static net.minecraft.class_243 cbMotion(net.minecraft.class_1297 e) {
        return e.method_18798();
    }

    /** 1.20.6 widened the head yaw to the constructor's double the same way */
    public static double cbYHeadRot(net.minecraft.class_1297 e) {
        return e.method_5791();
    }

    /** ClientboundAddEntityPacket(entity) of 1.20.6 was (entity, 0) */
    public static int cbNoData() {
        return 0;
    }

    // ------------------------------------------------------------------ ClientboundAddExperienceOrbPacket (class_2606)

    /**
     * ClientboundAddExperienceOrbPacket(orb) of 1.20.6 is (orb, serverEntity) since 1.21, which takes the
     * position from the server entity's position base (the orb's tracking position when it was made).
     * The server entity is made from the orb itself; the packet's position is then set to the orb's own
     * coordinates, which is what 1.20.6 read ({@link #cbOrbPacketBuilt}), so an orb class overriding its
     * tracking position does not change the packet. The level is not used to make a server entity.
     */
    public static net.minecraft.class_3231 cbOrbServerEntity(net.minecraft.class_1303 orb) {
        return new net.minecraft.class_3231(null, orb, 0, false, packet -> {
        });
    }

    public static void cbOrbPacketBuilt(net.minecraft.class_2606 packet, net.minecraft.class_1303 orb) {
        top.dext.centurybridge.step.v1_21.mixin.CbNetAddExperienceOrbPacketAccess a =
            (top.dext.centurybridge.step.v1_21.mixin.CbNetAddExperienceOrbPacketAccess) packet;
        a.cb$setX(orb.method_23317());
        a.cb$setY(orb.method_23318());
        a.cb$setZ(orb.method_23321());
    }

    // ------------------------------------------------------------------ ClientboundSetEntityMotionPacket (class_2743)

    /**
     * ClientboundSetEntityMotionPacket.getXa/getYa/getZa answered the stored motion in 1/8000 block per
     * tick (an int) in 1.20.6; 1.21 keeps the same int and answers it divided by 8000. Undone exactly: the
     * quotient is within half a unit of the stored int.
     */
    public static int cbMotionUnits(double motion) {
        return (int) Math.round(motion * 8000.0);
    }

    /** a 1.20.6 override's int, as the double 1.21's callers read: what 1.21 does with its own int */
    public static double cbMotionValue(int units) {
        return units / 8000.0;
    }

    // ------------------------------------------------------------------ ClientboundProjectilePowerPacket (class_9632)

    /*
     * ClientboundProjectilePowerPacket(id, xPower, yPower, zPower) of 1.20.6 carried a hurting projectile's
     * constant acceleration vector; since 1.21 it carries accelerationPower, the magnitude 1.21 applies
     * along the projectile's motion. A 1.20.6-built packet carries the vector's length (what the
     * projectile bridge keeps as accelerationPower for a 1.20.6 vector) and keeps the vector itself, so
     * its 1.20.6 getters answer it exactly. A 1.21-built packet's 1.20.6 getters answer the vector 1.21
     * actually applies: the projectile's motion direction times the power -- the projectile's own 1.20.6
     * getters agree for a projectile in 1.21's mode.
     */
    private static final Map<net.minecraft.class_9632, double[]> POWER = new MapMaker().weakKeys().makeMap();

    public static double cbPowerMagnitude(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static void cbPowerPacketBuilt(net.minecraft.class_9632 packet, int id, double x, double y, double z) {
        POWER.put(packet, new double[] {x, y, z});
    }

    public static double cbXPower(net.minecraft.class_9632 packet) {
        return power(packet)[0];
    }

    public static double cbYPower(net.minecraft.class_9632 packet) {
        return power(packet)[1];
    }

    public static double cbZPower(net.minecraft.class_9632 packet) {
        return power(packet)[2];
    }

    private static double[] power(net.minecraft.class_9632 packet) {
        double[] kept = POWER.get(packet);
        if (kept != null) {
            return kept;
        }
        net.minecraft.class_1297 e = entity(packet.method_59507());
        if (e == null) {
            // the projectile is in no level this side knows: 1.21's packet holds the magnitude alone and
            // 1.21 applies it along the motion of an entity that is not there
            return new double[3];
        }
        net.minecraft.class_243 v = e.method_18798().method_1029().method_1021(packet.method_60423());
        return new double[] {v.field_1352, v.field_1351, v.field_1350};
    }

    /** the entity of that id: the client level's on a client, else the server's levels' */
    private static net.minecraft.class_1297 entity(int id) {
        if (clientSide()) {
            net.minecraft.class_1297 e = CbNetClient.cbClientEntity(id);
            if (e != null) {
                return e;
            }
        }
        Object game = FabricLoader.getInstance().getGameInstance();
        if (game instanceof net.minecraft.server.MinecraftServer server) {
            for (net.minecraft.class_3218 level : server.method_3738()) {
                net.minecraft.class_1297 e = level.method_8469(id);
                if (e != null) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * 1.21's client applies getAccelerationPower(); 1.20.6's applied getXPower/getYPower/getZPower, so a
     * 1.20.6 packet subclass overriding them changed what the projectile got. Its 1.21 power is the
     * length of what its getters answer (each the subclass's own when it overrides it).
     */
    public interface CbLegacyPowerPacket {
        default double method_59508() {
            return cbXPower((net.minecraft.class_9632) this);
        }

        default double method_59509() {
            return cbYPower((net.minecraft.class_9632) this);
        }

        default double method_59510() {
            return cbZPower((net.minecraft.class_9632) this);
        }
    }

    public static double cbAccelerationPower(CbLegacyPowerPacket packet) {
        return cbPowerMagnitude(packet.method_59508(), packet.method_59509(), packet.method_59510());
    }

    // ------------------------------------------------------------------ ServerboundUseItemPacket (class_2886)

    /**
     * ServerboundUseItemPacket(hand, sequence) of 1.20.6 carries the player's rotation too since 1.21, and
     * the server turns the player to it before using the item; 1.20.6's server used the rotation it
     * already had. A 1.20.6-built packet is marked: the server leaves the player's rotation alone for it
     * (mixin/CbNetServerUseItemMixin), as 1.20.6 did. Over the network the mark cannot travel, so a
     * packet built on the client carries the local player's own rotation -- the rotation the client keeps
     * the server up to date with every tick, and what 1.21's own client sends.
     */
    private static final Set<net.minecraft.class_2886> LEGACY_USE_ITEM =
        Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    public static float cbUseYRot() {
        return clientSide() ? CbNetClient.cbLocalYRot() : 0.0F;
    }

    public static float cbUseXRot() {
        return clientSide() ? CbNetClient.cbLocalXRot() : 0.0F;
    }

    public static void cbUseItemBuilt(net.minecraft.class_2886 packet) {
        LEGACY_USE_ITEM.add(packet);
    }

    /** whether the server should keep the player's rotation for this packet (a 1.20.6-built one) */
    public static boolean cbKeepsRotation(net.minecraft.class_2886 packet) {
        return LEGACY_USE_ITEM.contains(packet);
    }

    private static boolean clientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    // ------------------------------------------------------------------ ProtocolInfoBuilder (class_9147), ProtocolInfo.Unbound

    /**
     * ProtocolInfoBuilder.serverboundProtocol / clientboundProtocol of 1.20.6 answered a bound ProtocolInfo
     * over plain FriendlyByteBufs (build(FriendlyByteBuf::new)); since 1.21 they answer the unbound info,
     * which 1.21 binds where it is used. Bound the way 1.20.6 bound it.
     */
    public static net.minecraft.class_9127<?> cbBindPlain(net.minecraft.class_9127.class_9128<?, ?> unbound) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        net.minecraft.class_9127<?> bound = ((net.minecraft.class_9127.class_9128) unbound)
            .method_61107((Function<io.netty.buffer.ByteBuf, net.minecraft.class_2540>) net.minecraft.class_2540::new);
        return bound;
    }

    /** ProtocolInfoBuilder.serverboundProtocolUnbound of 1.20.6 is 1.21's serverboundProtocol */
    public static <T extends net.minecraft.class_8701, B extends io.netty.buffer.ByteBuf> net.minecraft.class_9127.class_9128<T, B>
            method_56457(net.minecraft.class_2539 protocol, java.util.function.Consumer<net.minecraft.class_9147<T, B>> setup) {
        return net.minecraft.class_9147.method_56451(protocol, setup);
    }

    /** ProtocolInfoBuilder.clientboundProtocolUnbound of 1.20.6 is 1.21's clientboundProtocol */
    public static <T extends net.minecraft.class_8697, B extends io.netty.buffer.ByteBuf> net.minecraft.class_9127.class_9128<T, B>
            method_56458(net.minecraft.class_2539 protocol, java.util.function.Consumer<net.minecraft.class_9147<T, B>> setup) {
        return net.minecraft.class_9147.method_56455(protocol, setup);
    }

    /**
     * ProtocolInfo.Unbound was 1.20.6's functional interface for a protocol waiting for its buffer
     * decorator: bind(decorator) and nothing else. 1.21's Unbound also tells its protocol id and flow and
     * lists its packets (only the packet report data generator asks, for vanilla's protocols). A 1.20.6
     * Unbound (a class or lambda) answers the id and flow of what it binds to; its packets it cannot list.
     */
    @FunctionalInterface
    public interface CbLegacyUnbound {
        net.minecraft.class_9127<?> bind(Function<io.netty.buffer.ByteBuf, ?> decorator);
    }

    /** a 1.20.6 Unbound lambda as 1.21's Unbound */
    public static net.minecraft.class_9127.class_9128<?, ?> cbUnbound(CbLegacyUnbound legacy) {
        return new LegacyUnbound(legacy);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class LegacyUnbound implements net.minecraft.class_9127.class_9128 {
        private final CbLegacyUnbound legacy;

        LegacyUnbound(CbLegacyUnbound legacy) {
            this.legacy = legacy;
        }

        @Override
        public net.minecraft.class_9127 method_61107(Function decorator) {
            return legacy.bind(decorator);
        }

        @Override
        public net.minecraft.class_2539 method_61106() {
            return cbUnboundId(this);
        }

        @Override
        public net.minecraft.class_2598 method_61109() {
            return cbUnboundFlow(this);
        }

        @Override
        public void method_61108(net.minecraft.class_9127.class_9128.class_9828 visitor) {
            cbUnboundPackets(this, visitor);
        }
    }

    /** a 1.20.6 Unbound's protocol: the one the info it binds to is for (the decorator is not run by binding) */
    public static net.minecraft.class_2539 cbUnboundId(net.minecraft.class_9127.class_9128<?, ?> unbound) {
        return cbBindPlain(unbound).comp_2234();
    }

    /** a 1.20.6 Unbound's flow: the one of the info it binds to */
    public static net.minecraft.class_2598 cbUnboundFlow(net.minecraft.class_9127.class_9128<?, ?> unbound) {
        return cbBindPlain(unbound).comp_2235();
    }

    /**
     * Listing the packets of a 1.20.6 Unbound: 1.20.6 kept the packet types inside the codec its builder
     * made, and an Unbound answered nothing but bind; neither it nor what it binds to can enumerate them.
     * 1.21 asks only its own protocols (the packet report), never a mod's.
     */
    public static void cbUnboundPackets(net.minecraft.class_9127.class_9128<?, ?> unbound,
                                        net.minecraft.class_9127.class_9128.class_9828 visitor) {
        throw new UnsupportedOperationException("a 1.20.6 ProtocolInfo.Unbound (" + unbound.getClass().getName()
            + ") has no packet list: 1.20.6's Unbound only binds");
    }
}
