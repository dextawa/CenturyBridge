package top.dext.centurybridge.step.v1_21_11.rt;

import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.net.InetSocketAddress;
import java.util.function.Supplier;
import net.minecraft.class_12239;
import net.minecraft.class_2535;
import net.minecraft.class_9191;

/**
 * 1.21.10's network members 1.21.11 dropped when it moved the netty event loop groups into EventLoopGroupHolder
 * (class_12239). PendingInvite's fields (client-only): {@link CbLegacyRealms}.
 *
 * <p>Event loop groups: 1.21.10 kept one lazily created group per kind in public static Suppliers of
 * Connection / ServerConnectionListener (NioEventLoopGroup, EpollEventLoopGroup, DefaultEventLoopGroup). 1.21.11's
 * groups are netty 4.2 MultiThreadIoEventLoopGroups, no instance of those 1.21.10 classes, so they cannot be
 * handed out under the 1.21.10 types. These Suppliers create the 1.21.10 kind lazily, once, with 1.21.10's daemon
 * thread names -- what 1.21.10's own suppliers did. Vanilla's 1.21.11 connections run on EventLoopGroupHolder's
 * groups, not on these (release_notes_known: a mod's channel on Connection.NETWORK_WORKER_GROUP no longer shares
 * vanilla's threads).
 */
public final class CbLegacyNet {

    private static <T> Supplier<T> lazy(Supplier<T> s) {
        return Suppliers.memoize(s::get);
    }

    private static java.util.concurrent.ThreadFactory threads(String format) {
        return new ThreadFactoryBuilder().setNameFormat(format).setDaemon(true).build();
    }

    private static final Supplier<NioEventLoopGroup> CLIENT = lazy(() -> new NioEventLoopGroup(0, threads("Netty Client IO #%d")));
    private static final Supplier<EpollEventLoopGroup> CLIENT_EPOLL = lazy(() -> new EpollEventLoopGroup(0, threads("Netty Epoll Client IO #%d")));
    private static final Supplier<DefaultEventLoopGroup> CLIENT_LOCAL = lazy(() -> new DefaultEventLoopGroup(0, threads("Netty Local Client IO #%d")));
    private static final Supplier<NioEventLoopGroup> SERVER = lazy(() -> new NioEventLoopGroup(0, threads("Netty Server IO #%d")));
    private static final Supplier<EpollEventLoopGroup> SERVER_EPOLL = lazy(() -> new EpollEventLoopGroup(0, threads("Netty Epoll Server IO #%d")));

    /** Connection.NETWORK_WORKER_GROUP */
    public static Supplier<NioEventLoopGroup> field_11650() {
        return CLIENT;
    }

    /** Connection.NETWORK_EPOLL_WORKER_GROUP */
    public static Supplier<EpollEventLoopGroup> field_11657() {
        return CLIENT_EPOLL;
    }

    /** Connection.LOCAL_WORKER_GROUP */
    public static Supplier<DefaultEventLoopGroup> field_11649() {
        return CLIENT_LOCAL;
    }

    /** ServerConnectionListener.SERVER_EVENT_GROUP */
    public static Supplier<NioEventLoopGroup> field_14111() {
        return SERVER;
    }

    /** ServerConnectionListener.SERVER_EPOLL_EVENT_GROUP */
    public static Supplier<EpollEventLoopGroup> field_14105() {
        return SERVER_EPOLL;
    }

    /**
     * Connection.connectToServer(address, useEpoll, sampleLogger): 1.21.11 takes the event loop group holder instead of
     * the flag. Minecraft passes its "use native transport" option either way; 1.21.11 turns it into the holder with
     * EventLoopGroupHolder.remote(flag) (method_75867: epoll / kqueue when the flag is on and available, else nio),
     * where 1.21.10 used epoll when the flag was on and epoll available, else nio.
     */
    public static class_2535 method_10753(InetSocketAddress address, boolean useEpoll, class_9191 sampleLogger) {
        return class_2535.method_10753(address, class_12239.method_75867(useEpoll), sampleLogger);
    }

    private CbLegacyNet() {
    }
}
