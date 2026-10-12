package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * The server running in this JVM -- a dedicated server, or the integrated one while a world is open --
 * recorded as it starts and dropped as it stops (CbServersMixin on MinecraftServer.runServer /
 * stopServer). The layer's one hook on a server's life: 1.20.6 shapes that named no level where 1.21's
 * need one read {@link #current} / {@link #levelFor}, and what keeps per-server state subscribes to
 * {@link #onStopped}.
 */
public final class CbServers {

    private CbServers() {
    }

    private static volatile net.minecraft.server.MinecraftServer server;

    private static final List<Consumer<net.minecraft.server.MinecraftServer>> STOPPED = new CopyOnWriteArrayList<>();

    /** run {@code listener} with each server as it finishes stopping (MinecraftServer.stopServer's end), in subscription order */
    public static void onStopped(Consumer<net.minecraft.server.MinecraftServer> listener) {
        STOPPED.add(listener);
    }

    /** MinecraftServer.runServer began (CbServersMixin) */
    public static void started(net.minecraft.server.MinecraftServer s) {
        server = s;
    }

    /** MinecraftServer.stopServer finished (CbServersMixin): the subscribers run, then the server is no longer current */
    public static void stopped(net.minecraft.server.MinecraftServer s) {
        for (Consumer<net.minecraft.server.MinecraftServer> listener : STOPPED) {
            listener.accept(s);
        }
        if (server == s) {
            server = null;
        }
    }

    /** the running server, or null (a client with no world open, or before the server starts) */
    public static net.minecraft.server.MinecraftServer current() {
        return server;
    }

    /**
     * The level a 1.20.6 call that passed only a random stands for: the server level whose own random it
     * is (level.random, as 1.20.6 callers passed it), else the server's overworld; null with no server.
     */
    public static net.minecraft.class_3218 levelFor(net.minecraft.class_5819 random) {
        net.minecraft.server.MinecraftServer s = server;
        if (s == null) {
            return null;
        }
        if (random != null) {
            for (net.minecraft.class_3218 level : s.method_3738()) {
                if (level.field_9229 == random) {
                    return level;
                }
            }
        }
        return s.method_30002();
    }
}
