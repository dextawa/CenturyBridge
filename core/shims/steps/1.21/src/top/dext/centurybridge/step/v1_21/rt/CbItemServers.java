package top.dext.centurybridge.step.v1_21.rt;

/**
 * The server running in this JVM (a dedicated server, or the integrated one while a world is open), for the
 * 1.20.6 shapes that named no level where 1.21's need one: recorded as the server starts and dropped as it
 * stops (CbItemServerMixin).
 */
public final class CbItemServers {

    private CbItemServers() {
    }

    private static volatile net.minecraft.server.MinecraftServer server;

    /** MinecraftServer.runServer began (CbItemServerMixin) */
    public static void started(net.minecraft.server.MinecraftServer s) {
        server = s;
    }

    /** MinecraftServer.stopServer finished (CbItemServerMixin) */
    public static void stopped(net.minecraft.server.MinecraftServer s) {
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
