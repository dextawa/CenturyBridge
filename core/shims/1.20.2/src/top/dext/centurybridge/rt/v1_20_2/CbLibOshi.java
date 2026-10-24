package top.dext.centurybridge.rt.v1_20_2;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import java.util.Collections;
import java.util.List;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.jna.platform.unix.AixLibc;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.jna.platform.unix.OpenBsdLibc;
import oshi.jna.platform.unix.SolarisLibc;
import oshi.software.os.FileSystem;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.software.os.OperatingSystem;
import oshi.software.os.linux.LinuxOSProcess;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.software.os.mac.MacOSProcess;
import oshi.software.os.mac.MacOperatingSystem;
import oshi.software.os.unix.aix.AixOSProcess;
import oshi.software.os.unix.aix.AixOperatingSystem;
import oshi.software.os.unix.freebsd.FreeBsdOSProcess;
import oshi.software.os.unix.freebsd.FreeBsdOperatingSystem;
import oshi.software.os.unix.openbsd.OpenBsdOSProcess;
import oshi.software.os.unix.openbsd.OpenBsdOperatingSystem;
import oshi.software.os.unix.solaris.SolarisOSProcess;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.util.platform.windows.WmiQueryHandler;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

/**
 * OSHI 6.2.2 (1.20.1) -> 6.4.5 (1.20.2) members a mod can reach that changed, with 6.2.2's semantics read off its bytecode:
 * the library front of step 1 (core/segments/damage-lib/1.20.1__1.20.2.tsv).
 * <ul>
 * <li>Processor counts (LogicalProcessorInformation, initProcessorCounts) carry a third list, the processor caches,
 * 6.2.2 did not read: a 6.2.2 answer has none (empty), a 6.4.5 answer read by 6.2.2-era code drops it.</li>
 * <li>The platform OSProcess constructors take their OperatingSystem: 6.2.2 read the same facts (page size, boot time,
 * process id) from the platform's statics, so it is that platform's operating system.</li>
 * <li>WmiQueryHandler: its constructor and two fields became protected / private; a plain handler, and the same state
 * through 6.4.5's accessors.</li>
 * <li>Methods 6.4.5's interfaces newly demand (thread id, current thread, processor caches, open-file limits, libc thread
 * ids) answer, for a 6.2.2-era implementation, what the machine's own OSHI objects / libc bindings answer: they describe
 * the running system or process, which a 6.2.2 implementation did not report.</li>
 * </ul>
 */
public final class CbLibOshi {
    private CbLibOshi() {
    }

    private static OperatingSystem os() {
        return new SystemInfo().getOperatingSystem();
    }

    /** a 6.4.5 Triplet (logical, physical, caches) read by 6.2.2-era code as its Pair */
    public static Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> withoutCaches(
            Triplet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>> t) {
        return new Pair<>(t.getA(), t.getB());
    }

    /** a 6.2.2 Pair answer where 6.4.5 asks the Triplet: no cache information */
    public static Triplet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>> withCaches(
            Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> p) {
        return new Triplet<>(p.getA(), p.getB(), Collections.emptyList());
    }

    // one operating system per platform, made when first asked (6.2.2's processes read the same facts from statics)
    private static final class Linux { static final LinuxOperatingSystem OS = new LinuxOperatingSystem(); }
    private static final class Mac { static final MacOperatingSystem OS = new MacOperatingSystem(); }
    private static final class FreeBsd { static final FreeBsdOperatingSystem OS = new FreeBsdOperatingSystem(); }
    private static final class OpenBsd { static final OpenBsdOperatingSystem OS = new OpenBsdOperatingSystem(); }
    private static final class Solaris { static final SolarisOperatingSystem OS = new SolarisOperatingSystem(); }
    private static final class Aix { static final AixOperatingSystem OS = new AixOperatingSystem(); }

    public static LinuxOperatingSystem linuxOs() {
        return Linux.OS;
    }

    public static MacOperatingSystem macOs() {
        return Mac.OS;
    }

    public static FreeBsdOperatingSystem freeBsdOs() {
        return FreeBsd.OS;
    }

    public static OpenBsdOperatingSystem openBsdOs() {
        return OpenBsd.OS;
    }

    public static SolarisOperatingSystem solarisOs() {
        return Solaris.OS;
    }

    public static AixOperatingSystem aixOs() {
        return Aix.OS;
    }

    // new <Platform>OSProcess(6.2.2 arguments): the same process, of its platform's operating system

    public static LinuxOSProcess linuxOSProcess(int pid) {
        return new LinuxOSProcess(pid, Linux.OS);
    }

    public static MacOSProcess macOSProcess(int pid, int major, int minor) {
        return new MacOSProcess(pid, major, minor, Mac.OS);
    }

    public static FreeBsdOSProcess freeBsdOSProcess(int pid, @SuppressWarnings({"rawtypes", "unchecked"}) java.util.Map psMap) {
        // the key type is FreeBsdOperatingSystem's package-private PsKeywords
        return new FreeBsdOSProcess(pid, psMap, FreeBsd.OS);
    }

    public static OpenBsdOSProcess openBsdOSProcess(int pid, @SuppressWarnings({"rawtypes", "unchecked"}) java.util.Map psMap) {
        // the key type is OpenBsdOperatingSystem's package-private PsKeywords
        return new OpenBsdOSProcess(pid, psMap, OpenBsd.OS);
    }

    public static SolarisOSProcess solarisOSProcess(int pid) {
        return new SolarisOSProcess(pid, Solaris.OS);
    }

    public static AixOSProcess aixOSProcess(int pid, Pair<Long, Long> cpuMillis,
            java.util.function.Supplier<com.sun.jna.platform.unix.aix.Perfstat.perfstat_process_t[]> procCpu) {
        return new AixOSProcess(pid, cpuMillis, procCpu, Aix.OS);
    }

    /** new WmiQueryHandler(): a plain handler (6.4.5's constructor is protected) */
    public static WmiQueryHandler wmiQueryHandler() {
        return new WmiQueryHandler() {
        };
    }

    /** WmiQueryHandler.wmiTimeout (a subclass's read): the same state through 6.4.5's accessor */
    public static int wmiTimeout(WmiQueryHandler handler) {
        return handler.getWmiTimeout();
    }

    /** WmiQueryHandler.wmiTimeout (a subclass's write) */
    public static void wmiTimeout(WmiQueryHandler handler, int timeout) {
        handler.setWmiTimeout(timeout);
    }

    private static final java.lang.reflect.Field FAILED_WMI_CLASS_NAMES;

    static {
        try {
            FAILED_WMI_CLASS_NAMES = WmiQueryHandler.class.getDeclaredField("failedWmiClassNames");
            FAILED_WMI_CLASS_NAMES.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * WmiQueryHandler.failedWmiClassNames (a subclass's read): the same live set, which 6.4.5 keeps private and
     * exposes through no accessor (a library class: no access widener reaches it)
     */
    @SuppressWarnings("unchecked")
    public static java.util.Set<String> failedWmiClassNames(WmiQueryHandler handler) {
        try {
            return (java.util.Set<String>) FAILED_WMI_CLASS_NAMES.get(handler);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("OSHI WmiQueryHandler.failedWmiClassNames", e);
        }
    }

    // ------------------------------------------------------------------ methods 6.4.5's interfaces demand

    public static List<CentralProcessor.ProcessorCache> getProcessorCaches(CentralProcessor self) {
        return new SystemInfo().getHardware().getProcessor().getProcessorCaches();
    }

    public static int getThreadId(OperatingSystem self) {
        return os().getThreadId();
    }

    public static OSThread getCurrentThread(OperatingSystem self) {
        return os().getCurrentThread();
    }

    public static long getMaxFileDescriptorsPerProcess(FileSystem self) {
        return os().getFileSystem().getMaxFileDescriptorsPerProcess();
    }

    /** the process's own limit as the system reports it; -1 (OSHI's "unavailable") when the process is not running */
    public static long getHardOpenFileLimit(OSProcess self) {
        OSProcess p = os().getProcess(self.getProcessID());
        return p == null ? -1L : p.getHardOpenFileLimit();
    }

    public static long getSoftOpenFileLimit(OSProcess self) {
        OSProcess p = os().getProcess(self.getProcessID());
        return p == null ? -1L : p.getSoftOpenFileLimit();
    }

    public static int gettid(LinuxLibc self) {
        return LinuxLibc.INSTANCE.gettid();
    }

    public static NativeLong syscall(LinuxLibc self, NativeLong number, Object[] args) {
        return LinuxLibc.INSTANCE.syscall(number, args);
    }

    public static int thread_self(AixLibc self) {
        return AixLibc.INSTANCE.thread_self();
    }

    public static int thr_self(FreeBsdLibc self, NativeLongByReference tid) {
        return FreeBsdLibc.INSTANCE.thr_self(tid);
    }

    public static int getthrid(OpenBsdLibc self) {
        return OpenBsdLibc.INSTANCE.getthrid();
    }

    public static int thr_self(SolarisLibc self) {
        return SolarisLibc.INSTANCE.thr_self();
    }
}
