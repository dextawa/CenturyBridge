package top.dext.centurybridge.step.v1_21.rt;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4341;
import net.minecraft.class_4346;
import net.minecraft.class_4355;
import net.minecraft.class_437;
import net.minecraft.class_4388;
import net.minecraft.class_4877;
import top.dext.centurybridge.step.v1_21.facade.class_4880;
import top.dext.centurybridge.step.v1_21.facade.class_4882;
import top.dext.centurybridge.step.v1_21.mixin.CbRealmsClientInvoker;

/**
 * 1.20.6's Realms API that 1.21 reshaped: the live player lists (1.21 resolves their players to profiles while
 * parsing), RealmsServer's ping, and RealmsConfigureWorldScreen's open / close taking the screen to return to.
 * Wired from s6fin-realms.json.
 */
@Environment(EnvType.CLIENT)
public final class CbRealms {

    private CbRealms() {
    }

    // ------------------------------------------------------------------------------------------------ RealmsServer.serverPing

    /** RealmsServer's 1.20.6 serverPing field, given back by CbRealmsServerMixin */
    public interface PingHolder {
        class_4880 cb$serverPing();

        void cb$setServerPing(class_4880 ping);
    }

    public static class_4880 serverPing(class_4877 server) {
        return ((PingHolder) server).cb$serverPing();
    }

    public static void setServerPing(class_4877 server, class_4880 ping) {
        ((PingHolder) server).cb$setServerPing(ping);
    }

    // ------------------------------------------------------------------------------------------------ RealmsClient.getLiveStats

    /**
     * 1.20.6 client.getLiveStats(): a 1.20.6 subclass's own getLiveStats() when the client is one (the call
     * dispatches to it, as in 1.20.6), else GET activities/liveplayerlist, parsed into 1.20.6's lists (server id
     * and player UUIDs; 1.20.6 resolved no profiles here). 1.21 asks the same endpoint and resolves every player's
     * profile. The subclass's method returns 1.20.6's lists, a shape 1.21's class lacks, so it is found by its
     * exact descriptor.
     */
    public static class_4882 method_21018(class_4341 client) throws class_4355 {
        if (client.getClass() != class_4341.class) {
            java.lang.invoke.MethodHandle own = legacyLiveStats(client.getClass());
            if (own != null) {
                try {
                    return (class_4882) own.invoke(client);
                } catch (class_4355 | RuntimeException | Error e) {
                    throw e;
                } catch (Throwable t) {
                    throw new java.lang.reflect.UndeclaredThrowableException(t);
                }
            }
        }
        return superGetLiveStats(client);
    }

    private static final ClassValue<java.util.Optional<java.lang.invoke.MethodHandle>> LEGACY_LIVE_STATS = new ClassValue<>() {
        @Override
        protected java.util.Optional<java.lang.invoke.MethodHandle> computeValue(Class<?> type) {
            try {
                return java.util.Optional.of(java.lang.invoke.MethodHandles.privateLookupIn(type, java.lang.invoke.MethodHandles.lookup())
                    .findVirtual(type, "method_21018", java.lang.invoke.MethodType.methodType(class_4882.class)));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                return java.util.Optional.empty();
            }
        }
    };

    private static java.lang.invoke.MethodHandle legacyLiveStats(Class<?> type) {
        return LEGACY_LIVE_STATS.get(type).orElse(null);
    }

    /** 1.20.6's own getLiveStats() body (a subclass's super call): its request and parse */
    public static class_4882 superGetLiveStats(class_4341 client) throws class_4355 {
        CbRealmsClientInvoker invoker = (CbRealmsClientInvoker) client;
        String url = invoker.cb$url("activities/liveplayerlist");
        String json = invoker.cb$execute(class_4346.method_21040(url));
        return class_4882.method_25075(json);
    }

    // ------------------------------------------------------------------------------------------------ RealmsConfigureWorldScreen

    /** a 1.20.6 configure screen's own openTheWorld(join, lastScreen) / closeTheWorld(lastScreen) */
    public interface LegacyConfigure {
        void method_21218(boolean join, class_437 lastScreen);

        void method_21217(class_437 lastScreen);
    }

    /**
     * 1.21's openTheWorld(join) of a 1.20.6 configure screen: its own openTheWorld(join, lastScreen) with the screen
     * 1.21 returns to -- a fresh configure screen of the same server (getNewScreen()), what 1.21's own method shows.
     */
    public static void openTheWorld(LegacyConfigure self, boolean join) {
        self.method_21218(join, ((class_4388) self).method_21219());
    }

    /** 1.21's closeTheWorld() of a 1.20.6 configure screen: its own closeTheWorld(lastScreen) with getNewScreen() */
    public static void closeTheWorld(LegacyConfigure self) {
        self.method_21217(((class_4388) self).method_21219());
    }
}
