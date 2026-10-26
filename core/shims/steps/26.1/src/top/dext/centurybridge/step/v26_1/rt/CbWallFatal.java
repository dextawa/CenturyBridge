package top.dext.centurybridge.step.v26_1.rt;

import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.util.thread.BlockableEventLoop;

/**
 * 1.21.11's MinecraftServer fatal exception (MinecraftServer.field_51917, a static AtomicReference): chunk generation
 * (GenerationChunkHolder) set the first one, and the server's blocking waits (ServerChunkCache's main-thread executor
 * managedBlock) threw it. 26.1 keeps the same thing as BlockableEventLoop's delayed crash: GenerationChunkHolder hands
 * the crash report to relayDelayCrash (the first kept, later ones added to it as suppressed), and the event loops
 * throw it as a ReportedException (javap of both).
 */
public final class CbWallFatal {

    private CbWallFatal() {
    }

    /** 1.21.11 MinecraftServer.setFatalException(e) (method_60582): keeps the first */
    public static void setFatalException(RuntimeException e) {
        BlockableEventLoop.relayDelayCrash(e instanceof ReportedException reported ? reported.getReport()
            : CrashReport.forThrowable(e, "Fatal exception"));
    }

    /** 1.21.11 MinecraftServer.throwIfFatalException() (method_60584): throws the kept one, else true */
    public static boolean throwIfFatalException() {
        Supplier<CrashReport> crash = BlockableEventLoop.delayedCrash;
        if (crash != null) {
            throw new ReportedException(crash.get());
        }
        return true;
    }
}
