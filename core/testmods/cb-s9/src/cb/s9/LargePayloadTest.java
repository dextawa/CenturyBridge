package cb.s9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.class_2535;
import net.minecraft.class_2539;
import net.minecraft.class_2540;
import net.minecraft.class_2598;
import net.minecraft.class_2658;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import net.minecraft.class_8732;
import net.minecraft.class_9139;
import net.minecraft.class_9157;

/**
 * A 1.21.8 mod's large payload (Fabric API 0.136 PayloadTypeRegistry.registerLarge) sent through two real vanilla
 * connections wired back to back on embedded channels: the sender's configuration-phase S2C pipeline (with whatever
 * the layer adds when the protocol is set up) and the receiver's, whose packet listener records what arrives.
 */
final class LargePayloadTest {

    private LargePayloadTest() {
    }

    record Big(byte[] data) implements class_8710 {
        static final class_8710.class_9154<Big> ID = new class_8710.class_9154<>(class_2960.method_60655("cbs9", "big"));
        static final class_9139<class_2540, Big> CODEC = class_9139.method_56437((buf, big) -> {
            buf.writeInt(big.data.length);
            buf.writeBytes(big.data);
        }, buf -> {
            byte[] data = new byte[buf.readInt()];
            buf.readBytes(data);
            return new Big(data);
        });

        @Override
        public class_8710.class_9154<? extends class_8710> method_56479() {
            return ID;
        }
    }

    static final int MAX = 4 * 1024 * 1024;

    /** other packets the receiving listener was handed (by class name) */
    static final List<String> OTHER = new ArrayList<>();

    static void register() {
        PayloadTypeRegistry.configurationS2C().registerLarge(Big.ID, Big.CODEC, MAX);
    }

    /** the listener of the receiving connection: answers by return type (names differ between releases), records payload packets */
    static class_8732 listener(List<Object> received) {
        return (class_8732) Proxy.newProxyInstance(LargePayloadTest.class.getClassLoader(), new Class<?>[] {class_8732.class}, (proxy, method, args) -> {
            Class<?> rt = method.getReturnType();
            if (rt == class_2539.class) {
                return class_2539.field_45671;
            }
            if (rt == class_2598.class) {
                return class_2598.field_11942;
            }
            if (rt == boolean.class) {
                return true;
            }
            if (rt == void.class && args != null && args.length == 1 && args[0] instanceof class_2658 packet) {
                received.add(packet.comp_1646());
                return null;
            }
            if (rt == void.class && args != null && args.length == 1 && args[0] instanceof net.minecraft.class_2596<?> other) {
                OTHER.add(other.getClass().getName());
                return null;
            }
            if (method.getName().equals("hashCode")) {
                return System.identityHashCode(proxy);
            }
            if (method.getName().equals("equals")) {
                return proxy == args[0];
            }
            if (method.getName().equals("toString")) {
                return "cb-s9 listener";
            }
            return null;
        });
    }

    /** sends one payload through, answers the frames on the wire and what the receiver got */
    static Object[] roundTrip(class_8710 payload) throws Exception {
        EmbeddedChannel out = new EmbeddedChannel(false, false);
        class_2535.method_48311(out.pipeline(), class_2598.field_11941, false, null);
        class_2535 sender = new class_2535(class_2598.field_11941);
        sender.method_53859(out.pipeline());
        out.register();
        sender.method_56329(class_9157.field_48699);

        EmbeddedChannel in = new EmbeddedChannel(false, false);
        class_2535.method_48311(in.pipeline(), class_2598.field_11942, false, null);
        class_2535 receiver = new class_2535(class_2598.field_11942);
        receiver.method_53859(in.pipeline());
        in.register();
        List<Object> received = new ArrayList<>();
        receiver.method_56330(class_9157.field_48699, listener(received));

        sender.method_10743(new class_2658(payload));
        out.runPendingTasks();
        int frames = 0;
        Object frame;
        while ((frame = out.readOutbound()) != null) {
            if (frame instanceof ByteBuf buf && buf.isReadable()) {
                frames++;
                in.writeInbound(buf);
            }
        }
        in.runPendingTasks();
        boolean senderOpen = out.isOpen();
        out.finishAndReleaseAll();
        in.finishAndReleaseAll();
        return new Object[] {frames, received, senderOpen};
    }

    static void run() {
        S9TestMod.run("large payload (registerLarge)", () -> {
            boolean threw = false;
            try {
                PayloadTypeRegistry.configurationS2C().registerLarge(new class_8710.class_9154<Big>(class_2960.method_60655("cbs9", "negative")), Big.CODEC, -1);
            } catch (IllegalArgumentException e) {
                threw = true;
            }
            S9TestMod.check(threw, "registerLarge with a negative max size throws IllegalArgumentException");
            try {
                byte[] data = new byte[3 * 1024 * 1024];
                new Random(59).nextBytes(data);
                Object[] r = roundTrip(new Big(data));
                @SuppressWarnings("unchecked")
                List<Object> got = (List<Object>) r[1];
                S9TestMod.check((int) r[0] > 1, "a 3 MiB large payload leaves in more than one frame (split), got " + r[0]);
                S9TestMod.check(got.size() == 1 && got.get(0) instanceof Big b && Arrays.equals(b.data, data),
                    "the receiver gets one payload packet with the 3 MiB merged back byte for byte, got " + got.size());

                byte[] small = new byte[100];
                new Random(60).nextBytes(small);
                r = roundTrip(new Big(small));
                @SuppressWarnings("unchecked")
                List<Object> got2 = (List<Object>) r[1];
                S9TestMod.check((int) r[0] == 1, "a 100-byte payload of the large type leaves in one frame, got " + r[0]);
                S9TestMod.check(got2.size() == 1 && got2.get(0) instanceof Big b2 && Arrays.equals(b2.data, small),
                    "the receiver gets the small payload unchanged");

                OTHER.clear();
                r = roundTrip(new Big(new byte[MAX + 1024]));
                @SuppressWarnings("unchecked")
                List<Object> got3 = (List<Object>) r[1];
                S9TestMod.check(got3.isEmpty() && (int) r[0] <= 1, "a payload above the registered max is refused by the sender (at most vanilla's"
                    + " disconnect leaves, no payload arrives), got " + r[0] + " frames, " + got3.size() + " received, other packets " + OTHER);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
