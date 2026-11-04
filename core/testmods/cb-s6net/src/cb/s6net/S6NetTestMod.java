package cb.s6net;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1267;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2632;
import net.minecraft.class_2909;
import net.minecraft.class_2924;
import net.minecraft.class_2926;
import net.minecraft.class_4210;
import net.minecraft.class_5455;
import net.minecraft.class_9129;
import net.minecraft.class_9135;
import net.minecraft.class_9139;
import net.minecraft.server.MinecraftServer;

/** Real 1.21.6 behavior checks for the 1.21.5 packet codec fields and registry length-delimited operation. */
public final class S6NetTestMod implements ModInitializer {

    private static final List<String> FAILS = new ArrayList<>();
    private static int checks;

    private static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-S6NET " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    @Override
    public void onInitialize() {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3806() && server.method_30002() != null) {
                    server.execute(S6NetTestMod::start);
                    return;
                }
            }
        }, "cb-s6net");
        thread.setDaemon(true);
        thread.start();
    }

    private static void start() {
        MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        try {
            packetCodecs();
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "packet codecs threw " + t);
        }
        try {
            registryOperation(server.method_30002().method_30349());
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "registry length-delimited operation threw " + t);
        }
        System.out.println("CB-S6NET DONE " + (checks - FAILS.size()) + "/" + checks
            + (FAILS.isEmpty() ? "" : " FAILS " + FAILS));
    }

    /** A default StreamCodec.map composition, typed entirely with 1.21.5's FriendlyByteBuf receiver. */
    private static <T> class_9139<class_2540, T> mapped(class_9139<class_2540, T> codec) {
        return codec.method_56432(value -> value, value -> value);
    }

    private static <T> T roundTrip(class_9139<class_2540, T> codec, T value) {
        class_2540 buf = new class_2540(Unpooled.buffer());
        try {
            codec.encode(buf, value);
            return codec.decode(buf);
        } finally {
            buf.release();
        }
    }

    private static void packetCodecs() {
        class_9139<class_2540, class_2632> clientDifficulty = class_2632.field_47907;
        check(clientDifficulty == class_2632.field_47907, "ClientboundChangeDifficultyPacket codec keeps constant identity");
        class_2632 clientValue = new class_2632(class_1267.method_5462(3), true);
        class_2632 client = roundTrip(mapped(clientDifficulty), clientValue);
        check(clientValue.equals(client), "ClientboundChangeDifficultyPacket mapped codec round-trip");

        class_9139<class_2540, class_2909> loginDisconnect = class_2909.field_48237;
        check(loginDisconnect == class_2909.field_48237, "ClientboundLoginDisconnectPacket codec keeps constant identity");
        class_2909 loginValue = new class_2909(class_2561.method_43470("cb-s6net"));
        class_2909 login = roundTrip(mapped(loginDisconnect), loginValue);
        check(loginValue.equals(login), "ClientboundLoginDisconnectPacket mapped codec round-trip");

        class_9139<class_2540, class_2924> statusResponse = class_2924.field_48258;
        check(statusResponse == class_2924.field_48258, "ClientboundStatusResponsePacket codec keeps constant identity");
        class_2926 status = new class_2926(class_2561.method_43470("cb-status"), Optional.empty(), Optional.empty(),
            Optional.empty(), true);
        class_2924 statusValue = new class_2924(status);
        class_2924 statusDecoded = roundTrip(mapped(statusResponse), statusValue);
        check(statusValue.equals(statusDecoded), "ClientboundStatusResponsePacket mapped codec round-trip");

        class_9139<class_2540, class_4210> serverDifficulty = class_4210.field_48176;
        check(serverDifficulty == class_4210.field_48176, "ServerboundChangeDifficultyPacket codec keeps constant identity");
        class_4210 serverValue = new class_4210(class_1267.method_5462(2));
        class_4210 server = roundTrip(mapped(serverDifficulty), serverValue);
        check(serverValue.equals(server), "ServerboundChangeDifficultyPacket mapped codec round-trip");
    }

    private static void registryOperation(class_5455 access) {
        int[] calls = new int[2];
        class_9139<class_9129, Integer> base = new class_9139<>() {
            @Override
            public Integer decode(class_9129 buf) {
                if (buf.method_56349() != access) {
                    throw new IllegalStateException("decode registries changed");
                }
                calls[0]++;
                return buf.readInt();
            }

            @Override
            public void encode(class_9129 buf, Integer value) {
                if (buf.method_56349() != access) {
                    throw new IllegalStateException("encode registries changed");
                }
                calls[1]++;
                buf.writeInt(value);
            }
        };
        class_9139.class_9140<class_9129, Integer, Integer> operation = class_9135.method_68864(16);
        class_9139<class_9129, Integer> codec = base.method_56433(operation);
        class_9129 buf = new class_9129(Unpooled.buffer(), access);
        try {
            codec.encode(buf, 0x12345678);
            int value = codec.decode(buf);
            check(value == 0x12345678, "registry length-delimited codec round-trip");
            check(calls[0] == 1 && calls[1] == 1, "registry codec receives the same RegistryAccess on encode and decode");
            check(!buf.isReadable(), "length-delimited decode consumes exactly its framed value");
        } finally {
            buf.release();
        }
    }
}
