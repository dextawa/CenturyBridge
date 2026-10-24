package top.dext.centurybridge.step.v1_21_4.rt;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import oshi.hardware.CentralProcessor;
import oshi.util.FileUtil;
import oshi.util.tuples.Quartet;
import oshi.util.tuples.Triplet;

/**
 * The libraries' 1.21.3 -> 1.21.4 members a mod can reach that changed or went (core/segments/damage-lib/1.21.3__1.21.4.tsv;
 * rows in packages the library does not export are ledgered by lib-internal-gen.json), with the 1.21.3 versions' semantics
 * read off their bytecode.
 * <ul>
 * <li>JOML 1.10.5 -> 1.10.8: Vector4i / Vector4ic dot and distanceSquared answer long; 1.10.5 computed them in int, which
 * is the low 32 bits of 1.10.8's long ({@link #lowInt}, and {@link #widen} for an old override vanilla now asks).</li>
 * <li>OSHI 6.4.10 -> 6.6.5: FileUtil.readAllBytes(file) reported errors (readAllBytes(file, true)); Constants.SYSFS_SERIAL_PATH
 * was "/sys/devices/virtual/dmi/id/"; AbstractCentralProcessor.initProcessorCounts answers a Quartet whose fourth list, the
 * CPU feature flags, 6.4.10 did not have (empty).</li>
 * <li>ICU 73.2 -> 76.1 (client-only library): {@link CbLibIcu}.</li>
 * <li>log4j-core 2.22.1 -> 2.24.1: JsonConstants() became private; the class holds only static constants.</li>
 * </ul>
 */
public final class CbLibMisc {
    private CbLibMisc() {
    }

    /** Vector4i(c).dot / distanceSquared: 1.10.5's int arithmetic, the low 32 bits of 1.10.8's long */
    public static int lowInt(long value) {
        return (int) value;
    }

    /** an old override's int answer where 1.10.8 asks a long */
    public static long widen(int value) {
        return value;
    }

    /** FileUtil.readAllBytes(file) */
    public static byte[] readAllBytes(String file) {
        return FileUtil.readAllBytes(file, true);
    }

    /** Constants.SYSFS_SERIAL_PATH */
    public static String SYSFS_SERIAL_PATH() {
        return "/sys/devices/virtual/dmi/id/";
    }

    /** an old initProcessorCounts() answer where 6.6.5 asks a Quartet: no feature flags */
    public static Quartet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>,
            List<CentralProcessor.ProcessorCache>, List<String>> withFeatureFlags(Triplet<List<CentralProcessor.LogicalProcessor>,
            List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>> counts) {
        return new Quartet<>(counts.getA(), counts.getB(), counts.getC(), Collections.emptyList());
    }

    /** a 6.6.5 initProcessorCounts() answer where 1.21.3-era code (a subclass's super call) takes a Triplet */
    public static Triplet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>,
            List<CentralProcessor.ProcessorCache>> withoutFeatureFlags(Quartet<List<CentralProcessor.LogicalProcessor>,
            List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>, List<String>> counts) {
        return new Triplet<>(counts.getA(), counts.getB(), counts.getC());
    }

    /** new PhysicalMemory(bankLabel, capacity, clockSpeed, manufacturer, memoryType) */
    public static oshi.hardware.PhysicalMemory physicalMemory(String bankLabel, long capacity, long clockSpeed, String manufacturer,
            String memoryType) {
        return new CbLibPhysicalMemory(bankLabel, capacity, clockSpeed, manufacturer, memoryType);
    }

    /** new JsonConstants(): the class's only constructor, private since 2.24 (an instance holds nothing) */
    public static org.apache.logging.log4j.core.jackson.JsonConstants jsonConstants() {
        try {
            Constructor<org.apache.logging.log4j.core.jackson.JsonConstants> c =
                org.apache.logging.log4j.core.jackson.JsonConstants.class.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("log4j-core's JsonConstants constructor", e);
        }
    }
}
