package top.dext.centurybridge.step.v26_1.rt;

/**
 * 1.21.11's BlockableEventLoop(name) / ReentrantBlockableEventLoop(name): 26.1's take (name, propagatesCrashes).
 * With propagatesCrashes, 26.1's pollTask throws a crash another loop delayed (BlockableEventLoop.delayCrash /
 * relayDelayCrash, a static shared by every loop); 1.21.11's loop had no such field or check (javap class_1255: its
 * pollTask and managedBlock rethrow nothing they did not catch themselves). A 1.21.11 mod's own loop therefore does
 * not propagate, as 26.1's own non-server loops (ServerChunkCache.MainThreadExecutor, SoundEngineExecutor) pass false.
 */
public final class CbWallEventLoops {
    private CbWallEventLoops() {
    }

    /** the propagatesCrashes a 1.21.11 subclass's super(name) gets */
    public static boolean noCrashPropagation() {
        return false;
    }
}
