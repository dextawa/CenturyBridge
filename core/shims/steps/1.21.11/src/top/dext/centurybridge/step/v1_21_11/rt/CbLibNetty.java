package top.dext.centurybridge.step.v1_21_11.rt;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import io.netty.channel.EventLoop;
import io.netty.channel.IoHandler;
import io.netty.channel.SingleThreadIoEventLoop;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.nio.NioEventLoop;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * netty 4.1.118 (1.21.10) -> 4.2.7 (1.21.11) and JNA 5.15 -> 5.17 members a mod can reach that changed (the library front of
 * this step, core/segments/damage-lib/1.21.10__1.21.11.tsv), with the 1.21.10 versions' semantics read off their bytecode.
 * <ul>
 * <li>AbstractNioChannel.eventLoop() answered (NioEventLoop) super.eventLoop(); 4.2 drops the covariant override. The same
 * cast: a channel on a NioEventLoopGroup (4.2 still has it; 1.21.11's management server uses one) answers its loop. Vanilla
 * 1.21.11's game connections run on a MultiThreadIoEventLoopGroup with NioIoHandler, whose loops are no NioEventLoop: there
 * the cast fails exactly as 4.1.118's own cast would have on such a loop (reported, not hidden).</li>
 * <li>EpollEventLoop.openFileDescriptors / closeFileDescriptors opened and closed the loop's epoll, event and timer
 * descriptors; in 4.2 the loop's EpollIoHandler holds them and has the same two methods.</li>
 * <li>JNA 5.17 WinUser / User32 declare RegisterPowerSettingNotification / UnregisterPowerSettingNotification abstract: a
 * 5.15-era implementation reaches the native functions through JNA's own User32 binding.</li>
 * </ul>
 */
public final class CbLibNetty {
    private CbLibNetty() {
    }

    private static final MethodHandle IO_HANDLER;

    static {
        try {
            IO_HANDLER = MethodHandles.privateLookupIn(SingleThreadIoEventLoop.class, MethodHandles.lookup())
                .findVirtual(SingleThreadIoEventLoop.class, "ioHandler", MethodType.methodType(IoHandler.class));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /** AbstractNioChannel.eventLoop()'s 4.1.118 answer: the channel's loop, cast as 4.1.118 cast it */
    public static NioEventLoop nioEventLoop(EventLoop loop) {
        return (NioEventLoop) loop;
    }

    private static EpollIoHandler handler(EpollEventLoop loop) {
        try {
            return (EpollIoHandler) (IoHandler) IO_HANDLER.invoke((SingleThreadIoEventLoop) loop);
        } catch (Throwable t) {
            throw new IllegalStateException("EpollEventLoop's io handler", t);
        }
    }

    public static void openFileDescriptors(EpollEventLoop loop) {
        handler(loop).openFileDescriptors();
    }

    public static void closeFileDescriptors(EpollEventLoop loop) {
        handler(loop).closeFileDescriptors();
    }

    public static WinUser.HPOWERNOTIFY RegisterPowerSettingNotification(WinUser self, WinNT.HANDLE recipient, Guid.GUID setting, int flags) {
        return User32.INSTANCE.RegisterPowerSettingNotification(recipient, setting, flags);
    }

    public static WinDef.BOOL UnregisterPowerSettingNotification(WinUser self, WinUser.HPOWERNOTIFY handle) {
        return User32.INSTANCE.UnregisterPowerSettingNotification(handle);
    }

    public static WinUser.HPOWERNOTIFY RegisterPowerSettingNotification(User32 self, WinNT.HANDLE recipient, Guid.GUID setting, int flags) {
        return RegisterPowerSettingNotification((WinUser) self, recipient, setting, flags);
    }

    public static WinDef.BOOL UnregisterPowerSettingNotification(User32 self, WinUser.HPOWERNOTIFY handle) {
        return UnregisterPowerSettingNotification((WinUser) self, handle);
    }
}
