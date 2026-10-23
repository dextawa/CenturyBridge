package top.dext.centurybridge.step.v1_21.rt;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_3176;
import net.minecraft.class_3218;
import net.minecraft.class_9240;
import net.minecraft.class_9813;
import net.minecraft.server.MinecraftServer;

/**
 * The server and its reports: MinecraftServer's file methods (File in 1.20.6, Path in 1.21), the nether switch
 * (isNetherEnabled() became isLevelEnabled(Level)), the chunk I/O failure hooks, PlayerList.respawn's removal
 * reason and CrashReport's report text and file. Wired from s6world.json.
 */
public final class CbWorldServer {

    // ------------------------------------------------------------------------------------------------ files
    /**
     * A 1.21 server path as the File 1.20.6 answered. 1.20.6's MinecraftServer.getServerDirectory() was new
     * File(".") and getFile(name) new File(getServerDirectory(), name); 1.21's are Path.of("") and
     * Path.of("").resolve(name). An empty path is ".", and a bare relative name is "./name" -- so that
     * getParentFile() still answers the server directory and new File(dir, child) stays relative (a File
     * built on an empty parent resolves against the file-system root). Any other path (IntegratedServer's
     * game directory, an absolute path) is the same file.
     */
    public static File file(Path path) {
        if (path == null) {
            return null;
        }
        if (path.toString().isEmpty()) {
            return new File(".");
        }
        if (!path.isAbsolute() && path.getParent() == null) {
            return new File(new File("."), path.toString());
        }
        return path.toFile();
    }

    /** a 1.20.6 override's File, as the Path 1.21 asks for (null stays null) */
    public static Path path(File file) {
        return file == null ? null : file.toPath();
    }

    // ------------------------------------------------------------------------------------------------ nether
    /** the 1.20.6 isNetherEnabled() a server subclass of a 1.20.6 mod overrides */
    public interface LegacyNether {
        boolean method_3839();
    }

    /**
     * 1.20.6 server.isNetherEnabled(): a 1.20.6 subclass's own override answers; else what 1.21 answers for
     * the nether level -- the dedicated server's allow-nether property (DedicatedServer.isLevelEnabled reads
     * it for the nether only), true on any other server (MinecraftServer's own answer in both releases).
     */
    public static boolean isNetherEnabled(MinecraftServer server) {
        if (server instanceof LegacyNether legacy) {
            return legacy.method_3839();
        }
        return netherAnswer(server);
    }

    private static boolean netherAnswer(MinecraftServer server) {
        if (server instanceof class_3176 dedicated) {
            return dedicated.method_16705().field_16811;
        }
        class_3218 nether = server.method_3847(class_1937.field_25180);
        return nether == null || server.method_60671(nether);
    }

    /** a 1.20.6 mod's super.isNetherEnabled() in a MinecraftServer subclass: MinecraftServer's own 1.20.6 body */
    public static boolean superIsNetherEnabled(MinecraftServer server) {
        return true;
    }

    /** a 1.20.6 mod's super.isNetherEnabled() in a DedicatedServer subclass: DedicatedServer's 1.20.6 body */
    public static boolean superIsNetherEnabled(class_3176 server) {
        return server.method_16705().field_16811;
    }

    /**
     * 1.21's isLevelEnabled(level) on a 1.20.6 server subclass that overrides isNetherEnabled(): the nether is
     * the subclass's answer, as 1.20.6 asked isNetherEnabled() only for the nether portal; any other level
     * goes to the vanilla answer above the subclass (the guard sends the call back down to super's shape).
     */
    public static boolean isLevelEnabled(LegacyNether self, class_1937 level) {
        if (level != null && level.method_27983() == class_1937.field_25180) {
            return self.method_3839();
        }
        return ((MinecraftServer) self).method_60671(level);
    }

    // ------------------------------------------------------------------------------------------------ chunk I/O reports
    /**
     * The failure a 1.20.6 caller reports without one. 1.20.6's MinecraftServer.reportChunkLoadFailure(pos) and
     * reportChunkSaveFailure(pos) were empty (ChunkMap logged the error itself); 1.21's take the exception and
     * the region storage, log them and queue a crash report. A report carrying this sentinel -- a 1.20.6
     * caller's, or a 1.20.6 override's super call with no 1.21 report behind it -- is 1.20.6's empty body
     * (CbWorldServerMixin, CbWorldServerClientMixin); 1.21 code never passes it.
     */
    public static final Throwable NO_FAILURE = new NoFailure();

    private static final class NoFailure extends RuntimeException {
        NoFailure() {
            super("no chunk I/O failure: reported through the 1.20.6 shape", null, false, false);
        }
    }

    public static Throwable noFailure() {
        return NO_FAILURE;
    }

    /** the storage of a sentinel report: never read, the sentinel returns first */
    public static class_9240 noStorage() {
        return null;
    }

    // ------------------------------------------------------------------------------------------------ PlayerList.respawn
    /** 1.20.6's PlayerList.respawn removed the old player with DISCARDED; 1.21 takes the reason */
    public static class_1297.class_5529 respawnRemovalReason() {
        return class_1297.class_5529.field_26999;
    }

    // ------------------------------------------------------------------------------------------------ CrashReport
    /** 1.20.6 had one kind of report, the crash report: its header is 1.21's ReportType.CRASH */
    public static class_9813 crashReportType() {
        return class_9813.field_52181;
    }

    /**
     * 1.20.6 CrashReport.getFriendlyReport(): 1.21's crash report text -- the ReportType.CRASH header (1.20.6's
     * "---- Minecraft Crash Report ----" and a witty comment), no extra lines -- asked virtually, so a 1.20.6
     * subclass's override answers through its hook.
     */
    public static String getFriendlyReport(net.minecraft.class_128 report) {
        return report.method_60921(class_9813.field_52181, List.of());
    }

    /** 1.20.6's report had no extra header lines */
    public static List<String> noExtraLines() {
        return List.of();
    }


    private CbWorldServer() {
    }
}
