package top.dext.centurybridge.rt.v1_20_2;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.minecraft.class_2535;
import net.minecraft.class_3242;
import net.minecraft.class_3528;

/**
 * The netty event loop groups of ClientConnection and ServerNetworkIo were Lazy<EventLoopGroup>
 * (class_3528) in 1.20.1 and are memoized Suppliers in 1.20.2. Each field below has the 1.20.1 name
 * and type and wraps 1.20.2's supplier, so get() starts and answers the very group vanilla uses --
 * nothing is created before a mod first asks, as in 1.20.1. A holder of its own: loading it touches
 * only these two classes.
 */
public final class CbNetGroups {

    /** ClientConnection.CLIENT_IO_GROUP */
    public static final class_3528<NioEventLoopGroup> field_11650 = new class_3528<>(class_2535.field_11650);
    /** ClientConnection.EPOLL_CLIENT_IO_GROUP */
    public static final class_3528<EpollEventLoopGroup> field_11657 = new class_3528<>(class_2535.field_11657);
    /** ClientConnection.LOCAL_CLIENT_IO_GROUP */
    public static final class_3528<DefaultEventLoopGroup> field_11649 = new class_3528<>(class_2535.field_11649);
    /** ServerNetworkIo.DEFAULT_CHANNEL */
    public static final class_3528<NioEventLoopGroup> field_14111 = new class_3528<>(class_3242.field_14111);
    /** ServerNetworkIo.EPOLL_CHANNEL */
    public static final class_3528<EpollEventLoopGroup> field_14105 = new class_3528<>(class_3242.field_14105);

    private CbNetGroups() {
    }
}
