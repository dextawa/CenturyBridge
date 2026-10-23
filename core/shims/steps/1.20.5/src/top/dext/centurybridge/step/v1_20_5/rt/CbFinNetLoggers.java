package top.dext.centurybridge.step.v1_20_5.rt;

import top.dext.centurybridge.step.v1_20_5.facade.SampleLogger;

/**
 * 1.20.4's SampleLogger API on 1.20.5's sample loggers (area s4fin, util/debugchart and network).
 *
 * <p>A 1.20.4 SampleLogger-typed value is a 1.20.5 sample logger (the class_8743 interface): one this
 * layer made (facade/SampleLogger, a LocalSampleLogger), a 1.20.4 subclass of it, or a LocalSampleLogger of
 * vanilla's (DebugScreenOverlay's ping and bandwidth loggers). 1.20.4's ring methods answer from its
 * SampleStorage; where 1.20.4 took the logger, 1.20.5 takes a LocalSampleLogger, which every such value is.
 * A sample logger of 1.20.5's that keeps no samples here (RemoteSampleLogger, which only forwards to
 * subscribers) never reaches 1.20.4 code through a 1.20.4 member.
 */
public final class CbFinNetLoggers {

    private CbFinNetLoggers() {
    }

    /** new SampleLogger() */
    public static net.minecraft.class_8743 create() {
        return new SampleLogger();
    }

    private static net.minecraft.class_9194 storage(net.minecraft.class_8743 logger) {
        if (logger instanceof net.minecraft.class_9194 storage) {
            return storage;
        }
        throw new UnsupportedOperationException("1.20.4 SampleLogger ring methods on a 1.20.5 sample logger that keeps no samples: "
            + logger.getClass().getName());
    }

    /** a 1.20.4 logger where 1.20.5 takes a LocalSampleLogger */
    public static net.minecraft.class_9191 logger(net.minecraft.class_8743 logger) {
        if (logger == null || logger instanceof net.minecraft.class_9191) {
            return (net.minecraft.class_9191) logger;
        }
        throw new IllegalArgumentException("a 1.20.4 SampleLogger is a LocalSampleLogger; got " + logger.getClass().getName());
    }

    /** SampleLogger.capacity() */
    public static int method_53064(net.minecraft.class_8743 logger) {
        return logger instanceof SampleLogger s ? s.method_53064() : storage(logger).method_56662();
    }

    /** SampleLogger.size() */
    public static int method_53067(net.minecraft.class_8743 logger) {
        return logger instanceof SampleLogger s ? s.method_53067() : storage(logger).method_56663();
    }

    /** SampleLogger.get(index) */
    public static long method_53065(net.minecraft.class_8743 logger, int index) {
        return logger instanceof SampleLogger s ? s.method_53065(index) : storage(logger).method_56659(index);
    }

    /** SampleLogger.reset() */
    public static void method_53069(net.minecraft.class_8743 logger) {
        if (logger instanceof SampleLogger s) {
            s.method_53069();
        } else {
            storage(logger).method_56664();
        }
    }
}
