package top.dext.centurybridge.step.v1_20_5.rt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_2539;
import net.minecraft.class_2540;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_2960;
import net.minecraft.class_5455;
import net.minecraft.class_8697;
import net.minecraft.class_8701;
import net.minecraft.class_8703;
import net.minecraft.class_9095;
import net.minecraft.class_9097;
import net.minecraft.class_9099;
import net.minecraft.class_9103;
import net.minecraft.class_9127;
import net.minecraft.class_9129;
import net.minecraft.class_9136;
import net.minecraft.class_9139;
import net.minecraft.class_9145;
import net.minecraft.class_9157;
import top.dext.centurybridge.step.v1_20_5.mixin.CbNetDispatchAccess;

/**
 * Packet.write(FriendlyByteBuf) and Packet.type() between 1.20.4 and 1.20.5 (area s4net).
 *
 * 1.20.4's Packet interface declared write(buf); 1.20.5's declares type() instead and writes a
 * packet only through the codec of the protocol it belongs to. Every vanilla packet whose 1.20.4
 * class wrote itself has write back (the generated CbNetWrite* mixins), and a 1.20.4 mod's own packet
 * classes keep theirs (the converter adds CbNetLegacyPacket); both implement {@link CbNetLegacyPacket}.
 */
public final class CbNetPacketWrites {

    /**
     * was Packet.write(buf), called through the interface: the packet's own write where it has one;
     * any other packet -- one 1.20.5 added, or a 1.20.5 mod's -- writes its fields the way the protocol
     * that knows its type writes them (the protocol codec's own entry, without the leading packet id).
     */
    public static void method_11052(class_2596<?> packet, class_2540 buf) {
        if (packet instanceof CbNetLegacyPacket legacy) {
            legacy.method_11052(buf);
            return;
        }
        class_9145<?> type = packet.method_55846();
        for (class_9127<?> protocol : cbProtocols(type.comp_2230())) {
            if (!(protocol.comp_2236() instanceof class_9136<?, ?, ?> dispatch)) {
                continue;
            }
            int id = ((CbNetDispatchAccess) dispatch).cb$toId().getOrDefault(type, -1);
            if (id < 0) {
                continue;
            }
            ByteBuf scratch = Unpooled.buffer();
            try {
                @SuppressWarnings("unchecked")
                class_9139<ByteBuf, Object> codec = (class_9139<ByteBuf, Object>) (Object) protocol.comp_2236();
                codec.encode(scratch, packet);
                scratch.skipBytes(class_8703.method_53015(id)); // the packet id the protocol writes first
                buf.writeBytes(scratch);
            } finally {
                scratch.release();
            }
            return;
        }
        throw new IllegalArgumentException("CenturyBridge: no 1.20.5 protocol writes packets of type " + type);
    }

    /** the protocols of one direction, the game's bound to the running world's registries */
    private static List<class_9127<?>> cbProtocols(class_2598 flow) {
        List<class_9127<?>> out = new ArrayList<>(5);
        class_5455 registries = CbRegistries.currentAccess();
        if (flow == class_2598.field_11942) {
            out.add(cbGame(registries, false));
            out.add(class_9157.field_48699);
            out.add(class_9099.field_48248);
            out.add(class_9103.field_48264);
        } else {
            out.add(cbGame(registries, true));
            out.add(class_9157.field_48698);
            out.add(class_9099.field_48247);
            out.add(class_9103.field_48263);
            out.add(class_9097.field_48231);
        }
        return out;
    }

    /** the game protocols bound to a registry access, made once per access */
    private static volatile Object[] game = {null, null, null};

    private static class_9127<?> cbGame(class_5455 registries, boolean serverbound) {
        Object[] g = game;
        if (g[0] != registries) {
            g = new Object[] {registries, class_9095.field_48172.bind(class_9129.method_56350(registries)),
                class_9095.field_48173.bind(class_9129.method_56350(registries))};
            game = g;
        }
        return (class_9127<?>) (serverbound ? g[1] : g[2]);
    }

    // ---------------------------------------------------------------- type() of a 1.20.4 mod's packet

    private static final ClassValue<class_9145<?>> LEGACY_TYPES = new ClassValue<>() {
        @Override
        protected class_9145<?> computeValue(Class<?> type) {
            String path = "legacy/" + type.getName().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");
            return new class_9145<>(cbFlow(type), new class_2960("centurybridge", path));
        }
    };

    /**
     * Packet.type() of a 1.20.4 mod's packet class (abstractFills: a class with no type() of its own
     * or from a concrete vanilla parent). In 1.20.4 such a class was sent only after the mod put it
     * into a protocol's packet set (ConnectionProtocol, through mixins or reflection), and a packet
     * of an unregistered class could not be sent: "Can't serialize unregistered packet". 1.20.5
     * protocols are built once, from vanilla's types; this type names the class (one per class,
     * centurybridge:legacy/...) and its direction, and sending it fails the same way -- 1.20.5's
     * "Sending unknown packet" -- instead of the AbstractMethodError of a missing type().
     */
    public static class_9145<?> cbLegacyPacketType(class_2596<?> packet) {
        return LEGACY_TYPES.get(packet.getClass());
    }

    /** the packet's direction, from the listener it is for (its Packet<..> type argument): clientbound unless it names a serverbound one */
    static class_2598 cbFlow(Class<?> type) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Type t : c.getGenericInterfaces()) {
                if (t instanceof ParameterizedType p && p.getRawType() == class_2596.class
                        && p.getActualTypeArguments()[0] instanceof Class<?> listener) {
                    if (class_8701.class.isAssignableFrom(listener)) {
                        return class_2598.field_11941;
                    }
                    if (class_8697.class.isAssignableFrom(listener)) {
                        return class_2598.field_11942;
                    }
                }
            }
        }
        return class_2598.field_11942;
    }

    // ---------------------------------------------------------------- nextProtocol()

    /**
     * was Packet.nextProtocol(): the protocol a connection switched to once this packet had passed --
     * 1.20.4's pipeline asked every packet, and the packets ending a phase answered (the handshake its
     * intention's, login's last ones CONFIGURATION, configuration's last ones PLAY, the play packets
     * that start a configuration CONFIGURATION), everything else null. A 1.20.4 mod's own packet that
     * answers it answers here too. 1.20.5 marks those packets terminal and switches in listener code.
     */
    public static class_2539 method_52273(class_2596<?> packet) {
        if (packet instanceof CbNetLegacyNextProtocol legacy) {
            return legacy.method_52273();
        }
        return cbSuperNextProtocol(packet);
    }

    /** super.nextProtocol() of a 1.20.4 subclass: what the vanilla class (or Packet's default) answered in 1.20.4 */
    public static class_2539 cbSuperNextProtocol(class_2596<?> packet) {
        if (packet instanceof net.minecraft.class_2889 intention) {
            return method_52285(intention.comp_1566());
        }
        if (packet instanceof net.minecraft.class_2901 || packet instanceof net.minecraft.class_8593
                || packet instanceof net.minecraft.class_8588 || packet instanceof net.minecraft.class_8591) {
            return class_2539.field_45671;
        }
        if (packet instanceof net.minecraft.class_8733 || packet instanceof net.minecraft.class_8736) {
            return class_2539.field_20591;
        }
        return null;
    }

    // the super redirect names the class a subclass's super call resolved to in 1.20.4 as the receiver
    // type: one overload per vanilla class that declared nextProtocol, each answering as that class did

    public static class_2539 cbSuperNextProtocol(net.minecraft.class_2889 packet) {
        return cbSuperNextProtocol((class_2596<?>) packet);
    }

    public static class_2539 cbSuperNextProtocol(net.minecraft.class_2901 packet) {
        return cbSuperNextProtocol((class_2596<?>) packet);
    }

    public static class_2539 cbSuperNextProtocol(net.minecraft.class_8593 packet) {
        return cbSuperNextProtocol((class_2596<?>) packet);
    }

    public static class_2539 cbSuperNextProtocol(net.minecraft.class_8733 packet) {
        return cbSuperNextProtocol((class_2596<?>) packet);
    }

    public static class_2539 cbSuperNextProtocol(net.minecraft.class_8736 packet) {
        return cbSuperNextProtocol((class_2596<?>) packet);
    }

    public static class_2539 cbSuperNextProtocol(net.minecraft.class_8588 packet) {
        return cbSuperNextProtocol((class_2596<?>) packet);
    }

    public static class_2539 cbSuperNextProtocol(net.minecraft.class_8591 packet) {
        return cbSuperNextProtocol((class_2596<?>) packet);
    }

    /**
     * was ClientIntent.protocol(): STATUS for a status ping, LOGIN for a login -- and LOGIN for the
     * transfer 1.20.5 added, which logs in as well.
     */
    public static class_2539 method_52285(net.minecraft.class_8592 intent) {
        return intent == net.minecraft.class_8592.field_44974 ? class_2539.field_20592 : class_2539.field_20593;
    }

    private CbNetPacketWrites() {
    }
}
